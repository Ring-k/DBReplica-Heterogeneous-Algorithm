package datamodel;

import constant.Constant;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This class record histograms info of a column.
 */
public class Histogram implements Serializable {

  private double maxX;
  private double minX;
  private double intervalLength;
  private double[] xCoordinate;
  private int[] yCoordinate;
  private double[] probability;
  private int pointsNum;
  private double step = Constant.HISTOGRAM_STEP;
  private int groupNumber;

  /**
   * Constructor
   *
   * @param data,     a list of column data, all data in a column
   * @param groupNum, the number of ranges need to do statistic work, number of ranges
   */
  public Histogram(List<Double> data, int groupNum) {
    initMaxAndMin(data);
    intervalLength = (maxX - minX) / groupNum;
    pointsNum = data.size();
    xCoordinate = new double[groupNum];
    yCoordinate = new int[groupNum];
    probability = new double[groupNum];

    for (int i = 0; i < xCoordinate.length; i++)
      xCoordinate[i] = minX + i * intervalLength;
    for (double i : data)
      yCoordinate[getStartIndex(i)]++;
    updateProbability();
  }

  /**
   * Constructor for adding items
   *
   * @param groupNum
   */
  public Histogram(double min, double max, int groupNum) {
    minX = min;
    maxX = max + 1;
    intervalLength = (maxX - minX) / groupNum;
    pointsNum = 0;
    xCoordinate = new double[groupNum];
    yCoordinate = new int[groupNum];
    probability = new double[groupNum];
    for (int i = 0; i < xCoordinate.length; i++)
      xCoordinate[i] = minX + i * intervalLength;
    this.groupNumber = groupNum;
  }

  public void add(double val) {
    yCoordinate[getStartIndex(val)]++;
  }

  public void updateProbability() {
    for (int i = 0; i < probability.length; i++)
      probability[i] = (double) yCoordinate[i] / pointsNum;
  }


  /**
   * Initialize max and min value of the histogram. Traverse all data in the list, get the maximum and minimum
   * value of it.
   *
   * @param data, a list of data
   */
  private void initMaxAndMin(List<Double> data) {
    maxX = data.get(0);
    minX = data.get(0);
    for (double i : data) {
      if (i > maxX) maxX = i;
      if (i < minX) minX = i;
    }
    maxX = maxX + 1;
  }


  /**
   * Given a data, get the start value of the range where the data is in.
   *
   * @param val, the value of data
   * @return the start value of the range, -1 if not found
   */
  private int getStartIndex(double val) {
    if (val > maxX - 1) return -1;
    if (val < minX) return -1;
    for (int i = xCoordinate.length - 1; i >= 0; i--)
      if (val >= xCoordinate[i]) return i;
    return -1;
  }

  /**
   * Evaluate the probability of getting a point data in the column, according to column histogram.
   * Assume value in range [x[i], x[i+1]) obeys mean value distribution, and the range  [x[i], x[i+1])
   * is divided by tiny step. Assume the probability of getting data point equals to accessing the step.
   *
   * @param val, value of data
   * @return probability of getting the data
   */
  public double getProbability(double val) {
    int index = getStartIndex(val);
    if (index == -1) return 0;
    return probability[index] * (step / intervalLength);
  }


  /**
   * Evaluate the probability of assessing a range of data. First calculate the probability at two
   * ends, from lower bound of the range to nearest greater spitting point, from lower bound of the range
   * to nearest less point.The calculate the probability from two splitting points.
   *
   * @param lowerBound, lower bound of the range
   * @param upperBound, upper bound of the range
   * @return the probability
   */
  public double getProbability(double lowerBound, double upperBound) {
    if (lowerBound > upperBound) throw new IllegalArgumentException();
    if (lowerBound == upperBound) return getProbability(lowerBound);
    if (lowerBound >= maxX + intervalLength || upperBound < minX) return 0.0;
    if (lowerBound == minX && upperBound == maxX) return 1.0;
    int lowerIndex = getStartIndex(lowerBound);
    int upperIndex = getStartIndex(upperBound);
    if (lowerIndex == -1) {
      lowerIndex = 0;
      lowerBound = minX;
    }
    if (upperIndex == -1) {
      upperIndex = xCoordinate.length - 1;
      upperBound = maxX;
    }
    if (lowerIndex == upperIndex)
      return (upperBound - lowerBound) / intervalLength * probability[lowerIndex];
    double result = 0.0;
    result += ((xCoordinate[lowerIndex + 1] - lowerBound)
            / intervalLength * probability[lowerIndex]);
    result += ((upperBound - xCoordinate[upperIndex])
            / intervalLength * probability[upperIndex]);
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

  // not allowed to use
  public void setPointsNum(int n) {
    int c = n / pointsNum;
    this.pointsNum = n;
    for (int i = 0; i < yCoordinate.length; i++)
      yCoordinate[i] *= c;
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

  public int getPointsNum() {
    return pointsNum;
  }
}
