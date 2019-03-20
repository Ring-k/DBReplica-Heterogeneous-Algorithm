package DivergentDesign;

//import columnchange.StimutaleAnneal;
//import com.sun.org.apache.xpath.internal.operations.Div;
//import constant.Constant;
//import cost.CostModel;
//import datamodel.DataTable;
//import divergentdesign.DivgDesign;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import query.Query;
//import query.QueryGenerator;
//import replica.MultiReplicas;
//import replica.Replica;
//
//import java.io.*;
//import java.security.NoSuchAlgorithmException;
//import java.util.Arrays;
//import java.util.List;
//
//import static columnchange.TestDataTable.generateDataTable;

public class TestDivergentDesign {

  @Test
  public void testConstructor() throws NoSuchAlgorithmException {
//    DataTable dataTable = generateDataTable();
//    Query[] queries = new QueryGenerator(1000, dataTable).getQueries();
//    DivgDesign dd = new DivgDesign(dataTable, queries);
//    assertEquals(3, dd.getReplicaNum());
//    assertEquals(2,dd.getLoadBalanceFactor());
//    assertEquals(0.001, dd.getEpsilone(), 0);
//
//    DivgDesign divgDesign = new DivgDesign(dataTable, queries, 5, 2, 30, 0.002);
//    assertEquals(5, divgDesign.getReplicaNum());
//    assertEquals(2, divgDesign.getLoadBalanceFactor());
//    assertEquals(30, divgDesign.getMaxIteration());
//    assertEquals(0.002, divgDesign.getEpsilone(), 0);
//  }
//
//  @Test
//  public void testInitDesign() throws NoSuchAlgorithmException {
//    DataTable dataTable = generateDataTable();
//    Query[] queries = new QueryGenerator(100, dataTable).getQueries();
//
//    DivgDesign dd = new DivgDesign(dataTable, queries);
//    dd.initDesign();
//    for(Query query: queries)
//      System.out.println(query.toString());
//
//    System.out.println();
//
//    for(List<Query> subset : dd.getWorkloadSubsets()){
//      System.out.println(">>>>>>>>>subset:");
//      for(Query q : subset)
//        System.out.println(q.toString());
//    }
//  }
//
//  @Test
//  public void TestRecommendReplica() throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
//    DataTable dataTable = generateDataTable();
//    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("queries"));
//    Query[] queries = (Query[]) ois.readObject();
//    DivgDesign dd = new DivgDesign(dataTable, queries);
//    dd.initDesign();
//    for(List<Query> subset : dd.getWorkloadSubsets()){
//      System.out.println(Arrays.toString(dd.recommandReplica(subset).getOrder()));
//    }
//  }
//
//  @Test
//  public void TestIsIterationTerminate() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
//    DataTable dataTable = generateDataTable();
//    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("queries"));
//    Query[] queries = (Query[]) ois.readObject();
//    DivgDesign dd = new DivgDesign(dataTable,queries,3, 2, 2, 0.01);
//    dd.setTotalCost(1);
//    assertTrue(dd.isIterationTerminate(2, 1));
//    assertFalse(dd.isIterationTerminate(0, 0.00001));
//    assertFalse(dd.isIterationTerminate(0, 1));
//    assertTrue(dd.isIterationTerminate(1, 1));
//    assertFalse(dd.isIterationTerminate(1, 0.1));
//  }
//
//  @Test
//  public void testGetLeastCostConfOrder() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
//    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("datatable"));
//    DataTable dataTable = (DataTable) ois.readObject();
//    ois = new ObjectInputStream(new FileInputStream("queries"));
//    Query[] queries = (Query[]) ois.readObject();
//
//    DivgDesign dd = new DivgDesign(dataTable,queries,3, 2, 2, 0.01);
//    int[] order1 = {0, 1, 2};
//    int[] order2 = {0, 2, 1};
//    int[] order3 = {2, 0, 1};
//    int[] order4 = {2, 1, 0};
//    int[] order5 = {1, 0, 2};
//    int[] order6 = {1, 2, 0};
//    Replica r1 = new Replica(dataTable, order1);
//    Replica r2 = new Replica(dataTable, order2);
//    Replica r3 = new Replica(dataTable, order3);
//    Replica r4 = new Replica(dataTable, order4);
//    Replica r5 = new Replica(dataTable, order5);
//    Replica r6 = new Replica(dataTable, order6);
//    Replica[] multiReplica  ={r1, r2, r3, r4, r5, r6};
//    Query q = queries[213];
//    for(int i = 0; i < 6; i++)
//      System.out.println(i +" " + CostModel.cost(multiReplica[i], q));
//    int[] ans = dd.getLeastCostConfOrder(multiReplica, q);
//    System.out.println(Arrays.toString(ans));
//    assertEquals(ans.length, 2);
//  }
//
//  @Test
//  public void testTotalCost() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
//    ObjectInputStream ois;
//    ois = new ObjectInputStream(new FileInputStream("datatable"));
//    DataTable dataTable = (DataTable) ois.readObject();
//    ois = new ObjectInputStream(new FileInputStream("queries"));
//    Query[] queries = (Query[]) ois.readObject();
//
//    DivgDesign dd = new DivgDesign(dataTable,queries,3, 2, 2, 0.01);
//    int[] order1 = {0, 1, 2};
//    int[] order2 = {0, 2, 1};
//    int[] order3 = {2, 0, 1};
//    int[] order4 = {2, 1, 0};
//    int[] order5 = {1, 0, 2};
//    int[] order6 = {1, 2, 0};
//    Replica r1 = new Replica(dataTable, order1);
//    Replica r2 = new Replica(dataTable, order2);
//    Replica r3 = new Replica(dataTable, order3);
//    Replica r4 = new Replica(dataTable, order4);
//    Replica r5 = new Replica(dataTable, order5);
//    Replica r6 = new Replica(dataTable, order6);
//    Replica[] multiReplica  ={r1, r2, r3, r4, r5, r6};
//
//    System.out.println(dd.totalCost(multiReplica));
//  }
//
//  @Test
//  public void testOptimal() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
//    ObjectInputStream ois;
//    ois = new ObjectInputStream(new FileInputStream("datatable"));
//    DataTable dataTable = (DataTable) ois.readObject();
//    ois = new ObjectInputStream(new FileInputStream("queries"));
//    Query[] queries = (Query[]) ois.readObject();
//
//    DivgDesign dd = new DivgDesign(dataTable, queries);
//    MultiReplicas m = dd.optimal();
//    System.out.println(StimutaleAnneal.getOrder(m));
//
//    StimutaleAnneal sa = new StimutaleAnneal(dataTable, queries, 3);
//    MultiReplicas ml = sa.optimal();
//    System.out.println("Solution: " + ml.toString());
//    System.out.println("Least Cost: " + sa.getOptimalCost());
//
//
//  }

  
}
