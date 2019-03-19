package columnchange;

import columnchange.datamodel.DataTable;
import columnchange.datamodel.Histogram;
import columnchange.query.Query;
import columnchange.replica.Replica;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


public class TestReplica {

  @Test
  public void testOrder() {
    List<Double> c0 = new ArrayList<>(),
            c1 = new ArrayList<>(),
            c2 = new ArrayList<>(),
            c3 = new ArrayList<>(),
            c4 = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      c0.add((double) ((int) (Math.random() * 10 + 1)));
      c1.add((double) ((int) (Math.random() * 10 + 1)));
      c2.add((double) ((int) (Math.random() * 10 + 1)));
      c3.add((double) ((int) (Math.random() * 10 + 1)));
      c4.add((double) ((int) (Math.random() * 10 + 1)));
    }

    Histogram h0 = new Histogram(c0, 10);
    Histogram h1 = new Histogram(c1, 10);
    Histogram h2 = new Histogram(c2, 10);
    Histogram h3 = new Histogram(c3, 10);
    Histogram h4 = new Histogram(c4, 10);

    Histogram[] cols = {h0, h1, h2, h3, h4};
    DataTable dataTable = new DataTable(cols);
    int[] order = {4, 3, 2, 1, 0};
    Replica r = new Replica(dataTable, order);
    assertEquals(h4, r.getDataTable().getColHistograms()[0]);
    assertEquals(h3, r.getDataTable().getColHistograms()[1]);
    assertEquals(h2, r.getDataTable().getColHistograms()[2]);
    assertEquals(h1, r.getDataTable().getColHistograms()[3]);
    assertEquals(h0, r.getDataTable().getColHistograms()[4]);

  }


  @Test
  public void testScanProbability() {
    DataTable dataTable = TestDataTable.generateDataTable();
    double[] pntVals = {2500, 0, 6500};
    Query query = new Query(1, 3000, 7000, pntVals, 1);
    Replica r = new Replica(dataTable);
    assertEquals(0.12 / 1000, r.scanProbability(query), 0.0000001);
  }

  @Test
  public void testEquals() {
    DataTable dataTable = TestDataTable.generateDataTable();
    int[] order = {0, 1, 2};
    int[] order2 = {2, 0, 1};
    Replica r1 = new Replica(dataTable);
    Replica r2 = new Replica(dataTable, order);
    Replica r3 = new Replica(dataTable, order2);

    DataTable dataTable1 = TestDataTable.generateDataTable();
    Replica r4 = new Replica(dataTable1);

    assertTrue(r1.equals(r2));
    assertFalse(r1.equals(r3));
    assertTrue(r1.equals(r4));
  }

  @Test
  public void testResultProbability() {
    DataTable dataTable = TestDataTable.generateDataTable();
    double[] pntVals = {2500, 0, 6500};
    Query query = new Query(1, 3000, 7000, pntVals, 1);
    Replica r = new Replica(dataTable);
    assertEquals(0.3 * 0.4 * 0.6 / 1000000, r.resultProbability(query), 0.0000001);
  }


  /*
  info of the data is shown below
  column 0:
  x coordinate: [1000.0, 2000.0, 3000.0, 4000.0]
  y coordinate: [2, 3, 2, 3]
  probability:  [0.2, 0.3, 0.2, 0.3]
  minX: 1000.0, maxX5000.0, interval length: 1000.0
  column 1:
  x coordinate: [2000.0, 4000.0, 6000.0, 8000.0]
  y coordinate: [1, 2, 3, 4]
  probability:  [0.1, 0.2, 0.3, 0.4]
  minX: 2000.0, maxX10000.0, interval length: 2000.0
  column 2:
  x coordinate: [5000.0, 6000.0, 7000.0, 8000.0]
  y coordinate: [1, 6, 2, 1]
  probability:  [0.1, 0.6, 0.2, 0.1]
  minX: 5000.0, maxX9000.0, interval length: 1000.0
   */



}
