package query;

import java.io.Serializable;
import java.util.Arrays;


// TODO modify comments

/**
 * This class represent a query in the workload. A Query consists of a range query and a
 * group of point value queries. It has 6 attributes in total. Attribute rangeColIndex is
 * the index of column there the range query works on. Attribute colNum is the number of
 * columns this query works on, including point query and  * range query, which normally
 * equals to the column number of data table. Attribute lowerBound and upperBound is the
 * lower bound value and upper bound value of range query. The array, pointVals, records
 * point query values on each column, and the column which will range query working on
 * is also included, but the value is initialized as 0. The order of values in the array
 * is consistent with the column order of original data table. So the total length of the
 * array equals to colNum. Attribute weight indicates the weight of the query.
 */
public class Query implements Serializable {
  private MiniQuery[] originMiniQueries;
  private MiniQuery[] miniQueries;
  private int[] order;
  private double weight;

  private int rangeColIndex;
  private double lowerBound;
  private double upperBound;

  public Query(MiniQuery[] originMiniQueries, double weight) {
    this.originMiniQueries = new MiniQuery[originMiniQueries.length];
    this.miniQueries = new MiniQuery[originMiniQueries.length];
    this.order = new int[originMiniQueries.length];
    for (int i = 0; i < originMiniQueries.length; i++)
      this.order[i] = i;
    System.arraycopy(originMiniQueries, 0, this.originMiniQueries, 0, this.originMiniQueries.length);
    System.arraycopy(originMiniQueries, 0, this.miniQueries, 0, this.originMiniQueries.length);
    this.weight = weight;
    rangeColIndex = -1;
    for (int i = 0; i < miniQueries.length; i++) {
      if (miniQueries[i] instanceof RangeQuery) {
        rangeColIndex = i;
        lowerBound = ((RangeQuery)miniQueries[i]).getLowerBound();
        upperBound = ((RangeQuery)miniQueries[i]).getUpperBound();
        break;
      }
    }

  }

  /**
   * Copy constructor, deep copy
   *
   * @param q, another query
   */
  public Query(Query q) {
    this.weight = q.weight;
    this.originMiniQueries = new MiniQuery[q.originMiniQueries.length];
    this.miniQueries = new MiniQuery[q.miniQueries.length];
    this.order = new int[q.order.length];
    System.arraycopy(q.originMiniQueries, 0, this.originMiniQueries, 0, originMiniQueries.length);
    System.arraycopy(q.miniQueries, 0, this.miniQueries, 0, q.miniQueries.length);
    System.arraycopy(q.order, 0, this.order, 0, q.order.length);
    this.rangeColIndex = q.rangeColIndex;
    this.lowerBound = q.lowerBound;
    this.upperBound = q.upperBound;
  }

  /**
   * Transform a query, according to given column order.
   *
   * @param order, the order of columns (in a replica)
   * @return
   */
  public Query getQuery(int[] order) {
    if (order.length != originMiniQueries.length) throw new IllegalArgumentException();
    MiniQuery[] newMiniQueries = new MiniQuery[this.originMiniQueries.length];
    for (int i = 0; i < order.length; i++)
      newMiniQueries[i] = originMiniQueries[order[i]];
    return new Query(newMiniQueries, this.weight);
//
//    int newRangeColIdx = -1;
//    for (int i = 0; i < order.length; i++) {
//      if (order[i] == rangeColIndex) {
//        newRangeColIdx = i;
//        break;
//      }
//    }
//    if (newRangeColIdx == -1) throw new IllegalArgumentException();
//    double[] newPointVals = new double[order.length];
//    for (int i = 0; i < newPointVals.length; i++) newPointVals[i] = pointVals[order[i]];
//    return new Query(newRangeColIdx, lowerBound, upperBound, newPointVals, weight);
  }

  @Override
  public String toString() {
    return "Query{" +
            "originMiniQueries=" + Arrays.toString(originMiniQueries) +
            ", weight=" + weight +
            '}';
  }


  public int getRangeColIndex() {
    for (int i = 0; i < miniQueries.length; i++) {
      if (miniQueries[i] instanceof RangeQuery) return i;
    }
    return -1;
  }

  public int getColNum() {
    return miniQueries.length;
  }

    public double getUpperBound() {
    return upperBound;
  }

  public double getLowerBound() {
    return lowerBound;
  }
//
//  public double[] getPointVals() {
//    return pointVals;
//  }
//
  public double getWeight() {
    return weight;
  }

  public Query setWeight(double weight) {
    this.weight = weight;
    return this;
  }

  public MiniQuery[] getMiniQueries() {
    return miniQueries;
  }
}
