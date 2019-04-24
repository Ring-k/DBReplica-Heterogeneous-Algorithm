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

public class ExperimentOnReplicaNumbers {

  public static void SAExp(String dataTablePath, DataTable dataTable, Query[] queries, int replicaNumber)
          throws IOException, NoSuchAlgorithmException {
    File f = new File("simulateanneal_r" + replicaNumber + ".out");
//    File f = new File("data\\out\\sa.out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
//    Replica replica = new SearchAll(dataTable, queries).optimalReplica();
    SimulateAnneal sa = new SimulateAnneal(dataTable, queries).withReplicaNumber(replicaNumber).initSolution();
    MultiReplicas m = sa.optimal();
    double cost = sa.getOptimalCost();
    String out = "SA:{table" + dataTablePath + "||replica_num" + replicaNumber + "||solution:" + m.getOrderString() + "||" + "cost:" + cost + "||costs: ";
    BigDecimal[] costs = CostModel.costOnEachReplica(m, queries);
    for (BigDecimal c : costs) out += c.setScale(10, BigDecimal.ROUND_HALF_UP) + ",";
    out += "\n";
    fw.write(out);
    fw.close();
  }

  public static void DivgExp(String dataTablePath, DataTable dataTable, Query[] queries, int m, int replicaNumber)
          throws IOException, NoSuchAlgorithmException {
//    File f = new File("data\\out\\divg.out");
    File f = new File("divg_r" + replicaNumber + ".out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    DivergentDesign dd = new DivergentDesign(dataTable, queries)
            .withReplicaNumber(replicaNumber)
            .withLoadBalanceFactor(m)
            .withMaxIteration(1000)
            .withEpsilon(0.0000000000000000000001);
    MultiReplicas multi = dd.optimal();
    double cost = dd.getOptimalCost();
    String out = "Divg:{table" + dataTablePath + "|| replica_num = " + replicaNumber + "||" + " m = " + m + "||" + "solution:" + multi.getOrderString() + "||" + "cost:" + cost + "|| costs: ";
    BigDecimal[] costs = CostModel.costOnEachReplica(multi, queries);
    for (BigDecimal c : costs) out += c.setScale(10, BigDecimal.ROUND_HALF_UP) + ",";
    out += "\n";
    fw.write(out);
    fw.close();
  }

  public static void SearchAllExp(String dataTablePath, DataTable dataTable, Query[] queries, int replicanumber)
          throws IOException {
//    File f = new File("data\\out\\searchall.out");
    File f = new File("searchall.out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    SearchAll sa = new SearchAll(dataTable, queries);
    Replica r = sa.optimalReplica();
    MultiReplicas m = new MultiReplicas();
    for (int i = 0; i < replicanumber; i++)
      m.add(new Replica(r));
    double cost = CostModel.cost(m, queries).doubleValue();
    String out = "searchall:{table" + dataTablePath + "||" + "solution:" + m.getOrderString() + "||" + "cost:" + cost + "|| costs: ";
    BigDecimal[] costs = CostModel.costOnEachReplica(m, queries);
    for (BigDecimal c : costs) out += c.setScale(10, BigDecimal.ROUND_HALF_UP) + ",";
    out += "\n";
    fw.write(out);
    fw.close();
  }

  public static void GeneticExp(String dataTablePath, DataTable dataTable, Query[] queries, int replicaNumber)
          throws IOException, NoSuchAlgorithmException {
    File f = new File("genetic_r" + replicaNumber + ".out");
//    File f = new File("data\\out\\genetic.out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    Genetic g = new Genetic(dataTable, queries)
            .withReplicaNumber(replicaNumber)
            .withPopulationSize(100)
            .withMinIteration(50)
            .withMaxIteration(20000)
            .withCrossoverRate(0.8)
            .withMutationRate(0.01)
            .withGeneChangeNumber(1);

    MultiReplicas m = g.optimal();
    double cost = CostModel.cost(m, queries).doubleValue();
    String out = "genetic:{table" + dataTablePath + "|| replica_num = " + replicaNumber + "||" + "solution:" + m.getOrderString() + "||" + "cost:" + cost + "|| costs: ";
    BigDecimal[] costs = CostModel.costOnEachReplica(m, queries);
    for (BigDecimal c : costs) out += c.setScale(10, BigDecimal.ROUND_HALF_UP) + ",";
    out += "\n";
    fw.write(out);
    fw.close();
  }


  /**
   * @param args, {dataTableFilePath, queryFilePath}
   */
  public static void main(String args[]) {

    int expTimes = 10;
    ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 2);

    final int s = 1;
    String dataPath = "lineitem_s" + s + ".csv.obj";
    String queryPath = "queries";

    Thread simulateAnnealThread = new Thread(() -> {
      for (int j = 0; j < expTimes; j++) {
        try {
          System.out.println("start simulate anneal, repeat " + j + ", s = " + s);
          DataTable dataTable1 = DataLoader.getDataTable(dataPath);
          Query[] queries1 = DataLoader.getQueries(queryPath);
          for (int k = 2; k <= 5; k++)
            SAExp(dataPath, dataTable1, queries1, k);
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
          for (int k = 2; k <= 5; k++)
            DivgExp(dataPath, dataTable1, queries1, 1, k);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

//    Thread divergentM2Thread = new Thread(() -> {
//      for (int j = 0; j < expTimes; j++) {
//        try {
//          System.out.println("start divergent m = 2, repeat " + j + ", s = " + s);
//          DataTable dataTable1 = DataLoader.getDataTable(dataPath);
//          Query[] queries1 = DataLoader.getQueries(queryPath);
//          for (int k = 2; k <= 5; k++)
//            DivgExp(dataPath, dataTable1, queries1, 2, k);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      }
//    });

//    Thread divergentM3Thread = new Thread(() -> {
//      for (int j = 0; j < expTimes; j++) {
//        try {
//          System.out.println("start divergent m = 3, repeat " + j + ", s = " + s);
//          DataTable dataTable1 = DataLoader.getDataTable(dataPath);
//          Query[] queries1 = DataLoader.getQueries(queryPath);
//          for (int k = 2; k <= 5; k++)
//            DivgExp(dataPath, dataTable1, queries1, 3, k);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      }
//    });

    Thread searchAllThread = new Thread(() -> {
      for (int j = 0; j < expTimes; j++) {
        try {
          System.out.println("start search all, repeat " + j + ", s = " + s);
          DataTable dataTable1 = DataLoader.getDataTable(dataPath);
          Query[] queries1 = DataLoader.getQueries(queryPath);
          for (int k = 2; k <= 5; k++)
            SearchAllExp(dataPath, dataTable1, queries1, k);
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
          for (int k = 2; k <= 5; k++)
            GeneticExp(dataPath, dataTable1, queries1, k);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    threadPool.execute(simulateAnnealThread);
    threadPool.execute(divergentM1Thread);
    threadPool.execute(searchAllThread);
    threadPool.execute(geneticThread);
  }


}

