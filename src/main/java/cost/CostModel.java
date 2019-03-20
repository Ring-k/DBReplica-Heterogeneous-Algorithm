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

  public static BigDecimal cost(Replica replica, Query query) {
    return replica.scanRows(query).multiply(Constant.COST_SCALE);
  }

  public static BigDecimal cost(MultiReplicas multiReplicas, Query query) {
    BigDecimal ans = null;
    for (Replica r : multiReplicas.getReplicas().keySet()) {
      BigDecimal cost = cost(r, query);
      if(ans == null) ans = cost;
      else if(ans .compareTo(cost) == 1) ans = cost;
    }
    return ans;
  }

  public static BigDecimal cost(MultiReplicas multiReplicas, Query[] queries){
    BigDecimal ans = new BigDecimal("0");
    for(Query q : queries)
      ans = ans.add(cost(multiReplicas, q));
    return ans;
  }

  // TODO consiter cost
  public static BigDecimal cost(Replica[] multiReplicas, Query query){
    BigDecimal ans = new BigDecimal("0");
    for(Replica r : multiReplicas)
      ans = ans.add(cost(r, query));
    return ans;
  }

}
