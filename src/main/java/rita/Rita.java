package rita;


import constant.Constant;
import cost.CostModel;
import datamodel.DataTable;
import heterogeneous.ArrayTransform;
import javafx.util.Pair;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;
import searchall.SearchAll;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class Rita {

  private DataTable data;
  private Query[] queries;

  private int replicaNumber;
  private double temperature;
  private int iteration = 0;
  private int optimalCnt = 0;
  private double skewFactor = 0.0; // 0 = balance case

  // the solution
  private MultiReplicas multiReplicas = null;
  private BigDecimal optimalCost;
  private List<Double> costHistory = new ArrayList<>();

  public Rita(DataTable dataTable, Query[] queries) {
    this.data = dataTable;
    this.queries = queries;
    this.replicaNumber = Constant.REPLICA_NUMBER;
    this.skewFactor = Constant.SKEW_FACTOR;
  }

  public Rita(DataTable dataTable, Query[] queries, int replicaNumber) {
    this.data = dataTable;
    this.queries = queries;
    this.replicaNumber = replicaNumber;
    this.skewFactor = Constant.SKEW_FACTOR;
  }

  /**
   * Same as Simulate Anneal
   *
   * @return
   * @throws NoSuchAlgorithmException
   */
  public MultiReplicas optimal() throws NoSuchAlgorithmException {
    initTemperature();
    if (multiReplicas == null)
      multiReplicas = initSolutionByOptimalReplica();
    optimalCost = cost(multiReplicas, queries).getValue();
    costHistory.add(optimalCost.doubleValue());
    while (!isGlobalConverge()) {
      MultiReplicas curMultiReplica = new MultiReplicas(multiReplicas);
      BigDecimal curCost = optimalCost;
      while (!isLocalConverge()) {
        // generate new solution
        MultiReplicas newMultiReplica = generateNewMultiReplica(curMultiReplica);
        Pair costPair = cost(newMultiReplica, queries);
        boolean isBalance = (boolean) costPair.getKey();
        BigDecimal newCost = (BigDecimal) costPair.getValue();
        if (isBalance && isChosen(newCost, curCost)) {
          curMultiReplica = newMultiReplica;
          curCost = newCost;
        }
        costHistory.add(curCost.doubleValue());
        iteration++;
      }
      iteration = 0;
      if (curCost.compareTo(optimalCost) < 0) {
        multiReplicas = new MultiReplicas(curMultiReplica);
        optimalCost = curCost;
        optimalCnt = 0;
      } else {
        optimalCnt++;
      }
      decreaseTemperature();
    }
    return multiReplicas;
  }

  /**
   * Generate a new Replica, using different methods at certain probability.
   * 5% total shuffle, 15% range shuffle, 20% swap, 20% insert before,
   * 20% insert after, 15% range reverse, 5% total reverse.
   *
   * @param replica, the original replica
   * @return new replica
   */
  private static Replica generateNewReplica(Replica replica)
          throws NoSuchAlgorithmException {
    Random rand = SecureRandom.getInstanceStrong();
    int columnNum = replica.getDataTable().getColNum();
    int pos0 = 0;
    int pos1 = 0;
    while (pos0 == pos1) {
      pos0 = rand.nextInt(columnNum);
      pos1 = rand.nextInt(columnNum);
    }
    int len = rand.nextInt(columnNum - pos0) + 1;
    int seed = rand.nextInt(100);
    int[] newOrder = null;
    if (isIn(seed, 0, 5))
//      newOrder = ArrayTransform.shuffle(replica.getOrder());
      newOrder = ArrayTransform.swap(replica.getOrder(), pos0, pos1);
    else if (isIn(seed, 5, 20))
      newOrder = ArrayTransform.shuffle(replica.getOrder(), pos0, len);
    else if (isIn(seed, 20, 40))
      newOrder = ArrayTransform.swap(replica.getOrder(), pos0, pos1);
    else if (isIn(seed, 40, 60))
      newOrder = ArrayTransform.insertBefore(replica.getOrder(), pos0, pos1);
    else if (isIn(seed, 60, 80))
      newOrder = ArrayTransform.insertAfter(replica.getOrder(), pos0, pos1);
    else if (isIn(seed, 80, 95))
      newOrder = ArrayTransform.reverse(replica.getOrder(), pos0, len);
    else if (isIn(seed, 95, 100))
//      newOrder = ArrayTransform.reverse(replica.getOrder());
      newOrder = ArrayTransform.swap(replica.getOrder(), pos0, pos1);
    if (newOrder == null) throw new NullPointerException();
    return new Replica(replica.getOriginalDataTable(), newOrder);
  }

  /**
   * generate a set of replicas. Given a multi-replica, generate new
   * replicas according to replicas in the original multi-replica.
   *
   * @param multiReplica, original multi-replica
   * @return a generated new multi-replica
   */
  private MultiReplicas generateNewMultiReplica(MultiReplicas multiReplica)
          throws NoSuchAlgorithmException {
    MultiReplicas ans;
    do {
      ans = new MultiReplicas();
      for (Map.Entry<Replica, Integer> e : multiReplica.getReplicas().entrySet())
        for (int i = 0; i < e.getValue(); i++)
          ans.add(generateNewReplica(e.getKey()));
    } while (multiReplica.equals(ans));
    return ans;
  }

  /**
   * Check if choose new strategy. If the new cost is less than old one, return true.
   * If the new cost is greater than old one, choose it at some probability, which is
   * related to current temperature. Probability decreases when temperature getting lower.
   * The threshold is exp(-delta/temperature).
   *
   * @param newCost
   * @param oldCost
   * @return
   */
  private boolean isChosen(BigDecimal newCost, BigDecimal oldCost) {
    if (newCost.compareTo(oldCost) < 0) return true;
    double threshold = Math.exp(oldCost.subtract(newCost).doubleValue() / temperature);
    return Math.random() <= threshold;
  }

  /**
   * Method to decrease temperature. When it is called, temperature * 0.7.
   */
  private void decreaseTemperature() {
    temperature *= Constant.TEMPERATURE_DECREASE_RATE;
  }

  /**
   * Check if global loop should terminate. Here, we use a counter to record the current appearance
   * is an optimal cost. If it stays the same after certain times, the algorithm converges.
   *
   * @return true if converges
   */
  private boolean isGlobalConverge() {
    return optimalCnt == Constant.OPTIMAL_COUNT_THRESHOLD;
  }

  /**
   * Check if local loop converges. If temperature == 0 or iteration hits threshold, the local loop
   * should end.
   *
   * @return true if converges
   */
  private boolean isLocalConverge() {
//    return temperature == 0
//            || iteration == Constant.LOCAL_ITERATION_NUM;
    return iteration == Constant.LOCAL_ITERATION_NUM;
  }

  /**
   * Initialize temperature. Randomly pick 20 multi-replica solutions, and get the greatest and least
   * cost of them. Note the absolute difference as delta. The initial temperature = -delta/ln(seed),
   * where seed is in (0, 1], and close to 1.
   */
  private void initTemperature() {
    MultiReplicas m;
    BigDecimal max = null;
    BigDecimal min = null;
    for (int i = 0; i < 20; i++) {
      m = initSolutioRandom();
      BigDecimal curCost = CostModel.cost(m, queries);
      if (max == null || max.compareTo(curCost) < 0) max = curCost;
      if (min == null || min.compareTo(curCost) > 0) min = curCost;
    }
    if (min == null) throw new NullPointerException();
    temperature = min.subtract(max)
            .divide(BigDecimal.valueOf(Math.log(Constant.TEMPERATURE_INIT_SEED)),
                    10, BigDecimal.ROUND_HALF_UP)
            .doubleValue();
  }

  public Rita initSolution(Replica r) {
    multiReplicas = new MultiReplicas();
    for (int i = 0; i < replicaNumber; i++)
      multiReplicas.add(new Replica(r));
    return this;
  }

  /**
   * Randomly generate a multi-replica solution.
   *
   * @return a multi-replica
   */
  private MultiReplicas initSolutionByOptimalReplica() {
    MultiReplicas newMultiReplica = new MultiReplicas();
    Replica r = new SearchAll(data, queries).optimalReplica();
    for (int i = 0; i < replicaNumber; i++)
      newMultiReplica.add(new Replica(r));
    return newMultiReplica;
  }

  private MultiReplicas initSolutioRandom() {
    MultiReplicas newMultiReplica = new MultiReplicas();
    for (int i = 0; i < replicaNumber; i++)
      newMultiReplica.add(new Replica(data, ArrayTransform.random(data.getColNum())));
    return newMultiReplica;
  }

  /**
   * Check if a value is in a range or not.
   *
   * @param value,      the value
   * @param lowerBound, lower bound of the range
   * @param upperBound, upper bound of the range
   * @return true if in, false if not
   */
  private static boolean isIn(int value, int lowerBound, int upperBound) {
    return value >= lowerBound && value < upperBound;
  }

  /**
   * Get the optimal cost after running algorithm
   *
   * @return
   */
  public double getOptimalCost() {
    return optimalCost.doubleValue();
  }

  /**
   * Get the record history of optimal cost.
   *
   * @return the record history
   */
  public List<Double> getHistory() {
    return costHistory;
  }

  public Pair<Boolean, BigDecimal> cost(MultiReplicas multiReplicas, Query[] queries) {
    Map<Replica, BigDecimal> ans = new HashMap<>();
    for (Replica r : multiReplicas.getReplicas().keySet())
      ans.put(r, new BigDecimal("0"));
    for (Query q : queries) {
      Pair<Replica, BigDecimal> costPair = CostModel.cost(multiReplicas, q);
      BigDecimal cost = ans.get(costPair.getKey()).add(costPair.getValue());
      ans.put(costPair.getKey(), cost);
    }

    for (Replica r : ans.keySet()) {
      int replicaNumber = multiReplicas.getReplicas().get(r);
      if (replicaNumber != 1) {
        ans.put(r, ans.get(r).divide(BigDecimal.valueOf(replicaNumber),
                100, BigDecimal.ROUND_HALF_UP));
      }
    }

    BigDecimal res = null;
    for (BigDecimal n : ans.values()) {
      if (res == null || res.compareTo(n) < 0)
        res = n;
    }

    BigDecimal min = null;
    BigDecimal max = null;
    for (BigDecimal n : ans.values()) {
      if (min == null || min.compareTo(n) > 0) min = n;
      if (max == null || max.compareTo(n) < 0) max = n;
    }

//    System.out.println(min.toString());
    boolean isBalance = min.multiply(BigDecimal.valueOf(1 + skewFactor)).compareTo(max) >= 0;
    return new Pair<>(isBalance, res);
  }
}