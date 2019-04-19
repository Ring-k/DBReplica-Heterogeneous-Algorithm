package divergentdesign;

import datamodel.DataTable;
import datamodel.Histogram;
import query.Query;
import replica.MultiReplicas;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DivgDesignDemo {
  static Query[] getQueries(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("queries_" + colNums));
    return (Query[]) ois.readObject();
  }

  static DataTable getDataTable(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data_table_" + colNums));
    return (DataTable) ois.readObject();
  }

  public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    // to serialize data into file, see demo of heterogeneous

//    ObjectInputStream ois = null;
//    ois = new ObjectInputStream(new FileInputStream("queries"));
//    Query[] queries = (Query[]) ois.readObject();
//    ois = new ObjectInputStream(new FileInputStream("data_table"));
//    DataTable dataTable = (DataTable) ois.readObject();

    DataTable dataTable = getDataTable(7);
    Query[] queries = getQueries(7);

    DivergentDesign d = new DivergentDesign(dataTable, queries);
    MultiReplicas m = d.optimal();
    System.out.println(m.getOrderString());
    System.out.println("cost: " + d.getOptimalCost());
    System.out.println("history: " + d.getHistory());

//    writeHistory(d.getHistory());


  }

  /**
   * Write the record history of optimal cost in a file for analytic work.
   */
  private static void writeHistory(List<Double> record) throws IOException {
    File file = new File("divergent.record");
    if (file.exists()) file.delete();
    file.createNewFile();
    PrintWriter pw = new PrintWriter(file);
    for (double c : record)
      pw.println(c);
    pw.flush();
    pw.close();
  }

  static DataTable generateDataTable(int colNum, int rowNum, int group) throws NoSuchAlgorithmException {
//    Random rand = SecureRandom.getInstanceStrong();
    Random rand = new Random();
    Histogram[] hs = new Histogram[colNum];
    int c = 1;
    for(int i = 0; i < colNum; i++){
      List<Double> l = new ArrayList<>();
      for(int j = 0; j < rowNum; j++)
        l.add(rand.nextDouble()*c);
      c *= 10;
      hs[i] = new Histogram(l, group);
    }
    return new DataTable(hs);
  }


}
