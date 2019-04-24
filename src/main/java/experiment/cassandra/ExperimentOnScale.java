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
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExperimentOnScale {

  public static void SAExp(String dataTablePath, DataTable dataTable, Query[] queries, boolean isNewMethod)
          throws IOException, NoSuchAlgorithmException {
    File f = new File("simulateanneal.out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    SimulateAnneal sa = new SimulateAnneal(dataTable, queries, 3, isNewMethod).initSolution();
    MultiReplicas m = sa.optimal();
    double cost = sa.getOptimalCost();
    String out = "SA:{table" + dataTablePath + "isNewMethod:" + isNewMethod + "||" + "solution:" + m.getOrderString() + "||" + "cost:" + cost + "|| costs: ";
    BigDecimal[] costs = CostModel.costOnEachReplica(m, queries);
    for (BigDecimal c : costs) out += c.setScale(10, BigDecimal.ROUND_HALF_UP) + ",";
    out += "\n";
    fw.write(out);
    fw.close();
  }

  public static void DivgExp(String dataTablePath, DataTable dataTable, Query[] queries, int m, boolean isNewMethod)
          throws IOException, NoSuchAlgorithmException {
    File f = new File("divg.out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    DivergentDesign dd = new DivergentDesign(dataTable, queries, 3, m, 1000, 0.0000000000000000000001, isNewMethod);
    MultiReplicas multi = dd.optimal();
    double cost = CostModel.cost(multi, queries).doubleValue();
//    double cost = dd.getOptimalCost();
    String out = "Divg:{table" + dataTablePath + "isNewMethod:" + isNewMethod + "||" + " m = " + m + "||" + "solution:" + multi.getOrderString() + "||" + "cost:" + cost + "|| costs: ";
    BigDecimal[] costs = CostModel.costOnEachReplica(multi, queries);
    for (BigDecimal c : costs) out += c.setScale(10, BigDecimal.ROUND_HALF_UP) + ",";
    out += "\n";
    fw.write(out);
    fw.close();
  }

  public static void SearchAllExp(String dataTablePath, DataTable dataTable, Query[] queries, boolean isNewMethod) throws IOException {
    File f = new File("searchall.out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    SearchAll sa = new SearchAll(dataTable, queries);
    Replica r = sa.optimalReplica();
    MultiReplicas m = new MultiReplicas();
    m.add(new Replica(r)).add(new Replica(r)).add(new Replica(r));
    double cost = CostModel.cost(m, queries).doubleValue();
    String out = "searchall:{table" + dataTablePath + "isNewMethod:" + isNewMethod + "||" + "solution:" + m.getOrderString() + "||" + "cost:" + cost + "|| costs: ";
    BigDecimal[] costs = CostModel.costOnEachReplica(m, queries);
    for (BigDecimal c : costs) out += c.setScale(10, BigDecimal.ROUND_HALF_UP) + ",";
    out += "\n";
    fw.write(out);
    fw.close();
  }

  public static void GeneticExp(String dataTablePath, DataTable dataTable, Query[] queries, boolean isNewMethod) throws IOException, NoSuchAlgorithmException {
    File f = new File("genetic.out");
    if (!f.exists()) f.createNewFile();
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    Genetic g = new Genetic(dataTable, queries, 3,
            100, 50, 20000,
            0.8, 0.01, 1, true);
    MultiReplicas m = g.optimal();
    double cost = CostModel.cost(m, queries).doubleValue();
    String out = "genetic:{table" + dataTablePath + "isNewMethod:" + isNewMethod + "||" + "solution:" + m.getOrderString() + "||" + "cost:" + cost + "|| costs: ";
    BigDecimal[] costs = CostModel.costOnEachReplica(m, queries);
    for (BigDecimal c : costs) out += c.setScale(10, BigDecimal.ROUND_HALF_UP) + ",";
    out += "\n";
    fw.write(out);
    fw.close();
  }

  public static void getSolutionsExp(String[] args) {
    DataTable dataTable = null;
    Query[] queries = null;

    int expTimes = 10;
    ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 2);
    for (int i = 1; i <= 5; i++) {
      String dataPath = "../data/lineitem_s" + i + ".csv.obj";
      String queryPath = "queries_s" + i;
      final int s = i;
      Thread simulateAnnealThread = new Thread(() -> {
        for (int j = 0; j < expTimes; j++) {
          try {
            System.out.println("start simulate anneal, repeat " + j + ", s = " + s);
            DataTable dataTable1 = DataLoader.getDataTable(dataPath);
            Query[] queries1 = DataLoader.getQueries(queryPath);
            SAExp(dataPath, dataTable1, queries1, true);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

      Thread divergentM1Thread = new Thread(() -> {
        for (int j = 0; j < expTimes; j++) {
          try {
            System.out.println("start divergent m = 1, repeat " + j + ", s = " + s);
            DataTable dataTable1 = DataLoader.getDataTable(dataPath);
            Query[] queries1 = DataLoader.getQueries(queryPath);
            DivgExp(dataPath, dataTable1, queries1, 1, true);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

      Thread divergentM2Thread = new Thread(() -> {
        for (int j = 0; j < expTimes; j++) {
          try {
            System.out.println("start divergent m = 2, repeat " + j + ", s = " + s);
            DataTable dataTable1 = DataLoader.getDataTable(dataPath);
            Query[] queries1 = DataLoader.getQueries(queryPath);
            DivgExp(dataPath, dataTable1, queries1, 2, true);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

      Thread divergentM3Thread = new Thread(() -> {
        for (int j = 0; j < expTimes; j++) {
          try {
            System.out.println("start divergent m = 3, repeat " + j + ", s = " + s);
            DataTable dataTable1 = DataLoader.getDataTable(dataPath);
            Query[] queries1 = DataLoader.getQueries(queryPath);
            DivgExp(dataPath, dataTable1, queries1, 3, true);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

      Thread searchAllThread = new Thread(() -> {
        for (int j = 0; j < expTimes; j++) {
          try {
            System.out.println("start search all, repeat " + j + ", s = " + s);
            DataTable dataTable1 = DataLoader.getDataTable(dataPath);
            Query[] queries1 = DataLoader.getQueries(queryPath);
            SearchAllExp(dataPath, dataTable1, queries1, true);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

      Thread geneticThread = new Thread(() -> {
        for (int j = 0; j < expTimes; j++) {
          try {
            System.out.println("start genetic, repeat " + j + ", s = " + s);
            DataTable dataTable1 = DataLoader.getDataTable(dataPath);
            Query[] queries1 = DataLoader.getQueries(queryPath);
            GeneticExp(dataPath, dataTable1, queries1, true);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

//      threadPool.execute(simulateAnnealThread);
      threadPool.execute(divergentM1Thread);
      threadPool.execute(divergentM2Thread);
      threadPool.execute(divergentM3Thread);
//      threadPool.execute(searchAllThread);
//      threadPool.execute(geneticThread);
    }
//      threadPool.shutdownNow();
  }


  /**
   * @param args, {dataTableFilePath, queryFilePath}
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws NoSuchAlgorithmException
   */
  public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    getSolutionsExp(args);
  }
}
