package columnchange;

import cost.CostModel;
import datamodel.DataTable;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCostModel {

  @Test
  public void testCostOneQueryOneReplica(){
    DataTable dataTable = TestDataTable.generateDataTable();
    double[] pntVals = {2500, 0, 6500};
    Query query = new Query(1, 3000, 7000, pntVals, 1);
    Replica r = new Replica(dataTable);
    assertEquals(0.3 * 0.4 * 1000, CostModel.cost(r, query).doubleValue(), 0.00001 );

  }

  @Test
  public void testCostOneQueryMultiReplica(){
    DataTable dataTable = TestDataTable.generateDataTable();
    double[] pntVals = {2500, 0, 6500};
    Query query = new Query(1, 3000, 7000, pntVals, 1);
    int[] order1 = {0, 1, 2};
    int[] order2 = {0, 2, 1};
    int[] order3 = {2, 0, 1};
    int[] ordre4 = {2, 1, 0};
    Replica r1 = new Replica(dataTable, order1);
    Replica r2 = new Replica(dataTable, order2);
    Replica r3 = new Replica(dataTable, order3);
    Replica r4 = new Replica(dataTable, ordre4);

    MultiReplicas m = new MultiReplicas();
    m.add(r1).add(r2).add(r3).add(r4);

    assertEquals(0.072, CostModel.cost(m, query).doubleValue(), 0.0001);
  }

  @Test
  public void testCostAfterTransform() {
    DataTable dataTable = TestDataTable.generateDataTable();
    double[] pntVals = {1001,9999, 0};
    Query query = new Query(2, 3000, 7000, pntVals, 1);
    int[] order1 = {0, 1, 2};
    int[] order2 = {0, 2, 1};
    int[] order3 = {2, 0, 1};
    int[] ordre4 = {2, 1, 0};
    Replica r1 = new Replica(dataTable, order1);
    Replica r2 = new Replica(dataTable, order2);
    Replica r3 = new Replica(dataTable, order3);
    Replica r4 = new Replica(dataTable, ordre4);

    System.out.println(CostModel.cost(r1, query).toString());
    System.out.println(CostModel.cost(r2, query).toString());
    System.out.println(CostModel.cost(r3, query).toString());
    System.out.println(CostModel.cost(r4, query).toString());

    System.out.println();

    System.out.println(dataTable.toString());

    Replica r5 = new Replica(dataTable, order1);
    for(int i = 0; i < 20; i++) {
      r5 = StimutaleAnneal.generateNewReplica(r5);
      System.out.println(CostModel.cost(r5, query).toString());
    }
  }
}
