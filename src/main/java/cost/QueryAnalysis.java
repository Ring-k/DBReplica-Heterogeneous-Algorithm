package cost;

import query.MiniQuery;
import query.Query;
import query.RangeQuery;

import java.util.Arrays;
import java.util.Comparator;

public class QueryAnalysis {

  /**
   * This is a class only providing static methods, should not be initialized.
   */
  private QueryAnalysis() {
  }

  /**
   * Analyze range query numbers on each column.
   *
   * @param queries all queries in the workload.
   * @return an array of integer, for i-th element of the array record number of range queries on
   * i-th column of the original table.
   */
  public static int[] getRangeQueryNumber(Query[] queries) {
    if (queries == null || queries.length == 0)
      throw new IllegalArgumentException();
    int[] res = new int[queries[0].getColNum()];
    for (int i = 0; i < res.length; i++) res[i] = 0;
    for (Query q : queries) {
      MiniQuery[] miniQueries = q.getMiniQueries();
      for (int i = 0; i < miniQueries.length; i++) {
        if (miniQueries[i] instanceof RangeQuery) {
          System.out.print(1);
          res[i]++;
        } else
          System.out.print(0);
      }
      System.out.println();
    }
    return res;
  }

  /**
   * Get the decrement order of columns, ordered by number of range queries on that column.
   *
   * @param queries All queries in the workload
   * @return a decrement order of columns
   */
  public static int[] getRangeQueryNumberOrder(Query[] queries) {
    if (queries == null || queries.length == 0)
      throw new IllegalArgumentException();
    int[] numberOnEachColumn = getRangeQueryNumber(queries);
    Integer[] order = new Integer[numberOnEachColumn.length];
    for (int i = 0; i < order.length; i++) order[i] = i;
    Arrays.sort(order, (o1, o2) -> numberOnEachColumn[o2] - numberOnEachColumn[o1]);
    int[] res = new int[order.length];
    for (int i = 0; i < res.length; i++)
      res[i] = order[i];
    return res;
  }

  /**
   * Get the decrement order of columns, ordered by number of range queries on that column.
   *
   * @param rangeQueryNumber An array of range query number
   * @return a decrement order of columns
   */
  public static int[] getRangeQueryNumberOrder(int[] rangeQueryNumber) {
    Integer[] order = new Integer[rangeQueryNumber.length];
    for (int i = 0; i < order.length; i++) order[i] = i;
    Arrays.sort(order, (o1, o2) -> rangeQueryNumber[o2] - rangeQueryNumber[o1]);
    int[] res = new int[order.length];
    for (int i = 0; i < res.length; i++)
      res[i] = order[i];
    return res;
  }
}
