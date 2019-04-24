package experiment.preprocess;

import dataloader.DataLoader;
import datamodel.DataTable;
import query.Query;
import query.QueryGenerator;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class CreateQueries {
  public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    for (int i = 1; i <= 5; i++) {
      DataTable dataTable = DataLoader.getDataTable("../data/lineitem_s" + i + ".csv.obj");
      Query[] queries = new QueryGenerator(1000, dataTable).getQueries();
      DataLoader.serialize(queries, "queries_s" + i);
    }
  }
}
