package columnchange;

import datamodel.DataTable;
import datamodel.Histogram;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestDataTable {
  @Test
  public void testGetHistograms(){
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
    int[] order = {4, 3, 2, 1, 0};
    DataTable dataTable = new DataTable(cols);
    DataTable newDataTable = dataTable.getDataTable(order);
    assertEquals(h4, newDataTable.getColHistograms()[0]);
    assertEquals(h3, newDataTable.getColHistograms()[1]);
    assertEquals(h2, newDataTable.getColHistograms()[2]);
    assertEquals(h1, newDataTable.getColHistograms()[3]);
    assertEquals(h0, newDataTable.getColHistograms()[4]);
  }

  public static  DataTable generateDataTable() {
    List<Double> c0 = new ArrayList<>(),
            c1 = new ArrayList<>(),
            c2 = new ArrayList<>();
    c0.add(1000.0);
    c0.add(1500.0);
    c0.add(4999.0);
    c0.add(4834.0);
    c0.add(4862.0);
    c0.add(2500.0);
    c0.add(2500.0);
    c0.add(2500.0);
    c0.add(3500.0);
    c0.add(3333.0);
    c1.add(2000.0);
    c1.add(9999.0);
    c1.add(8888.0);
    c1.add(9090.0);
    c1.add(9000.0);
    c1.add(6550.0);
    c1.add(6666.0);
    c1.add(6868.0);
    c1.add(4500.0);
    c1.add(4862.0);
    c2.add(5000.0);
    c2.add(8999.0);
    c2.add(6666.0);
    c2.add(6666.0);
    c2.add(6666.0);
    c2.add(6666.0);
    c2.add(6666.0);
    c2.add(6666.0);
    c2.add(7777.0);
    c2.add(7777.0);
    Histogram h0 = new Histogram(c0, 4);
    Histogram h1 = new Histogram(c1, 4);
    Histogram h2 = new Histogram(c2, 4);
    Histogram[] cols = {h0, h1, h2};
    DataTable dataTable = new DataTable(cols);
    return dataTable;
  }
}
