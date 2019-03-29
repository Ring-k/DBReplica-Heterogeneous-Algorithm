package query;

import datamodel.DataTable;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class QueryGenerator {

  Query[] queries;
  DataTable dataTable;

  public QueryGenerator(int numberOfQueries, DataTable dataTable) throws NoSuchAlgorithmException {
    queries = new Query[numberOfQueries];
    this.dataTable = dataTable;
    generate();
  }


  private void generate() throws NoSuchAlgorithmException {
    int colNum = dataTable.getColNum();
    Random rand = SecureRandom.getInstanceStrong();
    for (int i = 0; i < queries.length; i++) { // for a query
      MiniQuery[] miniQueries = new MiniQuery[colNum];
      for (int j = 0; j < colNum; j++) { //  of each column
        int drawLot = rand.nextInt(99) + 1;
        double minv = dataTable.getColHistograms()[j].getMinX();
        double maxv = dataTable.getColHistograms()[j].getMaxX();
        if (drawLot <= 33) {
          double v = Math.random() * (maxv - minv) + minv;
          miniQueries[j] = new PointQuery(v);
        } else if (drawLot <= 66) {
          double lowerBound = Math.random() * (maxv - minv) + minv;
          double upperBound = Math.random() * (maxv - lowerBound) + lowerBound;
          miniQueries[j] = new RangeQuery(lowerBound, upperBound);
        } else {
          miniQueries[j] = new RangeQuery(minv, maxv);
        }
      }
      queries[i] = new Query(miniQueries, 1);
    }
  }

  public Query[] getQueries() {
    return queries;
  }

}
