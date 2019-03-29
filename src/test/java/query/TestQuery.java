package query;

import static org.junit.Assert.*;

import datamodel.DataTable;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;

public class TestQuery {
  @Test
  public void TestConstructor() {
    MiniQuery[] qs = {new PointQuery(2), new PointQuery(3426), new RangeQuery(76, 123), new PointQuery(1234), new RangeQuery(46, 435)};
    Query q = new Query(qs, 1);
    System.out.println(q.toString());

    assertEquals(76, q.getLowerBound(), 0);
    assertEquals(123, q.getUpperBound(), 0);
  }

  @Test
  public void testGetQueryUsingOrder() {
    MiniQuery[] qs = {new PointQuery(2), new PointQuery(3426), new RangeQuery(76, 123), new PointQuery(1234), new RangeQuery(46, 435)};

    int[] order = {4, 1, 3, 2, 0};
    Query q = new Query(qs, 1).getQuery(order);

    assertEquals(46, q.getLowerBound(), 0);
    assertEquals(435, q.getUpperBound(), 0);
  }

  @Test
  public void testGenerateQuery() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    DataTable dataTable = getDataTable(7);
    QueryGenerator qg = new QueryGenerator(1000, dataTable);
    Query[] q = qg.getQueries();
    for (Query qu : q) {
      System.out.println(qu.toString());
    }
  }

  static DataTable getDataTable(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data_table_" + colNums));
    return (DataTable) ois.readObject();
  }
}
