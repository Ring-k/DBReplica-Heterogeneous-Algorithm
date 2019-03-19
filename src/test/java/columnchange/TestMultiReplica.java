package columnchange;

import columnchange.datamodel.DataTable;
import columnchange.replica.MultiReplicas;
import columnchange.replica.Replica;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestMultiReplica {

  @Test
  public void testEquals() {
    DataTable dataTable = TestDataTable.generateDataTable();
    int[] order1 = {0, 1, 2};
    int[] order2 = {0, 2, 1};
    int[] order3 = {2, 1, 0};

    Replica r1 = new Replica(dataTable, order1);
    Replica r2 = new Replica(dataTable, order2);
    Replica r3 = new Replica(dataTable, order3);

    MultiReplicas mul1 = new MultiReplicas();
    mul1.add(r1);
    mul1.add(r2);
    mul1.add(r3);

    Replica[] mularr = {r2, r1, r3};
    MultiReplicas mul2 = new MultiReplicas(mularr);

    Map<Replica, Integer> mulmap = new HashMap<>();
    mulmap.put(r1, 1);
    mulmap.put(r3, 1);
    mulmap.put(r2, 1);
    MultiReplicas mul3 = new MultiReplicas(mulmap);

    Replica[] mularr2 = {r1, r1, r2};
    MultiReplicas mul4 = new MultiReplicas(mularr2);

    assertTrue(mul1.equals(mul2));
    assertTrue(mul2.equals(mul3));
    assertFalse(mul1.equals(mul4));

  }

  @Test
  public void testGetSize(){
    DataTable dataTable = TestDataTable.generateDataTable();
    int[] order1 = {0, 1, 2};
    int[] order2 = {0, 2, 1};
    int[] order3 = {2, 1, 0};
    Replica r1 = new Replica(dataTable, order1);
    Replica r2 = new Replica(dataTable, order2);
    Replica r3 = new Replica(dataTable, order3);

    Replica[] mularr1 = {r1, r2, r3};
    MultiReplicas m1 = new MultiReplicas(mularr1);
    assertEquals(3, m1.getReplicaNum());

    Replica[] mularr2 = {r1, r1, r1, r2, r3};
    MultiReplicas m2 = new MultiReplicas(mularr2);
    assertEquals(5, m2.getReplicaNum());

  }

  @Test
  public void testAdd(){
    DataTable dataTable = TestDataTable.generateDataTable();
    int[] order1 = {0, 1, 2};
    int[] order2 = {0, 2, 1};
    int[] order3 = {2, 1, 0};
    Replica r1 = new Replica(dataTable, order1);
    Replica r2 = new Replica(dataTable, order2);
    Replica r3 = new Replica(dataTable, order3);

    MultiReplicas m2 = new MultiReplicas();
    assertEquals(0, m2.getReplicaNum());
    m2.add(r1);
    assertEquals(1, m2.getReplicaNum());
    m2.add(r2);
    assertEquals(2, m2.getReplicaNum());
    m2.add(r3);
    assertEquals(3, m2.getReplicaNum());
    m2.add(r2);
    assertEquals(4, m2.getReplicaNum());
    m2.add(r1);
    assertEquals(5, m2.getReplicaNum());
  }
}
