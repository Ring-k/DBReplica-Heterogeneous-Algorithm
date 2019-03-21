package cost;

import query.Query;
import replica.MultiReplicas;
import replica.Replica;
import constant.Constant;

import java.math.BigDecimal;


public class CostModel {

  /**
   * this is a constant class, which only provides static methods.
   */
  private CostModel() {
  }

  /**
   * Calculate cost of evaluating a query on a replica. Model the cost as
   * cost = scan_rows * cost_scale.
   * @param replica, the replica
   * @param query, the query
   * @return the cost
   */
  public static BigDecimal cost(Replica replica, Query query) {
    return replica.scanRows(query).multiply(Constant.COST_SCALE);
  }

  /**
   * Calculate the cost of evaluating a query on multi-replica. Calculate the cost of query
   * evaluated on each replca, and take the minimum value as the result
   * @param multiReplicas, the multi-repilica
   * @param query, the query
   * @return the cost
   *    */
  public static BigDecimal cost(MultiReplicas multiReplicas, Query query) {
    BigDecimal ans = null;
    for (Replica r : multiReplicas.getReplicas().keySet()) {
      BigDecimal cost = cost(r, query);
      if(ans == null) ans = cost;
      else if(ans .compareTo(cost) > 0) {
        ans = cost;
      }
    }
    return ans;
  }

  public static BigDecimal cost(MultiReplicas multiReplicas, Query[] queries){
    BigDecimal ans = new BigDecimal("0");
    for(Query q : queries)
      ans = ans.add(cost(multiReplicas, q));
    return ans;
  }

//  // TODO consiter cost
//  public static BigDecimal cost(Replica[] multiReplicas, Query query){
//    BigDecimal ans = new BigDecimal("0");
//    for(Replica r : multiReplicas)
//      ans = ans.add(cost(r, query));
//    return ans;
//  }

}
