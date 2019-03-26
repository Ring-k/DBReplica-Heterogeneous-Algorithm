package heterogeneous;

import constant.Constant;
import cost.CostModel;
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

public class HeterogeneousDemo {

  public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {


    DataTable dataTable = getDataTable(5);
    Query[] queries = getQueries(5);

    SimulateAnneal sa = new SimulateAnneal(dataTable, queries, 3);
    Constant.LOCAL_ITERATION_NUM = 12;
    MultiReplicas m = sa.optimal();
    System.out.println(m.getOrderString());
    System.out.println("cost: " + sa.getOptimalCost());
    System.out.println("cost: " + CostModel.cost(m, queries));
    System.out.println("history: " + sa.getHistory());


    writeHistory(sa.getHistory());

  }

  /**
   * Write the record history of optimal cost in a file for analytic work.
   */
  private static void writeHistory(List<Double> record) throws IOException {
    File file = new File("heterogeneous.record");
    if (file.exists()) file.delete();
    file.createNewFile();
    PrintWriter pw = new PrintWriter(file);
    for (double c : record)
      pw.println(c);
    pw.flush();
    pw.close();
  }

  static DataTable generateDataTable(int colNum, int rowNum, int group) throws NoSuchAlgorithmException {
    Random rand = SecureRandom.getInstanceStrong();
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

  static Query[] getQueries(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data\\queries_" + colNums));
    return (Query[]) ois.readObject();
  }

  static DataTable getDataTable(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data\\data_table_" + colNums));
    return (DataTable) ois.readObject();
  }

  static DataTable generateDataTable() {
    List<Double> c0 = new ArrayList<>(),
            c1 = new ArrayList<>(),
            c2 = new ArrayList<>();
    c0.add(1000.0);
    c0.add(1500.0);
    c0.add(4999.0);
    c0.add(4834.0);
    c0.add(4862.0);
    c0.add(2500.0);
    c0.add(2500.0);
    c0.add(2500.0);
    c0.add(3500.0);
    c0.add(3333.0);
    c1.add(2000.0);
    c1.add(9999.0);
    c1.add(8888.0);
    c1.add(9090.0);
    c1.add(9000.0);
    c1.add(6550.0);
    c1.add(6666.0);
    c1.add(6868.0);
    c1.add(4500.0);
    c1.add(4862.0);
    c2.add(5000.0);
    c2.add(8999.0);
    c2.add(6666.0);
    c2.add(6666.0);
    c2.add(6666.0);
    c2.add(6666.0);
    c2.add(6666.0);
    c2.add(6666.0);
    c2.add(7777.0);
    c2.add(7777.0);
    Histogram h0 = new Histogram(c0, 4);
    Histogram h1 = new Histogram(c1, 4);
    Histogram h2 = new Histogram(c2, 4);
    Histogram[] cols = {h0, h1, h2};
    DataTable dataTable = new DataTable(cols);
    return dataTable;
  }
}
