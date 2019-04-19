package experiment.cassandra;

import query.MiniQuery;
import query.PointQuery;
import query.Query;
import query.RangeQuery;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Command {

  public static String getString(String tableName, Query query) {
    String[] clusteringKeysNames = "L_ORDERKEY,L_PARTKEY,L_SUPPKEY,L_LINENUMBER,L_SHIPDATE,L_COMMITDATE,L_RECEIPTDATE".split(",");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String cmd = "select count(*) from " + tableName + " where pkey = 1 and ";
    MiniQuery[] miniQueries = query.getMiniQueries();
    for (int i = 0; i < miniQueries.length; i++) {
      if (miniQueries[i] instanceof PointQuery) {
        String colVal = i >= 4
                ? "\'" + sdf.format(new Date((long) ((PointQuery) (miniQueries[i])).getValue())) + "\'"
                : String.valueOf((int) ((PointQuery) (miniQueries[i])).getValue());
        cmd += clusteringKeysNames[i] + "=" + colVal + " and ";
      } else {
        String lowerbound = i >= 4
                ? "\'" + sdf.format(new Date((long) ((RangeQuery) (miniQueries[i])).getLowerBound())) + "\'"
                : String.valueOf((int) ((RangeQuery) (miniQueries[i])).getLowerBound());
        String upperbound = i >= 4
                ? "\'" + sdf.format(new Date((long) ((RangeQuery) (miniQueries[i])).getUpperBound())) + "\'"
                : String.valueOf((int) ((RangeQuery) (miniQueries[i])).getUpperBound());
        cmd += clusteringKeysNames[i] + " >= " + lowerbound + " and " + clusteringKeysNames[i] + " <= " + upperbound + " and ";
      }
    }
    cmd = (cmd.substring(0, cmd.length() - 4) + "allow filtering;\n");
    return cmd;
  }

}
