package columnchange;

import columnchange.query.Query;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestQuery {

  @Test
  public void testConstructor() {

    double[] pntVals = {3, 8, 2, 4, 2};
    Query q = new Query(3, 4, 12, pntVals, 1);
    assertEquals(3, q.getRangeColIndex());
    assertEquals(4, q.getLowerBound(), 0);
    assertEquals(12, q.getUpperBound(), 0);
    assertEquals(pntVals, q.getPointVals());
    assertEquals(1, q.getWeight(), 0);
  }

  @Test
  public void testGetQuery() {
    double[] pntVals = {3, 8, 2, 4, 2};
    Query q = new Query(3, 4, 12, pntVals, 1);
    int[] order = {0, 4, 2, 1, 3};
    Query afterOrder = q.getQuery(order);

    double[] newPntVals = {3, 2, 2, 8, 4};

    assertEquals(4, afterOrder.getRangeColIndex());
    assertEquals(4, afterOrder.getLowerBound(), 0);
    assertEquals(12, afterOrder.getUpperBound(), 0);
    assertArrayEquals(newPntVals, afterOrder.getPointVals(), 0);
    assertEquals(1, afterOrder.getWeight(), 0);
  }
}
