package experiment.cassandra;

import cost.CostModel;
import dataloader.DataLoader;
import datamodel.DataTable;
import divergentdesign.DivergentDesign;
import genetic.Genetic;
import heterogeneous.SimulateAnneal;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;
import searchall.SearchAll;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class GetMultiReplicaSolutionExperiment {

  public static void SAExp(String dataTablePath, DataTable dataTable, Query[] queries, boolean isNewMethod) throws IOException, NoSuchAlgorithmException {
    File f = new File("simulateanneal.out");
//    File f = new File("data\\out\\sa.out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
//    Replica replica = new SearchAll(dataTable, queries).optimalReplica();
    SimulateAnneal sa = new SimulateAnneal(dataTable, queries, 3, isNewMethod).initSolution();
    MultiReplicas m = sa.optimal();
    double cost = sa.getOptimalCost();
    String out = "SA:{table" + dataTablePath + "isNewMethod:" + isNewMethod + "||" + "solution:" + m.getOrderString() + "||" + "cost:" + cost + "\n";
    out += Arrays.toString(CostModel.costOnEachReplica(m, queries));
    out.replaceAll(" ", "");
    fw.write(out);
    fw.close();
  }

  public static void DivgExp(String dataTablePath, DataTable dataTable, Query[] queries, int m, boolean isNewMethod) throws IOException, NoSuchAlgorithmException {
//    File f = new File("data\\out\\divg.out");
    File f = new File("divg.out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    DivergentDesign dd = new DivergentDesign(dataTable, queries, 3, m, 1000, 0.0000000000000000000001, isNewMethod);
    MultiReplicas multi = dd.optimal();
    double cost = dd.getOptimalCost();
    String out = "Divg:{table" + dataTablePath + "isNewMethod:" + isNewMethod + "||" + " m = " + m + "||" + "solution:" + multi.getOrderString() + "||" + "cost:" + cost + "\n";
    out += Arrays.toString(CostModel.costOnEachReplica(multi, queries, m));
    out.replaceAll(" ", "");
    fw.write(out);
    fw.close();
  }

  public static void SearchAllExp(String dataTablePath, DataTable dataTable, Query[] queries, boolean isNewMethod) throws IOException {
//    File f = new File("data\\out\\searchall.out");
    File f = new File("searchall.out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    SearchAll sa = new SearchAll(dataTable, queries);
    Replica r = sa.optimalReplica();
    MultiReplicas m = new MultiReplicas();
    m.add(new Replica(r)).add(new Replica(r)).add(new Replica(r));
    double cost = isNewMethod
            ? CostModel.cost(m, queries).doubleValue()
            : CostModel.totalCost(m, queries).doubleValue();
    String out = "searchall:{table" + dataTablePath + "isNewMethod:" + isNewMethod + "||" + "solution:" + m.getOrderString() + "||" + "cost:" + cost + "\n";
    out += Arrays.toString(CostModel.costOnEachReplica(m, queries));
    out.replaceAll(" ", "");
    fw.write(out);
    fw.close();
  }

  public static void GeneticExp(String dataTablePath, DataTable dataTable, Query[] queries, boolean isNewMethod) throws IOException, NoSuchAlgorithmException {
    File f = new File("genetic.out");
//    File f = new File("data\\out\\genetic.out");
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
    String out = "genetic:{table" + dataTablePath + "isNewMethod:" + isNewMethod + "||" + "solution:" + m.getOrderString() + "||" + "cost:" + cost + "\n";
    out += Arrays.toString(CostModel.costOnEachReplica(m, queries));
    out.replaceAll(" ", "");
    fw.write(out);
    fw.close();
  }


  /**
   * @param args, {dataTableFilePath, queryFilePath}
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws NoSuchAlgorithmException
   */
  public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    DataTable dataTable = null;
    Query[] queries = null;
    if (args.length != 0) {
      dataTable = DataLoader.getDataTable(args[0]);
      queries = DataLoader.getQueries(args[1]);
    }

    if (dataTable == null || queries == null) {
      for (int i = 1; i <= 5; i++) {
        String filePath = "data_table_lineitem_s" + i;
        dataTable = DataLoader.getDataTable(filePath);
        queries = DataLoader.getQueries(filePath);

      }
    } else {
      int expTimes = 10;
      for (int i = 0; i < expTimes; i++) {
        System.out.println("turn " + i + ": ");
        System.out.println("Begin simulate anneal");
        SAExp(args[0], dataTable, queries, true);
        System.out.println("simulate anneal true finished");
        System.out.println("Begin divergent m = 1");
        DivgExp(args[0], dataTable, queries, 1, true);
        System.out.println("divergent m=1 true finished");
        System.out.println("Begin divergent m = 2");
        DivgExp(args[0], dataTable, queries, 2, true);
        System.out.println("divergent m=2 true finished");
        System.out.println("Begin divergent m = 3");
        DivgExp(args[0], dataTable, queries, 3, true);
        System.out.println("divergent m=3 true finished");
        System.out.println("Begin search all");
        SearchAllExp(args[0], dataTable, queries, true);
        System.out.println("search all true finished");
        System.out.println("Begin genetic");
        GeneticExp(args[0], dataTable, queries, true);
        System.out.println("genetic true finished");
      }
    }


  }
}
