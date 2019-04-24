package experiment;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import cost.CostModel;
import dataloader.DataLoader;
import datamodel.DataTable;
import experiment.cassandra.Command;
import experiment.preprocess.GenerateDataTable;
import query.Query;
import query.QueryGenerator;
import replica.Replica;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

public class VaryHistogramBucketEva {

  public static void generateTables(int minBucket, int maxBucket, int step) throws IOException, ParseException {

    int[] cols = {1, 2, 3, 4, 11, 12, 13};
    for (int i = minBucket; i <= maxBucket; i += step) {
      System.out.println("generate table bucket = " + i);
      DataTable dataTable = GenerateDataTable.getDataTableFromCsv("../data/lineitem_s1.csv", cols, i);
      String filePath = "data_lineitem_s1_b" + i;
      DataLoader.serialize(dataTable, filePath);
    }
  }

  public static void generateQueries(DataTable dataTable, int queryNumber, String outputPath) throws NoSuchAlgorithmException {
    System.out.println("write queries to " + outputPath);
    QueryGenerator queryGenerator = new QueryGenerator(queryNumber, dataTable);
    Query[] queries = queryGenerator.getQueries();
    DataLoader.serialize(queries, outputPath);
  }

  public static void main(String args[]) throws IOException, ParseException, ClassNotFoundException, NoSuchAlgorithmException {

    if (args.length == 0) {
      args = new String[]{"10", "100", "10"};
    }
    int min = Integer.parseInt(args[0]), max = Integer.parseInt(args[1]), step = Integer.parseInt(args[2]);

    // 1. generate tables and serialize them on the disk
//    System.out.println("start generate tables");
//    generateTables(min, max, step);
//
//
//    // 2. generate queries and serialize them on the disk
//    System.out.println("start generate queries");
//    generateQueries(DataLoader.getDataTable("data_lineitem_s1_b10"), 10000, "queries_on_data_lineitem_s1_b10_n10000");


    System.out.println("connect to cassandra and warm up");
    String node = "localhost";
    String keyspace = "bkt";
    SocketOptions so = new SocketOptions().setReadTimeoutMillis(10000000).setConnectTimeoutMillis(100000000);
    Cluster cluster = Cluster.builder().addContactPoint(node).withSocketOptions(so).build();
    Session session = cluster.connect(keyspace);
    long starWarmUpTime = System.currentTimeMillis();
    session.execute("select count(*) from default_table where pkey = 1");
    System.out.println("warming up finishes, " + (System.currentTimeMillis() - starWarmUpTime) / 1000 + " seconds");


    // 3. for data table on the list
    //        prepare record_file
    //        for each query in queries
    //            1) evaluate cost(table, query)
    //            2) get the command and execute on cassandra
    //            3) write/append the pair (eva_cost, real_cost) to a record_file

    int maxQuery = 1000;
    Query[] queries = DataLoader.getQueries("queries_on_data_lineitem_s1_b10_n10000");
    for (int i = min; i <= max; i += step) {
      String tablePath = "data_lineitem_s1_b" + i;
      System.out.println("try to create report file: " + "query_execution_cost_on_" + tablePath + ".csv");
      File f = new File("query_execution_cost_on_" + tablePath + ".csv");
      if (!f.exists()) f.createNewFile();
      FileWriter fw = new FileWriter(f, true);
      DataTable dataTable = DataLoader.getDataTable(tablePath);
      Replica replica = new Replica(dataTable);
      int curQueryNumber = 0;
      for (Query query : queries) {
        System.out.println("current query id: " + curQueryNumber);
        if (curQueryNumber++ == maxQuery) break;
        BigDecimal evaluateCost = CostModel.cost(replica, query);
        System.out.print("eva cost: " + new BigDecimal(evaluateCost.doubleValue()).setScale(10, BigDecimal.ROUND_HALF_UP) + " ");
        String command = Command.getString("default_table", query);
        long start = System.nanoTime();
        session.execute(command);
        long realCost = System.nanoTime() - start;
        System.out.println("real cost: " + realCost);
        fw.write(evaluateCost + "," + realCost + "\n");
      }
      fw.close();
    }
    session.close();
  }
}
