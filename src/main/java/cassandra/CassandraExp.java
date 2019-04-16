package cassandra;

import com.datastax.driver.core.*;
import cost.CostModel;
import dataloader.DataLoader;
import datamodel.DataTable;
import query.Query;
import replica.Replica;

import java.io.*;

public class CassandraExp {


  public static long max(long l1, long l2, long l3) {
    if (l1 >= l2 && l1 >= l3) return l1;
    if (l2 >= l1 && l2 >= l3) return l2;
    else return l3;
  }

  public static void runExp(int[] replicaOrder0, int[] replicaOrder1, int[] replicaOrder2, String method, int mode) throws IOException, ClassNotFoundException {
    String outpath = method + "_" + "mod" + mode;
    File f = new File(outpath + ".out");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    String node = "localhost";
    String keyspace = "tpch";
    SocketOptions so = new SocketOptions().setReadTimeoutMillis(10000000).setConnectTimeoutMillis(100000000);
    Cluster cluster = Cluster.builder().addContactPoint(node).withSocketOptions(so).build();
    Session session = cluster.connect(keyspace);

    Query[] quereis = DataLoader.getQueries();
    DataTable dataTable = DataLoader.getDataTable();
//    int[] replicaOrder0 = {4, 5, 6, 1, 3, 0, 2};
//    int[] replicaOrder1 = {0, 4, 5, 6, 1, 3, 2};
//    int[] reolicaOrder2 = {2, 4, 5, 6, 1, 0, 3};
    Replica rep0 = new Replica(dataTable, replicaOrder0);
    Replica rep1 = new Replica(dataTable, replicaOrder1);
    Replica rep2 = new Replica(dataTable, replicaOrder2);


    long timeOfRep0 = 0, timeOfRep1 = 0, timeOfRep2 = 0;

    int cnt = 0;
    for (Query q : quereis) {
      if (cnt == 1000) break;
      double costOnRep0 = CostModel.cost(rep0, q).doubleValue();
      double costOnRep1 = CostModel.cost(rep1, q).doubleValue();
      double costOnRep2 = CostModel.cost(rep2, q).doubleValue();
      ResultSet r;
      String cmd;
      if (costOnRep0 <= costOnRep1 && costOnRep0 <= costOnRep2) {
        cmd = Command.getString(outpath + "_rp0", q);
//        System.out.println(cmd);
        long start = System.currentTimeMillis();
        r = session.execute(cmd);
        timeOfRep0 += (System.currentTimeMillis() - start);

      } else if (costOnRep1 <= costOnRep0 && costOnRep1 <= costOnRep2) {
        cmd = Command.getString(outpath + "_rp1", q);
//        System.out.println(cmd);
        long start = System.currentTimeMillis();
        r = session.execute(cmd);
        timeOfRep1 += (System.currentTimeMillis() - start);
      } else {
        cmd = Command.getString(outpath + "_rp2", q);
//        System.out.println(cmd);
        long start = System.currentTimeMillis();
        r = session.execute(cmd);
        timeOfRep2 += (System.currentTimeMillis() - start);
      }

      for (Row row : r) {
        long v = row.getLong("count");
        if (v != 0) {
          System.out.print("query" + cnt + " ");
          System.out.println(v);
        }
      }
      cnt++;
    }
    session.close();
    cluster.close();
    System.out.println("time on rep0 : " + timeOfRep0);
    System.out.println("time on rep1 : " + timeOfRep1);
    System.out.println("time on rep2 : " + timeOfRep2);
    fw.write("rp0 : " + timeOfRep0 + ", rp1:" + timeOfRep1 + ", rp2" + timeOfRep2 + ", ");
    if (mode == 1) {
      long sum = timeOfRep0 + timeOfRep1 + timeOfRep2;
      fw.write("total = " + sum + "\n");
      System.out.println("total : " + sum);
    } else {
      long mx = max(timeOfRep0, timeOfRep1, timeOfRep2);
      System.out.println("max = " + mx + "\n");
      fw.write("" + mx + "\n");
    }
    fw.flush();
    fw.close();
  }

  public static void main(String args[]) throws IOException, ClassNotFoundException {
    String method = args[0];
    int mode = Integer.parseInt(args[1]);
    String[] order0 = args[2].split(",");
    String[] order1 = args[3].split(",");
    String[] order2 = args[4].split(",");

    int[] rep0 = new int[order0.length];
    for (int i = 0; i < order0.length; i++) rep0[i] = Integer.parseInt(order0[i]);
    int[] rep1 = new int[order1.length];
    for (int i = 0; i < order1.length; i++) rep1[i] = Integer.parseInt(order1[i]);
    int[] rep2 = new int[order2.length];
    for (int i = 0; i < order2.length; i++) rep2[i] = Integer.parseInt(order2[i]);

    runExp(rep0, rep1, rep2, method, mode);

  }
}
