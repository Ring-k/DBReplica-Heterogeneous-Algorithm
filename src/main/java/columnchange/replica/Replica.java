package columnchange.replica;

import columnchange.datamodel.DataTable;
import columnchange.query.Query;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

public class Replica {
  private DataTable dataTable;
  private final DataTable originalDataTable;
  private int[] order;


  public Replica(DataTable dataTable, int[] order) {
    this.order = order;
    this.originalDataTable = new DataTable(dataTable);
    this.dataTable = originalDataTable.getDataTable(order);
  }

  public Replica(Replica r) {
    this.dataTable = new DataTable(r.getDataTable());
    this.originalDataTable = new DataTable(r.getOriginalDataTable());
    this.order = new int[r.getOrder().length];
    System.arraycopy(r.getOrder(), 0, order, 0, order.length);
  }


  public Replica(DataTable dataTable) {
    this.order = new int[dataTable.getColNum()];
    for (int i = 0; i < order.length; i++) order[i] = i;
    this.dataTable = new DataTable(dataTable);
    this.originalDataTable = new DataTable(dataTable);
  }

  /**
   * a query on this replica, the query is original query, before order mapping
   *
   * @param query
   * @return
   */
  public double scanProbability(Query query) {
    if (query.getColNum() != dataTable.getColHistograms().length)
      throw new IllegalArgumentException();
    Query afterOrder = query.getQuery(order);
    double res = 1.0;
    for (int i = 0; i < afterOrder.getRangeColIndex(); i++)
      res *= dataTable.getColHistograms()[i]
              .getProbability(afterOrder.getPointVals()[i]);

    res *= dataTable.getColHistograms()[afterOrder.getRangeColIndex()]
            .getProbability(afterOrder.getLowerBound(), afterOrder.getUpperBound());
    return res;
  }

  public double resultProbability(Query query) {
    if (query.getColNum() != dataTable.getColHistograms().length)
      throw new IllegalArgumentException();
    Query afterOrder = query.getQuery(order);
    double res = scanProbability(query);
    for (int i = afterOrder.getRangeColIndex() + 1; i < dataTable.getColHistograms().length; i++)
      res *= dataTable.getColHistograms()[i]
              .getProbability(afterOrder.getPointVals()[i]);
    return res;
  }

  public BigDecimal scanRows(Query query) {
    return dataTable.getRowNum().multiply(BigDecimal.valueOf(scanProbability(query)));
  }

  public BigDecimal resultRows(Query query) {
    return dataTable.getRowNum().multiply(BigDecimal.valueOf(resultProbability(query)));
  }

  @Override
  public String toString() {
    String str = "";
    str += "original data table: \n" + originalDataTable.toString();
    str += ">>>> order: " + Arrays.toString(order) + "\n";
    str += "current data table: \n" + dataTable.toString();
    return str;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Replica replica = (Replica) o;
    return Objects.equals(dataTable, replica.dataTable) &&
            Objects.equals(originalDataTable, replica.originalDataTable) &&
            Arrays.equals(order, replica.order);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(dataTable, originalDataTable);
    result = 31 * result + Arrays.hashCode(order);
    return result;
  }

  public DataTable getDataTable() {
    return dataTable;
  }

  public DataTable getOriginalDataTable() {
    return originalDataTable;
  }

  public int[] getOrder() {
    return order;
  }

}
