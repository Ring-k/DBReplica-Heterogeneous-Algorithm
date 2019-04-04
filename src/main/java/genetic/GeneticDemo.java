package genetic;

import cost.CostModel;
import datamodel.DataTable;
import query.Query;
import replica.MultiReplicas;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;

public class GeneticDemo {
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
    Genetic g = new Genetic(dataTable, queries, 3,
            1000, 500, 20000,
            0.5, 0.01, 2, true);
    MultiReplicas multiReplicas = g.optimal();
    System.out.println(CostModel.cost(multiReplicas, queries));
  }
}
