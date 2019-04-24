package experiment.cassandra;

import com.datastax.driver.core.*;
import cost.CostModel;
import dataloader.DataLoader;
import datamodel.DataTable;
import query.Query;
import replica.Replica;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;

public class CassandraExecuteExperiment {

  /**
   * This experiment use default replica number 3.
   *
   * @param replicaOrder0 Clustering key order of 1-st replica
   * @param replicaOrder1 Clustering key order of 2-nd replica
   * @param replicaOrder2 Clustering key order of 3-rd replica
   * @param method        The method to use, one of {"simulateanneal", "searchall", "genetic", "divergent"}
   * @param queryNumbers  Number of queries need to evaluate
   */
  static void runExp(int[] replicaOrder0, int[] replicaOrder1, int[] replicaOrder2,
                     String method, int queryNumbers) throws IOException, ClassNotFoundException {
    // prepare for the experiment output
    String outputPath = method;
    File f = new File(outputPath + ".out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    // connect to experiment.cassandra
    String node = "localhost";
    String keyspace = "tpch";
    SocketOptions so = new SocketOptions().setReadTimeoutMillis(10000000).setConnectTimeoutMillis(100000000);
    Cluster cluster = Cluster.builder().addContactPoint(node).withSocketOptions(so).build();
    Session session = cluster.connect(keyspace);

    // initialize data table, queries, replicas
    Query[] queries = DataLoader.getQueries("queries");
    DataTable dataTable = DataLoader.getDataTable("data_table");
//    Query[] queries = DataLoader.getQueries();
//    DataTable dataTable = DataLoader.getDataTableFromCsv();
    Replica[] replicas = {new Replica(dataTable, replicaOrder0),
            new Replica(dataTable, replicaOrder1),
            new Replica(dataTable, replicaOrder2)};

    // initialize time consumption
    double[] times = new double[replicas.length];
    for (int i = 0; i < times.length; i++) times[i] = 0;

    int curQueryNumber = 0;

    // walk through all queries in workload
    for (Query query : queries) {
      if (curQueryNumber == queryNumbers) break;
      BigDecimal[] costs = new BigDecimal[replicas.length];
      for (int i = 0; i < replicas.length; i++)
        costs[i] = CostModel.cost(replicas[i], query);
      Integer[] replicaOrder = new Integer[replicas.length];
      for (int i = 0; i < replicaOrder.length; i++) replicaOrder[i] = i;
      Arrays.sort(replicaOrder, Comparator.comparing(o -> costs[o]));


      System.out.print("eva costs: " + Arrays.toString(costs));
      System.out.println(" order:" + Arrays.toString(replicaOrder));

      int number = 1;
      for (int i = 1; i < replicas.length; i++)
        if (costs[replicaOrder[i]].compareTo(costs[replicaOrder[0]]) == 0) number++;

      ResultSet executeResult = null;
      String queryCommand;

      // rout query command and get result
      System.out.print("query" + curQueryNumber + " ");
      for (int i = 0; i < number; i++) {
        int replicaIndex = replicaOrder[i];
        queryCommand = Command.getString(outputPath + "_rp" + replicaIndex, query);
        long start = System.nanoTime();
        executeResult = session.execute(queryCommand);
        long time = System.nanoTime() - start;
        times[replicaIndex] += ((double) time / number);
        System.out.print(queryCommand);
        System.out.print("#rout: " + number);
      }

      // print result
      for (Row row : executeResult) {
        long v = row.getLong("count");
//        if (v != 0) {
        System.out.println(" result:" + v);
//        }
      }
      System.out.println();
      curQueryNumber++;
    }

    // close session
    session.close();
    cluster.close();

    // out put experiment result
    String outputString = "";
    for (int i = 0; i < replicas.length; i++)
      outputString += ("time on replica" + i + ": " + times[i] + "; ");
    outputString += "\n";

    double max = 0;
    for (double i : times) if (max < i) max = i;
    outputString += ("max = " + max + "\n");

    System.out.println(outputString);
    fw.write(outputString);
    fw.flush();
    fw.close();
  }

  /**
   * Experiment using default query number, 1000
   */
  static void runExp(int[] replicaOrder0, int[] replicaOrder1, int[] replicaOrder2, String method) throws IOException, ClassNotFoundException {
    runExp(replicaOrder0, replicaOrder1, replicaOrder2, method, 1000);
  }

  public static void main(String args[]) throws IOException, ClassNotFoundException {
    String method = args[0];
    String[] order0 = args[2].split(",");
    String[] order1 = args[3].split(",");
    String[] order2 = args[4].split(",");

    int[] rep0 = new int[order0.length];
    for (int i = 0; i < order0.length; i++) rep0[i] = Integer.parseInt(order0[i]);
    int[] rep1 = new int[order1.length];
    for (int i = 0; i < order1.length; i++) rep1[i] = Integer.parseInt(order1[i]);
    int[] rep2 = new int[order2.length];
    for (int i = 0; i < order2.length; i++) rep2[i] = Integer.parseInt(order2[i]);

    runExp(rep0, rep1, rep2, method);
  }
}
