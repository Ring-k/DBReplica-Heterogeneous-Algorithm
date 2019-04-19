package cost;

import constant.Constant;
import javafx.util.Pair;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;

import java.math.BigDecimal;
import java.util.*;


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

  public static BigDecimal cost(Replica replica, Query[] queries) {
    BigDecimal ans = new BigDecimal("0");
    for (Query q : queries)
      ans = ans.add(cost(replica, q));
    return ans;
  }

//  /**
//   * Calculate the cost of evaluating a query on multi-replica. Calculate the cost of query
//   * evaluated on each replca, and take the minimum value as the result
//   *
//   * @param multiReplicas, the multi-repilica
//   * @param query,         the query
//   * @return the cost
//   */
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

//  /**
//   * Calculate the cost of evaluating a query on multi-replica. Calculate the cost of query
//   * evaluated on each replica, and take the minimum value as the result
//   *
//   * @param multiReplicas, the multi-repilica
//   * @param query,         the query
//   * @return a pair of (Replica, BigDecimal), the first element indicates routing the query to which replica,
//   * the next one indicates the cost of evaluate the query on that replica
//   */
//  public static Pair<Replica, BigDecimal> cost(MultiReplicas multiReplicas, Query query) {
//    BigDecimal ans = null;
//    Replica replica = null;
//    for (Replica r : multiReplicas.getReplicas().keySet()) {
//      BigDecimal cost = cost(r, query);
//      if (ans == null || ans.compareTo(cost) > 0) {
//        ans = cost;
//        replica = r;
//      }
//    }
//    return new Pair<>(replica, ans);
//  }

  /**
   * Calculate the cost of evaluating a query on multi-replica. Calculate the cost of query
   * evaluated on each replica, and take the minimum value as the result
   *
   * @param multiReplicas, the multi-repilica
   * @param query,         the query
   *                       the next one indicates the cost of evaluate the query on that replica
   */
  public static Pair<Set<Replica>, BigDecimal> cost(MultiReplicas multiReplicas, Query query) {

    Replica[] replicas = multiReplicas.getReplicasArray(true);
    BigDecimal[] costs = new BigDecimal[replicas.length];
    for (int i = 0; i < costs.length; i++) costs[i] = CostModel.cost(replicas[i], query);
    BigDecimal[] temp = new BigDecimal[replicas.length];
    System.arraycopy(costs, 0, temp, 0, temp.length);
    Arrays.sort(temp);
    int cnt = 1;
    for (int i = 1; i < temp.length; i++)
      if (temp[i].compareTo(temp[0]) == 0) cnt++;
    Set<Replica> resSet = new HashSet<>();
    for (int i = 0; i < costs.length; i++)
      if (costs[i].compareTo(temp[0]) == 0) resSet.add(replicas[i]);
    return new Pair<>(resSet, temp[0].divide(BigDecimal.valueOf(cnt), 100, BigDecimal.ROUND_HALF_UP));

  }


//  /**
//   * Get the cost of evaluating a workload on a multi-replica strategy.
//   * This method record a map, Replica -> BigDecimal, where the key is each key in multi-replica, the value is
//   * the coat (workload stress) on that replica. The cost is calculated in an accumulative way, traverse all queries
//   * in the workload, calculate cost(multi-replica, query), and then add the cost of corresponding replica.
//   * If the number of replica is more than 1, the cost will be divided. According to "cask effect", pick the cost
//   * of a replica which has maximum value as the cost of the multi-replica.
//   *
//   * @param multiReplicas, the multi-replica strategy
//   * @param queries,       the workload
//   * @return cost, according to cask effect
//   */
//  public static BigDecimal cost(MultiReplicas multiReplicas, Query[] queries) {
//    Map<Replica, BigDecimal> ans = new HashMap<>();
//    for (Replica r : multiReplicas.getReplicas().keySet())
//      ans.put(r, new BigDecimal("0"));
//    for (Query q : queries) {
//      Pair<Replica, BigDecimal> costPair = cost(multiReplicas, q);
//      BigDecimal cost = ans.get(costPair.getKey()).add(costPair.getValue());
//      ans.put(costPair.getKey(), cost);
//    }
//    for (Replica r : ans.keySet()) {
//      int replicaNumber = multiReplicas.getReplicas().get(r);
//      if (replicaNumber != 1)
//        ans.put(r, ans.get(r).divide(BigDecimal.valueOf(replicaNumber),
//                100, BigDecimal.ROUND_HALF_UP));
//    }
//    BigDecimal res = null;
//    for (BigDecimal n : ans.values())
//      if (res == null || res.compareTo(n) < 0)
//        res = n;
//    return res;
//  }

  /**
   * Get the cost of evaluating a workload on a multi-replica strategy.
   * This method record a map, Replica -> BigDecimal, where the key is each key in multi-replica, the value is
   * the coat (workload stress) on that replica. The cost is calculated in an accumulative way, traverse all queries
   * in the workload, calculate cost(multi-replica, query), and then add the cost of corresponding replica.
   * If the number of replica is more than 1, the cost will be divided. According to "cask effect", pick the cost
   * of a replica which has maximum value as the cost of the multi-replica.
   *
   * @param multiReplicas, the multi-replica strategy
   * @param queries,       the workload
   * @return cost, according to cask effect
   */
  public static BigDecimal cost(MultiReplicas multiReplicas, Query[] queries) {
//    Map<Replica, BigDecimal> ans = new HashMap<>();
//    for (Replica r : multiReplicas.getReplicas().keySet())
//      ans.put(r, new BigDecimal("0"));
//    for (Query q : queries) {
//      Pair<Set<Replica>, BigDecimal> costPair = cost(multiReplicas, q);
//      BigDecimal cost = costPair.getValue();
//      Set<Replica> replicaSet = costPair.getKey();
//      for(Replica replica : replicaSet)
//        ans.put(replica, ans.get(replica).add(cost));
//    }
//    BigDecimal res = null;
//    for (BigDecimal n : ans.values())
//      if (res == null || res.compareTo(n) < 0)
//        res = n;
//    return res;
    return cost(multiReplicas, queries, 1);
  }

  public static BigDecimal cost(MultiReplicas multiReplicas, Query[] queries, int loadBalanceFactor) {
    BigDecimal[] res = costOnEachReplica(multiReplicas, queries, loadBalanceFactor);
    BigDecimal cost = null;
    for (BigDecimal n : res) {
      if (cost == null || cost.compareTo(n) < 0)
        cost = n;
    }
    return cost;
  }

  public static BigDecimal[] costOnEachReplica(MultiReplicas multiReplicas, Query[] queries, int loadBalanceFactor) {
    Replica[] replicas = multiReplicas.getReplicasArray(true);
    BigDecimal[] res = new BigDecimal[replicas.length];
    for (int i = 0; i < res.length; i++) res[i] = new BigDecimal(0);
    for (Query query : queries) {
      BigDecimal[] costArray = getCostArray(replicas, query);
      int[] order = getLeastCostConfOrder(costArray);
      int number = 1;
      for (int i = 1; i < costArray.length; i++) {
        if (costArray[order[i]].compareTo(costArray[order[0]]) == 0)
          number++;
      }
      if (number < loadBalanceFactor) number = loadBalanceFactor;
      for (int i = 0; i < number; i++) {
        res[order[i]] = res[order[i]].add(costArray[order[i]].divide(BigDecimal.valueOf(number), 1000, BigDecimal.ROUND_HALF_UP));
      }
    }
    return res;
  }

  public static BigDecimal[] costOnEachReplica(MultiReplicas multiReplicas, Query[] queries) {
    return costOnEachReplica(multiReplicas, queries, 1);
  }

  /**
   * get an array of cost according to the given replica and the query
   *
   * @param replicas given replica array
   * @param query    given query
   * @return an array of cost
   */
  public static BigDecimal[] getCostArray(Replica[] replicas, Query query) {
    BigDecimal[] res = new BigDecimal[replicas.length];
    for (int i = 0; i < replicas.length; i++)
      res[i] = cost(replicas[i], query);
    return res;
  }

  public static int[] getLeastCostConfOrder(BigDecimal[] costArray) {
    Integer[] order = new Integer[costArray.length];
    for (int i = 0; i < order.length; i++) order[i] = i;
    Arrays.sort(order, Comparator.comparing(o -> costArray[o]));
    int[] res = new int[costArray.length];
    for (int i = 0; i < res.length; i++) res[i] = order[i];
    return res;
  }


//  public static void analysisEachReplica(MultiReplicas multiReplicas, Query[] queries) {
//    Replica[] rs = multiReplicas.getReplicasArray(true);
//    System.out.println(multiReplicas.getOrderString());
//    for (int i = 0; i < queries.length; i++) {
//      for (int j = 0; j < rs.length; j++) {
//        System.out.println("query" + i + " replica" + j + " " + cost(rs[j], queries[i]));
//      }
//    }
//    Map<Replica, BigDecimal> ans = new HashMap<>();
//    for (Replica r : multiReplicas.getReplicas().keySet())
//      ans.put(r, new BigDecimal("0"));
//    for (Query q : queries) {
////      Pair<Replica, BigDecimal> costPair = cost(multiReplicas, q);
////      BigDecimal cost = ans.get(costPair.getKey()).add(costPair.getValue());
////      ans.put(costPair.getKey(), cost);
//      Pair<Set<Replica>, BigDecimal> costPair = cost(multiReplicas, q);
//      BigDecimal cost = costPair.getValue();
//      Set<Replica> replicaSet = costPair.getKey();
//      for(Replica replica : replicaSet)
//        ans.put(replica, ans.get(replica).add(cost));
//    }
//    for (Replica r : ans.keySet()) {
//      int replicaNumber = multiReplicas.getReplicas().get(r);
//      if (replicaNumber != 1)
//        ans.put(r, ans.get(r).divide(BigDecimal.valueOf(replicaNumber),
//                100, BigDecimal.ROUND_HALF_UP));
//    }
//    int r = 0;
//    for (Map.Entry<Replica, Integer> en : multiReplicas.getReplicas().entrySet()) {
//      for (int i = 0; i < en.getValue(); i++)
//        System.out.print("replica " + r++ + ": " + ans.get(en.getKey()).setScale(10, BigDecimal.ROUND_HALF_UP) + ";  ");
//    }
//    System.out.println();
////    return ans;
//  }

  public static BigDecimal totalCost(MultiReplicas multiReplicas, Query[] queries) {
    BigDecimal ans = new BigDecimal("0");
    for (Query q : queries)
      ans = ans.add(cost(multiReplicas, q).getValue());
    return ans;
  }

}
