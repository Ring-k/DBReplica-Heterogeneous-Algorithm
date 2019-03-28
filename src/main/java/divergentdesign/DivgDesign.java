package divergentdesign;


import constant.Constant;
import cost.CostModel;
import datamodel.DataTable;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;
import searchall.SearchAll;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;


/**
 * This class implement divergent design algorithm. When suing it, create an instance of
 * the class, and all optimal() method. That method returns optimal solutions (multiple replicas),
 * and current optimalCost and workloadSubsets is consistent with the optimal strategy.
 * Experiments shows that evaluate cost is higher in SA, because we set replica num = 1 in SA here.
 */
public class DivgDesign {

  // input workload and data table
  private Query[] workload;
  private DataTable data;

  // constant variable in divergent design algorithm
  private int replicaNum = Constant.REPLICA_NUMBER;
  private int loadBalanceFactor = Constant.LOAD_BALANCE_FACTOR;
  private int maxIteration = Constant.MAX_ITERATION;
  private double epsilone = Constant.EPSILON;

  // subset of workload, group queries
  private List<Query>[] workloadSubsets;

  // record optimal cost of design
  private double optimalCost;
  private List<Double> history;

  /**
   * Constructor, using customized input variables, replica number, load balancing factor, iteration
   * threshold, epsilon threshold.
   *
   * @param data,              input data table
   * @param queries,           input queries, the workload
   * @param replicaNum,        input replica number
   * @param loadBalanceFactor, input load balance factor
   * @param maxIteration,      input iteration threshold
   * @param epsilon,           input epsilon threshold
   */
  public DivgDesign(DataTable data, Query[] queries,
                    int replicaNum, int loadBalanceFactor, int maxIteration, double epsilon) {
    this.data = new DataTable(data);
    this.replicaNum = replicaNum;
    this.loadBalanceFactor = loadBalanceFactor;
    this.maxIteration = maxIteration;
    this.epsilone = epsilon;
    this.workload = new Query[queries.length];
    System.arraycopy(queries, 0, workload, 0, workload.length);
    workloadSubsets = new List[replicaNum];
    for (int i = 0; i < workloadSubsets.length; i++)
      workloadSubsets[i] = new ArrayList<>();
    this.history = new ArrayList<>();
  }

  /**
   * Constructor, using default constant variables in Constant class.
   *
   * @param dataTable, input data table
   * @param queries,   input queries
   */
  public DivgDesign(DataTable dataTable, Query[] queries) {
    this.data = new DataTable(dataTable);
    this.workload = new Query[queries.length];
    System.arraycopy(queries, 0, workload, 0, workload.length);
    workloadSubsets = new List[replicaNum];
    for (int i = 0; i < workloadSubsets.length; i++)
      workloadSubsets[i] = new ArrayList<>();
    this.history = new ArrayList<>();
  }


  /*
   * get optimal multiple replicas.
   * 1. pick a random m-balanced design
   * 2. ----> start iteration
   * 3. |   get multi replica according to current design
   * 4. |   calculate total cost of current replica
   * 5. |   check if iteration ends, if ends ------------
   * 6. |   group queries again, generate new design    |
   * 7. --  update global record,                       |
   * 8. update global record <---------------------------
   * 9. return multiple replica
   * @return
   */
  public MultiReplicas optimal() throws NoSuchAlgorithmException {
    initDesign();
    Replica[] multiReplicas = new Replica[replicaNum];
    int it = 0;
    double curCost;
    while (true) { // here begins the iteration
      for (int i = 0; i < replicaNum; i++)
        multiReplicas[i] = recommendReplica(workloadSubsets[i]);
      curCost = totalCost(multiReplicas);
      if (isIterationTerminate(it, curCost)) break;
      optimalCost = curCost;
      history.add(optimalCost);
      it++;
      List<Query>[] curSubQueries = new List[replicaNum];
      for (int i = 0; i < curSubQueries.length; i++)
        curSubQueries[i] = new ArrayList<>();
      // add queries to cost least groups
      for (Query query : workload) {
        int[] order = getLeastCostConfOrder(multiReplicas, query);
        for (int i = 0; i < loadBalanceFactor; i++)
          curSubQueries[order[i]].add(new Query(query)
                  .setWeight(query.getWeight() / loadBalanceFactor));
      }
      System.arraycopy(curSubQueries, 0, workloadSubsets, 0, replicaNum);
    }
    optimalCost = curCost;
    history.add(optimalCost);
    MultiReplicas res = new MultiReplicas();
    for (Replica replica : multiReplicas)
      res.add(replica);
    return res;
  }


  /**
   * Initialize subsets of workload. Traverse each query in workload, and put it randomly
   * into different query groups.
   */
  private void initDesign() throws NoSuchAlgorithmException {
    Random random = SecureRandom.getInstanceStrong();
    for (Query q : workload)
      for (int j = 0; j < loadBalanceFactor; j++)
        workloadSubsets[random.nextInt(workloadSubsets.length)]
                .add(new Query(q).setWeight(q.getWeight() / loadBalanceFactor));
  }

  /**
   * This method takes a collection of queries as input, considering data table  and
   * return a replica configuration. The implementation depend on other modules or
   * tools. Here we use stimulate anneal algorithm and set replica num to 1.
   *
   * @param queries, a collection of queries
   * @return a recommended replica
   */
  private Replica recommendReplica(List<Query> queries) {
    return (Replica) new SearchAll(data, queries.toArray(new Query[0])).optimal().getReplicas().keySet().toArray()[0];
//    return (Replica) new SimulateAnneal(data, queries.toArray(new Query[0]), 1)
//            .optimal().getReplicas().keySet().toArray()[0];
  }

  /**
   * Check if iteration should terminate. If current iteration reaches its threshold
   * or improvement of current strategy too subtle (current cost - old cost < epsilon),
   * terminate the iteration.
   *
   * @param curIteration, current iteration number
   * @param curCost,      current cost
   * @return false if terminate
   */
  private boolean isIterationTerminate(int curIteration, double curCost) {
    if (optimalCost == 0 || curIteration == 0) return false;
    if (Math.abs(curCost - optimalCost) < epsilone) return true;
    return curIteration >= maxIteration;
  }

  /**
   * Input a group of replicas, calculate cost of evaluating given query on each replica,
   * and output the m-cost-least order of replicas. The length of the output equals to
   * load balancing factor.
   *
   * @param replicas, the candidate replicas
   * @param query,    the query need to evaluate
   * @return order of m-cost-least replicas
   */
  private int[] getLeastCostConfOrder(Replica[] replicas, Query query) {
    Integer[] order = new Integer[replicaNum];
    for (int i = 0; i < order.length; i++) order[i] = i;
    BigDecimal[] costs = new BigDecimal[replicaNum];
    for (int i = 0; i < costs.length; i++)
      costs[i] = CostModel.cost(replicas[i], query);
    Arrays.sort(order, Comparator.comparing(o -> costs[o]));
    int[] res = new int[loadBalanceFactor];
    for (int i = 0; i < res.length; i++) res[i] = order[i];
    return res;
  }

  /**
   * The total cost of a design, according to formula in paper
   *
   * @param mulReplicas, a collection of replicas
   * @return total cost
   */
  private double totalCost(Replica[] mulReplicas) {
    BigDecimal ans = new BigDecimal("0");
    for (Query query : workload) {
      BigDecimal curCost = new BigDecimal("0");
      int[] order = getLeastCostConfOrder(mulReplicas, query);
      for (int i = 0; i < loadBalanceFactor; i++)
        curCost = curCost
                .add(CostModel.cost(mulReplicas[order[i]], query)
                        .multiply(BigDecimal.valueOf((double) 1 / loadBalanceFactor))
                        .divide(BigDecimal.valueOf(loadBalanceFactor)));
      ans = ans.add(curCost);
    }
    return ans.doubleValue();
  }

  public double getOptimalCost() {
    return optimalCost;
  }

  public List<Double> getHistory() {
    return history;
  }
}
