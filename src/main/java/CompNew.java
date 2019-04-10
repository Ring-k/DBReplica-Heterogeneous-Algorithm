import cost.CostModel;
import datamodel.DataTable;
import divergentdesign.DivgDesign;
import genetic.Genetic;
import heterogeneous.SimulateAnneal;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;
import searchall.SearchAll;

import java.io.*;
import java.security.NoSuchAlgorithmException;

public class CompNew {

  static Query[] getQueries() throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data\\queries"));
//    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("queries"));
    return (Query[]) ois.readObject();
  }

  static DataTable getDataTable() throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data\\data_table"));
//    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data_table"));
    return (DataTable) ois.readObject();
  }

  public static void SAExp(DataTable dataTable, Query[] queries, boolean isNewMethod) throws IOException, NoSuchAlgorithmException {
//    File f = new File("simulateanneal.out");
    File f = new File("data\\out\\sa.out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    Replica replica = new SearchAll(dataTable, queries).optimalReplica();
    SimulateAnneal sa = new SimulateAnneal(dataTable, queries, 3, isNewMethod).initSolution(replica);
    MultiReplicas m = sa.optimal();
    double cost = sa.getOptimalCost();
    String out = "SA:{"
            + "isNewMethod:"
            + isNewMethod
            + "||"
            + "solution:"
            + m.getOrderString()
            + "||"
            + "cost:"
            + cost
            + "\n";
    fw.write(out);
    fw.close();
  }

  public static void DivgExp(DataTable dataTable, Query[] queries, int m, boolean isNewMethod) throws IOException, NoSuchAlgorithmException {
    File f = new File("data\\out\\divg.out");
//    File f = new File("divg.out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    DivgDesign dd = new DivgDesign(dataTable, queries, 3, m, 1000, 0.0000000000000000000001, false);
    MultiReplicas multi = dd.optimal();
    double cost = dd.getOptimalCost();
    String out = "Divg:{"
            + "isNewMethod:"
            + isNewMethod
            + "||"
            + " m = "
            + m + "||"
            + "solution:"
            + multi.getOrderString()
            + "||"
            + "cost:"
            + cost
            + "\n";
    fw.write(out);
    fw.close();
  }

  public static void SearchAllExp(DataTable dataTable, Query[] queries, boolean isNewMethod) throws IOException {
    File f = new File("data\\out\\searchall.out");
//    File f = new File("searchall.out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    SearchAll sa = new SearchAll(dataTable, queries);
    Replica r = sa.optimalReplica();
    MultiReplicas m = new MultiReplicas();
    m.add(new Replica(r)).add(new Replica(r)).add(new Replica(r));
    double cost = isNewMethod
            ? CostModel.cost(m, queries).doubleValue()
            : CostModel.totalCost(m, queries).doubleValue();
    String out = "searchall:{"
            + "isNewMethod:"
            + isNewMethod
            + "||"
            + "solution:"
            + m.getOrderString()
            + "||"
            + "cost:"
            + cost
            + "\n";
    fw.write(out);
    fw.close();
  }

  public static void GeneticExp(DataTable dataTable, Query[] queries, boolean isNewMethod) throws IOException, NoSuchAlgorithmException {
//    File f = new File("genetic.out");
    File f = new File("data\\out\\genetic.out");
    if (!f.exists()) f.createNewFile();
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    Genetic g = new Genetic(dataTable, queries, 3,
            100, 50, 20000,
            0.8, 0.01, 1, true);
    MultiReplicas m = g.optimal();
    double cost = isNewMethod
            ? CostModel.cost(m, queries).doubleValue()
            : CostModel.totalCost(m, queries).doubleValue();
    String out = "genetic:{"
            + "isNewMethod:"
            + isNewMethod
            + "||"
            + "solution:"
            + m.getOrderString()
            + "||"
            + "cost:"
            + cost
            + "\n";
    fw.write(out);
    fw.close();
  }


  public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    DataTable dataTable = getDataTable();
    Query[] queries = getQueries();
    int expTimes = 10;
    for(int i = 0;i < expTimes ; i++){
      System.out.println("turn " + i + ": ");
      SAExp(dataTable, queries, true);
      System.out.println("simulate anneal true finished");
      SAExp(dataTable, queries, false);
      System.out.println("simulate anneal false finished");
      DivgExp(dataTable, queries, 1, true);
      System.out.println("divergent m=1 true finished");
      DivgExp(dataTable, queries, 2, true);
      System.out.println("divergent m=2 true finished");
      DivgExp(dataTable, queries, 3, true);
      System.out.println("divergent m=3 true finished");
      DivgExp(dataTable, queries, 1, false);
      System.out.println("divergent m=1 false finished");
      DivgExp(dataTable, queries, 2, false);
      System.out.println("divergent m=2 false finished");
      DivgExp(dataTable, queries, 3, false);
      System.out.println("divergent m=3 false finished");
      SearchAllExp(dataTable, queries, true);
      System.out.println("search all true finished");
      SearchAllExp(dataTable, queries, false);
      System.out.println("search all false finished");
      GeneticExp(dataTable, queries, true);
      System.out.println("genetic true finished");
      GeneticExp(dataTable, queries, false);
      System.out.println("genetic false finished");
    }

  }
}
