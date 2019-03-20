package datamodel;

import constant.Constant;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Histogram {

  private double maxX, minX, intervalLength;
  private double[] xCoordinate;
  private int[] yCoordinate;
  private double[] probability;
  private int pointsNum;
  private double step = Constant.histogramStep;

  public Histogram(List<Double> data, int groupNum) {
    // init max and min
    initMaxAndMin(data);

    //init the interval length
    intervalLength = (maxX - minX) / groupNum;
    pointsNum = data.size();

    // init the statistics arrays
    xCoordinate = new double[groupNum];
    yCoordinate = new int[groupNum];
    probability = new double[groupNum];
    for (int i = 0; i < xCoordinate.length; i++)
      xCoordinate[i] = minX + i * intervalLength;
    for (double i : data) yCoordinate[getStartIndex(i)]++;
    for (int i = 0; i < probability.length; i++)
      probability[i] = (double) yCoordinate[i] / pointsNum;
  }

  private void initMaxAndMin(List<Double> data) {
    maxX = data.get(0);
    minX = data.get(0);
    for (double i : data) {
      if (i > maxX) maxX = i;
      if (i < minX) minX = i;
    }
    maxX = maxX + 1;
  }

  private boolean isIn(double val, double lowerBound, double upperBound) {
    return val >= lowerBound && val < upperBound;
  }

  private int getStartIndex(double val) {
    if (val > maxX - 1) return -1;
    if (val < minX) return -1;
    for (int i = xCoordinate.length - 1; i >= 0; i--)
      if (val >= xCoordinate[i]) return i;
    return -1;
  }

  public double getProbability(double val) {
    int index = getStartIndex(val);
    if (index == -1) return 0;
    return probability[index] * (step / intervalLength);
  }


  public double getProbability(double lowerBound, double upperBound) {
    if (lowerBound > upperBound) throw new IllegalArgumentException();
    // point query
    if (lowerBound == upperBound) return getProbability(lowerBound);
    // lower bound greater than max, or upper bound less than min
    if (lowerBound >= maxX + intervalLength || upperBound < minX) return 0;

    int lowerIndex = getStartIndex(lowerBound), upperIndex = getStartIndex(upperBound);
    // lower bound < min
    if (lowerIndex == -1) {
      lowerIndex = 0;
      lowerBound = minX;
    }
    // upper bound > max
    if (upperIndex == -1) {
      upperIndex = xCoordinate.length - 1;
      upperBound = maxX;
    }
    if (lowerIndex == upperIndex) return (upperBound - lowerBound) / intervalLength * probability[lowerIndex];
    double result = 0.0;
    result += ((xCoordinate[lowerIndex + 1] - lowerBound) / intervalLength * probability[lowerIndex]);
    result += ((upperBound - xCoordinate[upperIndex]) / intervalLength * probability[upperIndex]);
    for (int i = lowerIndex + 1; i < upperIndex; i++)
      result += probability[i];
    return result;
  }


  public double getMinX() {
    return minX;
  }

  public double getMaxX() {
    return maxX;
  }

  public double[] getProbability() {
    return probability;
  }

  public double[] getxCoordinate() {
    return xCoordinate;
  }

  public int[] getyCoordinate() {
    return yCoordinate;
  }

  @Override
  public String toString() {
    String ans = "";
    ans += ("x coordinate: " + Arrays.toString(xCoordinate) + "\n");
    ans += ("y coordinate: " + Arrays.toString(yCoordinate) + "\n");
    ans += ("probability:  " + Arrays.toString(probability) + "\n");
    ans += ("minX: " + minX + ", " + "maxX" + maxX + ", " + "interval length: " + intervalLength + "\n");
    return ans;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Histogram histogram = (Histogram) o;
    return Double.compare(histogram.maxX, maxX) == 0 &&
            Double.compare(histogram.minX, minX) == 0 &&
            Double.compare(histogram.intervalLength, intervalLength) == 0 &&
            pointsNum == histogram.pointsNum &&
            Double.compare(histogram.step, step) == 0 &&
            Arrays.equals(xCoordinate, histogram.xCoordinate) &&
            Arrays.equals(yCoordinate, histogram.yCoordinate) &&
            Arrays.equals(probability, histogram.probability);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(maxX, minX, intervalLength, pointsNum, step);
    result = 31 * result + Arrays.hashCode(xCoordinate);
    result = 31 * result + Arrays.hashCode(yCoordinate);
    result = 31 * result + Arrays.hashCode(probability);
    return result;
  }

  public double getIntervalLength() {
    return intervalLength;
  }

  public int getPointsNum() {
    return pointsNum;
  }

  public double getStep() {
    return step;
  }
}
