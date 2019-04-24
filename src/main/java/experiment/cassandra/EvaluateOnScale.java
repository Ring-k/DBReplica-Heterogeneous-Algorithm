package experiment.cassandra;

import com.datastax.driver.core.*;
import cost.CostModel;
import dataloader.DataLoader;
import datamodel.DataTable;
import query.Query;
import replica.Replica;

import java.io.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;

public class EvaluateOnScale {

  static Cluster cluster;
  static Session session;

  /**
   * @param stringArray {"6","4","3","5","0","2","1"}
   * @return
   */
  static int[] getIntegerArray(String[] stringArray) {
    int[] res = new int[stringArray.length];
    for (int i = 0; i < res.length; i++)
      res[i] = Integer.parseInt(stringArray[i]);
    return res;
  }

  /**
   * @param string e.g. 6,4,3,5,0,2,1-4,5,3,0,2,6,1-5,3,0,2,4,6,1
   * @return
   */
  static int[][] getIntegerArray(String string) {
    String[] orders = string.split("-");
    int[][] res = new int[orders.length][orders[0].split(",").length];
    for (int i = 0; i < orders.length; i++)
      res[i] = getIntegerArray(orders[i].split(","));
    return res;
  }

  static void multipleReplicaExperiment(int[][] replicaOrder, String method, int loadBalance, int scale)
          throws IOException, ClassNotFoundException {
    multipleReplicaExperiment(replicaOrder, method, loadBalance, 1000, scale);
  }

  static void multipleReplicaExperiment(int[][] replicaOrder, String method, int scale)
          throws IOException, ClassNotFoundException {
    multipleReplicaExperiment(replicaOrder, method, 1, 1000, scale);
  }

  public static Session initSession() {
    String node = "localhost";
    String keyspace = "exp1";
    SocketOptions so = new SocketOptions().setReadTimeoutMillis(10000000).setConnectTimeoutMillis(100000000);
    cluster = Cluster.builder().addContactPoint(node).withSocketOptions(so).build();
    session = cluster.connect(keyspace);
    return session;
  }

  static void multipleReplicaExperiment(int[][] replicaOrders, String method, int loadBalance, int queryNumbers, int scale)
          throws IOException, ClassNotFoundException {
    // prepare for the experiment output
    String outputPath = method;
    File f = new File(outputPath + ".out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    // connect to experiment.cassandra
    initSession();

    // initialize data table, queries, replicas
    Query[] queries = DataLoader.getQueries("queries_s" + scale);
    DataTable dataTable = DataLoader.getDataTable("../data/lineitem_s" + scale + ".csv.obj");

    Replica[] replicas = new Replica[replicaOrders.length];
    for (int i = 0; i < replicaOrders.length; i++)
      replicas[i] = new Replica(dataTable, replicaOrders[i]);

    // initialize time consumption
    double[] times = new double[replicas.length];
    for (int i = 0; i < times.length; i++) times[i] = 0;

    int curQueryNumber = 0;

    // walk through all queries in workload
    String outputString = "";
    for (Query query : queries) {
      if (curQueryNumber % 50 == 0 && curQueryNumber != 0) System.out.print("\n\n query processed: " + cluster + "\t");
      if (curQueryNumber == queryNumbers) break;
      BigDecimal[] costs = new BigDecimal[replicas.length];
      for (int i = 0; i < replicas.length; i++)
        costs[i] = CostModel.cost(replicas[i], query);
      Integer[] replicaOrder = new Integer[replicas.length];
      for (int i = 0; i < replicaOrder.length; i++) replicaOrder[i] = i;
      Arrays.sort(replicaOrder, Comparator.comparing(o -> costs[o]));

      outputString += "eva costs: ";
      System.out.print("eva costs: ");
      for (int i = 0; i < costs.length; i++) {
        outputString += costs[i].setScale(10, BigDecimal.ROUND_HALF_UP) + ", ";
        System.out.print(costs[i].setScale(10, BigDecimal.ROUND_HALF_UP) + ", ");
      }
      outputString += "\n";
      System.out.print("\n");

      int number = 1;
      for (int i = 1; i < replicas.length; i++)
        if (costs[replicaOrder[i]].compareTo(costs[replicaOrder[0]]) == 0) number++;
      if (number < loadBalance) number = loadBalance;

      ResultSet executeResult = null;
      String queryCommand;

      // rout query command and get result
      System.out.print("query" + curQueryNumber + " ");
      for (int i = 0; i < number; i++) {
        int replicaIndex = replicaOrder[i];
        queryCommand = Command.getString(method.split("_")[0] + "_rp" + replicaIndex, query);
        long start = System.nanoTime();
        executeResult = session.execute(queryCommand);
        long time = System.nanoTime() - start;
        times[replicaIndex] += ((double) time / number);
        System.out.print(queryCommand);
        System.out.print("#rout: " + number);
        System.out.print("\n");
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


  static void singleReplicaExperiment(int[] replicaOrder, String method, int queryNumbers, int scale)
          throws IOException, ClassNotFoundException {
    // prepare for the experiment output
    String outputPath = method;
    File f = new File(outputPath + ".out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    // connect to experiment.cassandra
    String node = "localhost";
    String keyspace = "exp1_" + method;
    SocketOptions so = new SocketOptions().setReadTimeoutMillis(10000000).setConnectTimeoutMillis(100000000);
    Cluster cluster = Cluster.builder().addContactPoint(node).withSocketOptions(so).build();
    Session session = cluster.connect(keyspace);

    Query[] queries = DataLoader.getQueries("queries_s" + scale);
    double totalTime = 0;
    int curQueryNumber = 0;
    String queryCommand;
    ResultSet executeResult = null;
    for (Query query : queries) {
      if (curQueryNumber == queryNumbers) break;
      queryCommand = Command.getString(method.split("\\|")[0] + "_rp0", query);
      long start = System.nanoTime();
      executeResult = session.execute(queryCommand);
      long time = System.nanoTime() - start;
      totalTime += time;
      System.out.print(queryCommand);
      System.out.print("#rout: " + 3);
      for (Row row : executeResult) {
        long v = row.getLong("count");
        System.out.println(" result:" + v);
      }
      System.out.println();
      curQueryNumber++;
    }

    // close session
    session.close();
    cluster.close();

    // out put experiment result
    String outputString = "";
    for (int i = 0; i < 3; i++)
      outputString += ("time on replica" + i + ": " + totalTime / 3 + "; ");
    outputString += "\n";
    outputString += ("max = " + totalTime / 3 + "\n");
    System.out.println(outputString);
    fw.write(outputString);
    fw.flush();
    fw.close();
  }

  static void singleReplicaExperiment(int[] replicaOrder, String method, int scale)
          throws IOException, ClassNotFoundException {
    singleReplicaExperiment(replicaOrder, method, 1000, scale);
  }

  /**
   * input solution file path,
   * @param args
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public static void main(String args[]) throws IOException, ClassNotFoundException {
    String filePath = args[0];
    File f = new File(filePath);
    if (!f.exists()) throw new FileNotFoundException();
    BufferedReader reader = new BufferedReader(new FileReader(f));
    String line = null;
    while ((line = reader.readLine()) != null) {
      if (line == "" || line == "\n" || line.length() == 0) continue;
      String[] experimentInfo = line.split("\\|");
      String method = experimentInfo[0];
      int tpcScale = Integer.parseInt(experimentInfo[1]);
      if (method.equals("simulateanneal") || method.equals("genetic")) {
        int[][] solutions = getIntegerArray(experimentInfo[2]);
        multipleReplicaExperiment(solutions, method + "_s" + tpcScale, tpcScale);
      } else if (method.equals("divergentm3") || method.equals("searchall")) {
        int[] solution = getIntegerArray(experimentInfo[2].split(","));
        singleReplicaExperiment(solution, method + "_s" + tpcScale, tpcScale);
      } else {
        int[][] solutions = getIntegerArray(experimentInfo[2]);
        multipleReplicaExperiment(solutions, method + "_s" + tpcScale, Integer.parseInt(method.split("m")[1]), tpcScale);
      }
    }
  }

}
