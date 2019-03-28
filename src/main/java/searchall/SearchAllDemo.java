package searchall;

import constant.Constant;
import datamodel.DataTable;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;

import java.io.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class SearchAllDemo {

  static Query[] getQueries(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("queries_" + colNums));
    return (Query[]) ois.readObject();
  }

  static DataTable getDataTable(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data_table_" + colNums));
    return (DataTable) ois.readObject();
  }

  public static void main(String args[]) throws IOException, ClassNotFoundException {
    int col = Integer.parseInt(args[0]);
    DataTable dataTable = getDataTable(col);
    Query[] queries = getQueries(col);
    SearchAll sa = new SearchAll(dataTable, queries);
    Constant.REPLICA_NUMBER = 1;
    Replica r = sa.optimalReplica();
    List<BigDecimal> history = sa.getHistory();
    System.out.println(Arrays.toString(r.getOrder()));
    System.out.println(sa.getOptimalCost());

    File f = new File("search_all_history1.csv");
    if(!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    for( BigDecimal d : history)
      fw.write(d.toString()+"\n");
    fw.flush();
    fw.close();
  }
}
