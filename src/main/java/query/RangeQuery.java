package query;

import java.io.Serializable;

public class RangeQuery implements MiniQuery, Serializable {
  double upperBound;
  double lowerBound;

  public RangeQuery(double lowerBound, double upperBound) {
    if (upperBound <= lowerBound)
      throw new IllegalArgumentException("Upper bound should be greater than lower bound");
    this.upperBound = upperBound;
    this.lowerBound = lowerBound;
  }

  public double getUpperBound() {
    return upperBound;
  }

  public double getLowerBound() {
    return lowerBound;
  }

  @Override
  public String toString() {
    return "RangeQuery{" +
            "upperBound=" + upperBound +
            ", lowerBound=" + lowerBound +
            '}';
  }
}
