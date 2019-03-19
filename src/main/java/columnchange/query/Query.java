package columnchange.query;

import java.io.Serializable;
import java.util.Arrays;

public class Query implements Serializable {

  private int rangeColIndex;
  private int colNum;
  private double upperBound, lowerBound;
  private double[] pointVals;// size = colNum, rangeColIndex is taken by some nonsense value
  private double weight;

  public Query(int rangeColIndex, double lowerBound, double upperBound, double[] pointVals, double weight){
    this.rangeColIndex = rangeColIndex;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.pointVals = pointVals;
    this.colNum = pointVals.length;
    this.weight = weight;
  }

  public Query getQuery(int[] order){
    if(order.length != colNum) throw new IllegalArgumentException();
    int newRangeColIdx = -1;
    for(int i = 0; i < order.length; i++) {
      if (order[i] == rangeColIndex) {
        newRangeColIdx = i;
        break;
      }
    }
    if(newRangeColIdx == -1) throw new IllegalArgumentException();
    double[] newPointVals = new double[order.length];
    for(int i = 0; i < newPointVals.length; i++) newPointVals[i] = pointVals[order[i]];
    return new Query(newRangeColIdx, lowerBound, upperBound, newPointVals, weight);
  }

  @Override
  public String toString() {
    return "Query{" +
            "rangeColIndex=" + rangeColIndex +
            ", colNum=" + colNum +
            ", lowerBound=" + lowerBound +
            ", upperBound=" + upperBound +
            ", pointVals=" + Arrays.toString(pointVals) +
            ", weight=" + weight +
            '}';
  }

  public int getRangeColIndex() {
    return rangeColIndex;
  }

  public int getColNum() {
    return colNum;
  }

  public double getUpperBound() {
    return upperBound;
  }

  public double getLowerBound() {
    return lowerBound;
  }

  public double[] getPointVals() {
    return pointVals;
  }

  public double getWeight() {
    return weight;
  }

  public void setRangeColIndex(int rangeColIndex) {
    this.rangeColIndex = rangeColIndex;
  }

  public void setColNum(int colNum) {
    this.colNum = colNum;
  }

  public void setUpperBound(double upperBound) {
    this.upperBound = upperBound;
  }

  public void setLowerBound(double lowerBound) {
    this.lowerBound = lowerBound;
  }

  public void setPointVals(double[] pointVals) {
    this.pointVals = pointVals;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }
}
