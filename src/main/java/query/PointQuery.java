package query;

import java.io.Serializable;

public class PointQuery implements  MiniQuery, Serializable {
  double value;
  public PointQuery(double v){
    this.value = v;
  }

  public double getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "PointQuery{" +
            "value=" + value +
            '}';
  }
}
