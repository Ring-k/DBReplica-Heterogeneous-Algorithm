package columnchange.query;

import columnchange.datamodel.DataTable;

import java.util.Random;

public class QueryGenerator {

  Query[] queries;
  DataTable dataTable;

  public QueryGenerator(int numberOfQueries, DataTable dataTable) {
    queries = new Query[numberOfQueries];
    this.dataTable = dataTable;
    generate();
  }

  private void generate() {
    int colNum = dataTable.getColNum();
    Random rand;
    for (int i = 0; i < queries.length; i++) {
      int rangeColIdx = (int) (Math.random() * colNum);
      double min = dataTable.getColHistograms()[rangeColIdx].getMinX();
      double max = dataTable.getColHistograms()[rangeColIdx].getMaxX();
      double lowerBound = Math.random() * (max - min) + min;
      double upperBound = Math.random() * (max - lowerBound) + lowerBound;

      // init point vals
      double[] pntVals = new double[colNum];
      for(int j = 0; j < colNum; j++){
        if(j == rangeColIdx) continue;
        double ptMin = dataTable.getColHistograms()[j].getMinX();
        double ptMax = dataTable.getColHistograms()[j].getMaxX();
        pntVals[j] = Math.random()*(ptMax-ptMin)+ptMin;
      }
      Query q = new Query(rangeColIdx, lowerBound, upperBound, pntVals, 1);
      queries[i] = q;
    }
  }

  public Query[] getQueries() {
    return queries;
  }

}
