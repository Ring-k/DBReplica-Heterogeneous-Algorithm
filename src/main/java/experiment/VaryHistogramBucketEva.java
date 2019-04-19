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
      String result = "bucket = " + i + "\n";
      DataTable dataTable = GenerateDataTable.getDataTableFromCsv("lineitem_s5.csv", cols, i);
      String filePath = "data_lineitem_s5_b" + i;
      DataLoader.serialize(dataTable, filePath);
    }
  }

  public static void generateQueries(DataTable dataTable, int queryNumber, String outputPath) throws NoSuchAlgorithmException {
    QueryGenerator queryGenerator = new QueryGenerator(queryNumber, dataTable);
    Query[] queries = queryGenerator.getQueries();
    DataLoader.serialize(queries, "queries");
  }

  public static void main(String args[]) throws IOException, ParseException, ClassNotFoundException, NoSuchAlgorithmException {

    int min = 10, max = 100, step = 10;

    // 1. generate tables and serialize them on the disk
    generateTables(min, max, step);

    // 2. generate queries and serialize them on the disk
    generateQueries(DataLoader.getDataTable("data_lineitem_s5_b10"), 10000, "queries_on_data_lineitem_s5_b10_n10000");

    String node = "localhost";
    String keyspace = "tpch";
    SocketOptions so = new SocketOptions().setReadTimeoutMillis(10000000).setConnectTimeoutMillis(100000000);
    Cluster cluster = Cluster.builder().addContactPoint(node).withSocketOptions(so).build();
    Session session = cluster.connect(keyspace);
    session.execute("select count(*) from default_table where pkey = 1");


    // 3. for data table on the list
    //        prepare record_file
    //        for each query in queries
    //            1) evaluate cost(table, query)
    //            2) get the command and execute on cassandra
    //            3) write/append the pair (eva_cost, real_cost) to a record_file

    int maxQuery = 1000;
    Query[] queries = DataLoader.getQueries("queries_on_data_lineitem_s5_b10_n10000");
    for (int i = min; i <= max; i += step) {
      String tablePath = "data_lineitem_s5_b" + i;
      File f = new File("query_execution_cost_on_" + tablePath + ".csv");
      if (!f.exists()) f.createNewFile();
      FileWriter fw = new FileWriter(f, true);
      DataTable dataTable = DataLoader.getDataTable(tablePath);
      Replica replica = new Replica(dataTable);
      int curQueryNumber = 0;
      for (Query query : queries) {
        if (curQueryNumber++ == maxQuery) break;
        BigDecimal evaluateCost = CostModel.cost(replica, queries);
        String command = Command.getString("default_table", query);
        long start = System.nanoTime();
        session.execute(command);
        long realCost = System.nanoTime() - start;
        fw.write(evaluateCost + "," + realCost);
      }
      fw.close();
    }
    session.close();
  }
}
