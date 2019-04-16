import constant.Constant;
import cost.CostModel;
import datamodel.DataTable;
import datamodel.Histogram;
import divergentdesign.DivgDesign;
import genetic.Genetic;
import heterogeneous.SimulateAnneal;
import query.Query;
import query.QueryGenerator;
import replica.MultiReplicas;
import replica.Replica;
import rita.Rita;
import searchall.SearchAll;

import java.io.*;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Exp1 {

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
//
//  static void writeSolutions(String outPutPath, int minCol, int maxCol) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
//    File f = new File("solutions.txt");
//    if (!f.exists()) f.createNewFile();
//    FileWriter fw = new FileWriter(f, true);
//
//    for (int i = minCol; i <= maxCol; i++) {
//      DataTable dataTable = getDataTable(i);
//      Query[] queries = getQueries(i);
//
//      // method 0
//      SimulateAnneal sa = new SimulateAnneal(dataTable, queries, 3);
//      String saString = sa.optimal().getOrderString();
//      fw.write(saString + "0" + "\n");
//
//      // method 1, dd loading factor = 1
//      DivgDesign dd = new DivgDesign(dataTable, queries, 3, 1, 1000, 0.001);
//      String ddString = dd.optimal().getOrderString();
//      fw.write(ddString + "1" + "\n");
//
//      // method 2, dd loading factor = 2
//      dd = new DivgDesign(dataTable, queries, 3, 2, 1000, 0.001);
//      ddString = dd.optimal().getOrderString();
//      fw.write(ddString + "2" + "\n");
//
//      // method 3 dd loading factor = 3
//      dd = new DivgDesign(dataTable, queries, 3, 3, 1000, 0.001);
//      ddString = dd.optimal().getOrderString();
//      fw.write(ddString + "3" + "\n");
//    }
//    fw.close();
//  }

  static void compare(String outPutPath, int minCol, int maxCol) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    File f = new File(outPutPath);
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    Constant.REPLICA_NUMBER = 3;
    Constant.MAX_ITERATION = 10;
    Constant.EPSILON = 0.00000001;

    Constant.LOCAL_ITERATION_NUM = 30;
    Constant.OPTIMAL_COUNT_THRESHOLD = 30;

    for (int i = minCol; i <= maxCol; i++) {
      DataTable dataTable = getDataTable(i);
      Query[] queries = getQueries(i);

      Replica replica = new SearchAll(dataTable, queries).optimalReplica();

      SimulateAnneal sa = new SimulateAnneal(dataTable, queries, 3, true).initSolution(replica);
      double cost = CostModel.cost(sa.optimal(), queries).doubleValue();
      String line = "SA: " + i + "," + cost + "\n";
      String record = "" + cost;

      DivgDesign dd = new DivgDesign(dataTable, queries, 3, 1, 1000, 0.0000000000000000000001, false);
      cost = CostModel.cost(dd.optimal(), queries).doubleValue();
      line += "Divergent Design, load balance = 1, isNewMethod = false, " + cost + "\n";
      record += ("," + cost);

      dd = new DivgDesign(dataTable, queries, 3, 2, 1000, 0.0000000000000000000001, false);
      cost = CostModel.cost(dd.optimal(), queries).doubleValue();
      line += "Divergent Design, load balance = 2, isNewMethod = false, " + cost + "\n";
      record += ("," + cost);

      dd = new DivgDesign(dataTable, queries, 3, 3, 1000, 0.0000000000000000000001, false);
      cost = CostModel.cost(dd.optimal(), queries).doubleValue();
      line += "Divergent Design, load balance = 3, isNewMethod = false, " + cost + "\n";
      record += ("," + cost);

      dd = new DivgDesign(dataTable, queries, 3, 1, 1000, 0.0000000000000000000001, true);
      cost = CostModel.cost(dd.optimal(), queries).doubleValue();
      line += "Divergent Design, load balance = 1, isNewMethod = true, " + cost + "\n";
      record += ("," + cost);

      dd = new DivgDesign(dataTable, queries, 3, 2, 1000, 0.0000000000000000000001, true);
      cost = CostModel.cost(dd.optimal(), queries).doubleValue();
      line += "Divergent Design, load balance = 2, isNewMethod = true, " + cost + "\n";
      record += ("," + cost);

      dd = new DivgDesign(dataTable, queries, 3, 3, 1000, 0.0000000000000000000001, true);
      cost = CostModel.cost(dd.optimal(), queries).doubleValue();
      line += "Divergent Design, load balance = 3, isNewMethod = true, " + cost + "\n";
      record += ("," + cost);

      // Rita
      Rita rt = new Rita(dataTable, queries, 3, 1, 1, 0.5, false).initSolution(replica);
      cost = CostModel.cost(rt.optimal(), queries).doubleValue();
      line += "RITA, load balance = 1, candidate factor = 1, skew = 0.5, isNewMethod = false, " + cost + "\n";
      record += ("," + cost);

      rt = new Rita(dataTable, queries, 3, 1, 2, 0.5, false).initSolution(replica);
      cost = CostModel.cost(rt.optimal(), queries).doubleValue();
      line += "RITA, load balance = 1, candidate factor = 2, skew = 0.5, isNewMethod = false, " + cost + "\n";
      record += ("," + cost);

      rt = new Rita(dataTable, queries, 3, 1, 3, 0.5, false).initSolution(replica);
      cost = CostModel.cost(rt.optimal(), queries).doubleValue();
      line += "RITA, load balance = 1, candidate factor = 3, skew = 0.5, isNewMethod = false, " + cost + "\n";
      record += ("," + cost);

      rt = new Rita(dataTable, queries, 3, 2, 2, 0.5, false).initSolution(replica);
      cost = CostModel.cost(rt.optimal(), queries).doubleValue();
      line += "RITA, load balance = 2, candidate factor = 2, skew = 0.5, isNewMethod = false, " + cost + "\n";
      record += ("," + cost);

      rt = new Rita(dataTable, queries, 3, 2, 3, 0.5, false).initSolution(replica);
      cost = CostModel.cost(rt.optimal(), queries).doubleValue();
      line += "RITA, load balance = 2, candidate factor = 3, skew = 0.5, isNewMethod = false, " + cost + "\n";
      record += ("," + cost);

      rt = new Rita(dataTable, queries, 3, 3, 3, 0.5, false).initSolution(replica);
      cost = CostModel.cost(rt.optimal(), queries).doubleValue();
      line += "RITA, load balance = 3, candidate factor = 3, skew = 0.5, isNewMethod = false, " + cost + "\n";
      record += ("," + cost);

      rt = new Rita(dataTable, queries, 3, 1, 1, 0.5, true).initSolution(replica);
      cost = CostModel.cost(rt.optimal(), queries).doubleValue();
      line += "RITA, load balance = 1, candidate factor = 1, skew = 0.5, isNewMethod = true, " + cost + "\n";
      record += ("," + cost);

      rt = new Rita(dataTable, queries, 3, 1, 2, 0.5, true).initSolution(replica);
      cost = CostModel.cost(rt.optimal(), queries).doubleValue();
      line += "RITA, load balance = 1, candidate factor = 2, skew = 0.5, isNewMethod = true, " + cost + "\n";
      record += ("," + cost);

      rt = new Rita(dataTable, queries, 3, 1, 3, 0.5, true).initSolution(replica);
      cost = CostModel.cost(rt.optimal(), queries).doubleValue();
      line += "RITA, load balance = 1, candidate factor = 3, skew = 0.5, isNewMethod = true, " + cost + "\n";
      record += ("," + cost);

      rt = new Rita(dataTable, queries, 3, 2, 2, 0.5, true).initSolution(replica);
      cost = CostModel.cost(rt.optimal(), queries).doubleValue();
      line += "RITA, load balance = 2, candidate factor = 2, skew = 0.5, isNewMethod = true, " + cost + "\n";
      record += ("," + cost);

      rt = new Rita(dataTable, queries, 3, 2, 2, 0.5, true).initSolution(replica);
      cost = CostModel.cost(rt.optimal(), queries).doubleValue();
      line += "RITA, load balance = 2, candidate factor = 3, skew = 0.5, isNewMethod = true, " + cost + "\n";
      record += ("," + cost);

      rt = new Rita(dataTable, queries, 3, 3, 3, 0.5, true).initSolution(replica);
      cost = CostModel.cost(rt.optimal(), queries).doubleValue();
      line += "RITA, load balance = 3, candidate factor = 3, skew = 0.5, isNewMethod = true, " + cost + "\n";
      record += ("," + cost + "\n");


      Genetic g = new Genetic(dataTable, queries, 3, 100, 50, 20000, 0.8, 0.01, 1, true);
      MultiReplicas multiReplicas = g.optimal();

      System.out.println(line);
      fw.write(record);


    }
    fw.close();
  }

  public static void main(String args[]) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
    // generate and serialize data
//    int minCol = Integer.parseInt(args[0]);
//    int maxCol = Integer.parseInt(args[1]);
//    generateDataAndQueries(minCol, maxCol);
//    writeSolutions("compare\\solutions.txt", 1, 10);
    for (int i = 0; i < 1000; i++)
      compare("comp.csv", 7, 7);
  }
}
