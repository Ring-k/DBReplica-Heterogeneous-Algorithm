package experiment.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import cost.CostModel;
import dataloader.DataLoader;
import datamodel.DataTable;
import query.Query;
import replica.Replica;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EvaluateOnScaleMultiThread {

  static Cluster cluster;

  static List<Query>[] queryGroups;
  static ExecutorService threadPool = Executors.newFixedThreadPool(10);
  static Map<String, Session> sessions;
  static FileWriter fw;

  static Map initSession(File f) throws IOException {
    String node = "localhost";
    SocketOptions so = new SocketOptions().setReadTimeoutMillis(10000000).setConnectTimeoutMillis(100000000);
    cluster = Cluster.builder().addContactPoint(node).withSocketOptions(so).build();
    BufferedReader br = new BufferedReader(new FileReader(f));
    String line = null;
    sessions = new HashMap<>();
    while ((line = br.readLine()) != null) {
      String[] ss = line.split("\\|");
      String method = ss[0];
      String scale = ss[1];
      String keyspace = "exp1_" + method + "_s" + scale;
      Session session = cluster.connect(keyspace);
      if (!sessions.keySet().contains(keyspace))
        sessions.put(keyspace, session);
    }
    br.close();
    return sessions;
  }

  static void closeSession() {
    for (Session s : sessions.values())
      s.close();
    cluster.close();
  }

  static void route(Replica[] replicas, Query[] queries, int loadBalance) {
    if (replicas.length == 1) {
      queryGroups = new List[1];
      for (Query query : queries)
        queryGroups[0].add(query);
      return;
    }
    queryGroups = new List[replicas.length];
    for (int i = 0; i < queryGroups.length; i++)
      queryGroups[i] = new ArrayList<>();
    for (Query query : queries) {
      BigDecimal[] costs = new BigDecimal[replicas.length];
      for (int i = 0; i < replicas.length; i++)
        costs[i] = CostModel.cost(replicas[i], query);
      Integer[] replicaOrder = new Integer[replicas.length];
      for (int i = 0; i < replicaOrder.length; i++) replicaOrder[i] = i;
      Arrays.sort(replicaOrder, Comparator.comparing(o -> costs[o]));
      int number = 1;
      for (int i = 1; i < replicas.length; i++)
        if (costs[replicaOrder[i]].compareTo(costs[replicaOrder[0]]) == 0) number++;
      if (number < loadBalance) number = loadBalance;
      for (int i = 0; i < number; i++) {
        Query q = new Query(query).setWeight((double) 1 / number);
        queryGroups[i].add(q);
      }
    }
  }

  static class RunQueryThread extends Thread {
    String keySpace;
    String tableName;
    int replicaIndex;
    Query query;
    int queryId;

    public RunQueryThread(String keySpace, String tableName, int replicaIndex, Query query, int queryId) {
      super();
      this.keySpace = keySpace;
      this.tableName = tableName;
      this.replicaIndex = replicaIndex;
      this.query = query;
      this.queryId = queryId;
    }

    public void run() {
      Session session = sessions.get(keySpace);
      double weight = query.getWeight();
      String queryCommand = Command.getString(tableName, query);
      long start = System.nanoTime();
      session.execute(queryCommand);
      double time = (double) (System.nanoTime() - start) * weight;
      String line = queryId + "," + keySpace + "," + tableName + "," + time + "\n";
      System.out.print("[result]" + line); // TODO
      try {
        fw.write(line);
        fw.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  static int[] getIntegerArray(String[] stringArray) {
    int[] res = new int[stringArray.length];
    for (int i = 0; i < res.length; i++)
      res[i] = Integer.parseInt(stringArray[i]);
    return res;
  }

  static int[][] getIntegerArray(String string) {
    if (!string.contains("-")) {
      String[] ss = string.split(",");
      int[][] res = new int[1][ss.length];
      res[0] = getIntegerArray(ss);
      return res;
    }
    String[] orders = string.split("-");
    int[][] res = new int[orders.length][orders[0].split(",").length];
    for (int i = 0; i < orders.length; i++)
      res[i] = getIntegerArray(orders[i].split(","));
    return res;
  }


  /**
   * @param args, arg[0] = input file path, arg[1] = output file path
   * @throws IOException
   */
  public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {
//    args = new String[]{"solution.txt", "simulateanneal.out"};
    File solutionFile = new File(args[0]);
    initSession(solutionFile);
    File out = new File(args[1]);
    fw = new FileWriter(out, true);
    BufferedReader br = new BufferedReader(new FileReader(solutionFile));
    String line = null;
    while ((line = br.readLine()) != null) {
      System.out.println(line); // TODO
      String[] ss = line.split("\\|");
      String method = ss[0];
      String scale = ss[1];
      String replicaString = ss[2];
      int[][] replicaOrder = getIntegerArray(replicaString);
      DataTable dataTable = DataLoader.getDataTable("../data/lineitem_s" + scale + ".csv.obj");
      Query[] queries = DataLoader.getQueries("queries_s" + scale);
      Replica[] replicas = new Replica[replicaOrder.length];
      for (int i = 0; i < replicas.length; i++)
        replicas[i] = new Replica(dataTable, replicaOrder[i]);
      int loadBalance = 1;
      if (method.startsWith("divergent")) {
        loadBalance = Integer.parseInt(method.split("m")[1]);
      }
      route(replicas, queries, loadBalance);
      for (int replicaIndex = 0; replicaIndex < queryGroups.length; replicaIndex++) {
        String keyspace = "exp1_" + method + "_s" + scale;
        String tableName = method + "_rp" + replicaIndex;
        for (int i = 0; i < queryGroups[replicaIndex].size(); i++) {
          RunQueryThread thread = new RunQueryThread(keyspace, tableName, replicaIndex, queryGroups[replicaIndex].get(i), i);
          threadPool.execute(thread);
        }
      }
    }
    System.out.println("Thread all created.");
    threadPool.shutdown();
    while(true) {
      if (threadPool.isTerminated()) break;
      Thread.sleep(1000);
    }
    fw.close();
    closeSession();
    System.out.println("------------------------------------------finished " + args[0] + args[1]);
  }

}

