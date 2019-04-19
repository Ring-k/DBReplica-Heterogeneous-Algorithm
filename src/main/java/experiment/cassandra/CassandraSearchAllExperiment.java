package experiment.cassandra;

import com.datastax.driver.core.*;
import dataloader.DataLoader;

import query.Query;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CassandraSearchAllExperiment {


  static void runExp(int[] replicaOrder, String method, int mode, int queryNumbers) throws IOException, ClassNotFoundException {
    // prepare for the experiment output
    String outputPath = method + "_" + "mod" + mode;
    File f = new File(outputPath + ".out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    // connect to experiment.cassandra
    String node = "localhost";
    String keyspace = "tpch";
    SocketOptions so = new SocketOptions().setReadTimeoutMillis(10000000).setConnectTimeoutMillis(100000000);
    Cluster cluster = Cluster.builder().addContactPoint(node).withSocketOptions(so).build();
    Session session = cluster.connect(keyspace);

    Query[] queries = DataLoader.getQueries("queries");
    double totalTime = 0;
    int curQueryNumber = 0;
    String queryCommand;
    ResultSet executeResult = null;
    for (Query query : queries) {
      if (curQueryNumber == queryNumbers) break;
      queryCommand = Command.getString(outputPath + "_rp0", query);
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
    if (mode == 1) {
      outputString += ("total = " + totalTime + "\n");
    } else {
      outputString += ("max = " + totalTime / 3 + "\n");
    }
    System.out.println(outputString);
    fw.write(outputString);
    fw.flush();
    fw.close();
  }

  static void runExp(int[] replicaOrder, String method, int mode) throws IOException, ClassNotFoundException {
    runExp(replicaOrder, method, mode, 1000);
  }

  public static void main(String args[]) throws IOException, ClassNotFoundException {
    String method = args[0];
    int mode = Integer.parseInt(args[1]);
    String[] order = args[2].split(",");
    int[] replicaOrder = new int[order.length];
    for (int i = 0; i < replicaOrder.length; i++)
      replicaOrder[i] = Integer.parseInt(order[i]);
    runExp(replicaOrder, method, mode);
  }

}
