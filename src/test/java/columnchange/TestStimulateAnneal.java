package columnchange;

import columnchange.algorithm.CostModel;
import columnchange.algorithm.StimutaleAnneal;
import columnchange.datamodel.DataTable;
import columnchange.query.Query;
import columnchange.query.QueryGenerator;
import columnchange.replica.MultiReplicas;
import columnchange.replica.Replica;

import static columnchange.algorithm.StimutaleAnneal.generateNewMultiReplica;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.util.Arrays;

import static columnchange.TestDataTable.generateDataTable;
import static columnchange.algorithm.StimutaleAnneal.generateNewReplica;

public class TestStimulateAnneal {




  @Test
  public void testGenerateNewReplica() {
    for(int i = 0; i < 100; i++) {
      DataTable dataTable = generateDataTable();
      int[] order = {0, 1, 2};
      Replica r1 = new Replica(dataTable, order);
      Replica r2 = generateNewReplica(r1);
      System.out.print("r1 " + Arrays.toString(r1.getOrder()) + " ");
      System.out.println("r2 " + Arrays.toString(r2.getOrder()));
      r1 = null;
      assertNotEquals(null, r2);
    }
  }

  @Test
  public void testGenerateNewMultiReplica() {
    for(int i = 0; i < 10000; i++){
      DataTable dataTable = generateDataTable();
      int[] order1 = {0, 1, 2};
      int[] order2 = {1, 0, 2};
      int[] order3 = {1, 2, 0};
      MultiReplicas m = new MultiReplicas();
      m.add(new Replica(dataTable, order1))
              .add(new Replica(dataTable, order2))
              .add(new Replica(dataTable, order3));
      MultiReplicas exp = new MultiReplicas();
      exp.add(new Replica(dataTable, order1))
              .add(new Replica(dataTable, order2))
              .add(new Replica(dataTable, order3));
      MultiReplicas m1 = generateNewMultiReplica(m);
      MultiReplicas m2 = generateNewMultiReplica(m);
      assertEquals(exp, m);
      assertNotEquals(exp, m1);
      assertNotEquals(exp, m2);
    }
  }

  @Test
  public void testOptimal() throws IOException {
    DataTable dataTable = generateDataTable();
    Query[] queries = new QueryGenerator(1000, dataTable).getQueries();

    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("queries"));
    oos.writeObject(queries);
    oos.close();

    StimutaleAnneal sa = new StimutaleAnneal(dataTable, queries);
    MultiReplicas m = sa.optimal();
    System.out.println("Solution: " + m.toString());
    System.out.println("Least Cost: " + sa.getCost());
    System.out.println(Arrays.toString(sa.getHistory().toArray()));


  }

  @Test
  public void testZeros() throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("queries"));
    Query[] queries = (Query[]) ois.readObject();

    DataTable dataTable = generateDataTable();
    int[] order1 = {1, 0, 2};
    int[] order2 = {2, 1, 0};
    int[] order3 = {0, 1, 2};
    Replica r1 = new Replica(dataTable, order1);
    Replica r2 = new Replica(dataTable, order2);
    Replica r3 = new Replica(dataTable, order3);
    MultiReplicas m = new MultiReplicas();
    m.add(r1).add(r2).add(r3);

    BigDecimal cost = CostModel.cost(m, queries);
    System.out.println(cost.setScale(10, BigDecimal.ROUND_HALF_UP).toString());


  }
}