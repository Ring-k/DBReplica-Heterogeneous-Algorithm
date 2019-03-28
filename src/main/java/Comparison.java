import constant.Constant;
import cost.CostModel;
import datamodel.DataTable;
import datamodel.Histogram;
import divergentdesign.DivgDesign;
import heterogeneous.SimulateAnneal;
import query.Query;
import query.QueryGenerator;

import java.io.*;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Comparison {

  static void generateDataAndQueries(int min, int max) throws NoSuchAlgorithmException {
    for (int i = min; i <= max; i++) {
      int group = 100;
      int maxVal = 1000;
      int row = 10000;
      for (int j = 1; j <= i; j++) // row*((max/group/step)^n)
        row *= (maxVal / (group * Constant.histogramStep));

      DataTable dataTable = generateDataTable(i, row, group, maxVal);
      Query[] queries = new QueryGenerator(1000, dataTable).getQueries();
      ObjectOutputStream oos = null;
      try {
        oos = new ObjectOutputStream(new FileOutputStream("queries_" + i));
        oos.writeObject(queries);
        oos = new ObjectOutputStream(new FileOutputStream("data_table_" + i));
        oos.writeObject(dataTable);
        oos.flush();
        oos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }

  static DataTable generateDataTable(int colNum, int rowNum, int group, int maxVal) throws NoSuchAlgorithmException {
    Random rand = SecureRandom.getInstanceStrong();
    Histogram[] hs = new Histogram[colNum];
    for (int i = 0; i < colNum; i++) {
      List<Double> ls = new ArrayList<>();
      for (int j = 0; j < 10000; j++)
        ls.add(rand.nextDouble() * maxVal);
      hs[i] = new Histogram(ls, group);
    }
    Constant.ROW_NUM = BigDecimal.valueOf(rowNum);
    return new DataTable(hs);
  }

  static Query[] getQueries(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("queries_" + colNums));
    return (Query[]) ois.readObject();
  }

  static DataTable getDataTable(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data_table_" + colNums));
    return (DataTable) ois.readObject();
  }

  static void writeSolutions(String outPutPath, int minCol, int maxCol) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    File f = new File("solutions.txt");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    for (int i = minCol; i <= maxCol; i++) {
      DataTable dataTable = getDataTable(i);
      Query[] queries = getQueries(i);

      // method 0
      SimulateAnneal sa = new SimulateAnneal(dataTable, queries, 3);
      String saString = sa.optimal().getOrderString();
      fw.write(saString + "0" + "\n");

      // method 1, dd loading factor = 1
      DivgDesign dd = new DivgDesign(dataTable, queries, 3, 1, 1000, 0.001);
      String ddString = dd.optimal().getOrderString();
      fw.write(ddString + "1" + "\n");

      // method 2, dd loading factor = 2
      dd = new DivgDesign(dataTable, queries, 3, 2, 1000, 0.001);
      ddString = dd.optimal().getOrderString();
      fw.write(ddString + "2" + "\n");

      // method 3 dd loading factor = 3
      dd = new DivgDesign(dataTable, queries, 3, 3, 1000, 0.001);
      ddString = dd.optimal().getOrderString();
      fw.write(ddString + "3" + "\n");
    }
    fw.close();
  }

  static void compare(String outPutPath, int minCol, int maxCol) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    File f = new File(outPutPath);
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    for (int i = minCol; i <= maxCol; i++) {
      DataTable dataTable = getDataTable(i);
      Query[] queries = getQueries(i);

      // method 0
      SimulateAnneal sa = new SimulateAnneal(dataTable, queries, 3);
      double cost = CostModel.cost(sa.optimal(),queries).doubleValue();
      String line = "" + i + "," + cost;

      // method 1, dd loading factor = 1
      DivgDesign dd = new DivgDesign(dataTable, queries, 3, 1, 1000, 0.001);
      cost = CostModel.cost(dd.optimal(), queries).doubleValue();
      line += "," + cost;

      // method 2, dd loading factor = 2
      dd = new DivgDesign(dataTable, queries, 3, 2, 1000, 0.001);
      cost = CostModel.cost(dd.optimal(), queries).doubleValue();
      line += "," + cost;

      // method 3 dd loading factor = 3
      dd = new DivgDesign(dataTable, queries, 3, 3, 1000, 0.001);
      cost = CostModel.cost(dd.optimal(), queries).doubleValue();
      line += "," + cost + "\n";
      fw.write(line);
    }
    fw.close();
  }

  public static void main(String args[]) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
    // generate and serialize data
//    int minCol = Integer.parseInt(args[0]);
//    int maxCol = Integer.parseInt(args[1]);
//    generateDataAndQueries(minCol, maxCol);
//    writeSolutions("compare\\solutions.txt", 1, 10);
    compare("comp.csv", 7, 7);
  }
}
