package datamodel;

import constant.Constant;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

/**
 * Info of the data table
 */
public class DataTable implements Serializable {
  private BigDecimal rowNum;
  private int colNum;
  private Histogram[] colHistograms;

  /**
   * Copy constructor. Construct an new DataTable instance using another DataTable instance.
   * @param dataTable, another data table
   */
  public DataTable(DataTable dataTable) {
    rowNum = dataTable.getRowNum();
    colHistograms = dataTable.getColHistograms().clone();
    colNum = dataTable.getColNum();
  }

  /**
   * Constructor, use a collection of histogram to construct a data table.
   * @param histo, a collection of column data histograms, ordered by column index
   */
  public DataTable(Histogram[] histo) {
    rowNum = Constant.ROW_NUM;
    colNum = histo.length;
    colHistograms = histo;
  }

  /**
   * Get an array of histograms according to given order
   * @param order, order of indexes
   * @return an array of column histograms
   */
  private Histogram[] getColHistograms(int[] order) {
    for (int i : order)
      if (i >= colHistograms.length || i < 0)
        throw new IllegalArgumentException();
    Histogram[] res = new Histogram[order.length];
    for (int i = 0; i < res.length; i++)
      res[i] = colHistograms[order[i]];
    return res;
  }

  /**
   * Generate new DataTable given column orders
   * @param order, an array of integer, order of indexes
   * @return a new DataTable, with it's same values, but in given order
   */
  public DataTable getDataTable(int[] order) {
    return new DataTable(getColHistograms(order));
  }


  @Override
  public String toString() {
    String str = "";
    for (int i = 0; i < colHistograms.length; i++)
      str += ("column " + i + ":\n" + colHistograms[i].toString());
    return str;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DataTable dataTable = (DataTable) o;
    return colNum == dataTable.colNum
            && Objects.equals(rowNum, dataTable.rowNum)
            && Arrays.equals(colHistograms, dataTable.colHistograms);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(rowNum, colNum);
    result = 31 * result + Arrays.hashCode(colHistograms);
    return result;
  }

  public BigDecimal getRowNum() {
    return rowNum;
  }

  public int getColNum() {
    return colNum;
  }

  public Histogram[] getColHistograms() {
    return colHistograms;
  }

//  public void setRowNum(BigDecimal rowNum) {
//    this.rowNum = rowNum;
//    for(int i = 0; i < colHistograms.length; i++){
//      colHistograms[i].setPointsNum(rowNum);
//    }
//  }
}
