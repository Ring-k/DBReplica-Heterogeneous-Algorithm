//import cost.CostModel;
//import dataloader.DataLoader;
//import datamodel.DataTable;
//import query.Query;
//import org.junit.Test;
//import replica.Replica;
//
//
//import java.io.IOException;
//
//public class TestReplica {
//
//  @Test
//  public void testReplica() throws IOException, ClassNotFoundException {
//    DataTable dataTable = DataLoader.getDataTable();
//    Query[] queries = DataLoader.getQueries();
//    System.out.println(dataTable.toString());
//
//    int[] order1 = {4, 6, 1, 5, 3, 2, 0};
//    Replica r1 = new Replica(dataTable, order1);
//
//    int[] order2 = {2, 1, 6, 5, 3, 0, 4};
//    Replica r2 = new Replica(dataTable, order2);
//
//    int[] order3 = {0, 2, 6, 1, 5, 4, 3};
//    Replica r3 = new Replica(dataTable, order3);
//
//
//    for (Query q : queries) {
//      System.out.println(CostModel.cost(r1, q) + " " + CostModel.cost(r2, q) + " " + CostModel.cost(r3, q));
//    }
//  }
//}
