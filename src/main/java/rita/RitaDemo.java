package rita;

import constant.Constant;
import cost.CostModel;
import datamodel.DataTable;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;
import searchall.SearchAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;

public class RitaDemo {

  static Query[] getQueries(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("queries_" + colNums));
    return (Query[]) ois.readObject();
  }

  static DataTable getDataTable(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data_table_" + colNums));
    return (DataTable) ois.readObject();
  }

  public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

    DataTable dataTable = getDataTable(7);
    Query[] queries = getQueries(7);

    Replica replica = new SearchAll(dataTable, queries).optimalReplica();

    for (double i = 0.1; i <= 1; i += 0.1) {
      Rita rt = new Rita(dataTable, queries, 3).initSolution(replica);
      double cost = CostModel.cost(rt.optimal(), queries).doubleValue();
      System.out.println(i + ", " + cost);
    }
  }
}
