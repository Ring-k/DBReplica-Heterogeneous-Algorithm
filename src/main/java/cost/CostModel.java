package cost;

import javafx.util.Pair;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;
import constant.Constant;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class CostModel {

  /**
   * this is a constant class, which only provides static methods.
   */
  private CostModel() {
  }

  /**
   * Calculate cost of evaluating a query on a replica. Model the cost as
   * cost = scan_rows * cost_scale.
   *
   * @param replica, the replica
   * @param query,   the query
   * @return the cost
   */
  public static BigDecimal cost(Replica replica, Query query) {
    return replica.scanRows(query).multiply(Constant.COST_SCALE);
  }

  public static BigDecimal cost(Replica replica, Query[] queries){
    BigDecimal ans = new BigDecimal("0");
    for(Query q : queries)
      ans = ans.add(cost(replica, q));
    return ans;
  }

  /**
   * Calculate the cost of evaluating a query on multi-replica. Calculate the cost of query
   * evaluated on each replca, and take the minimum value as the result
   *
   * @param multiReplicas, the multi-repilica
   * @param query,         the query
   * @return the cost
   */
//  public static BigDecimal cost(MultiReplicas multiReplicas, Query query) {
//    BigDecimal ans = null;
//    for (Replica r : multiReplicas.getReplicas().keySet()) {
//      BigDecimal cost = cost(r, query);
//      if(ans == null) ans = cost;
//      else if(ans .compareTo(cost) > 0) {
//        ans = cost;
//      }
//    }
//    return ans;
//  }

  public static Pair<Replica, BigDecimal> cost(MultiReplicas multiReplicas, Query query) {
    BigDecimal ans = null;
    Replica replica = null;
    for (Replica r : multiReplicas.getReplicas().keySet()) {
      BigDecimal cost = cost(r, query);
      if (ans == null || ans.compareTo(cost) > 0) {
        ans = cost;
        replica = r;
      }
    }
    return new Pair<>(replica, ans);
  }




  public static BigDecimal cost(MultiReplicas multiReplicas, Query[] queries) {

    Map<Replica, BigDecimal> ans = new HashMap<>();
//    BigDecimal ans = new BigDecimal("0");
    for(Replica r : multiReplicas.getReplicas().keySet())
      ans.put(r, new BigDecimal("0"));
    for (Query q : queries) {
      Pair<Replica, BigDecimal> costPair = cost(multiReplicas, q);
      BigDecimal cost = ans.get(costPair.getKey()).add(costPair.getValue());
      ans.put(costPair.getKey(), cost);
    }

    for(Replica r : ans.keySet()){
      int replicaNumber = multiReplicas.getReplicas().get(r);
      if(replicaNumber != 1){
        ans.put(r, ans.get(r).divide(BigDecimal.valueOf(replicaNumber), 100, BigDecimal.ROUND_HALF_UP));
      }
    }

    BigDecimal res = null;
    for(BigDecimal n : ans.values()){
      if(res == null || res.compareTo(n) < 0)
        res = n;
    }
    return res;
  }

//  // TODO consiter cost
//  public static BigDecimal cost(Replica[] multiReplicas, Query query){
//    BigDecimal ans = new BigDecimal("0");
//    for(Replica r : multiReplicas)
//      ans = ans.add(cost(r, query));
//    return ans;
//  }

}
