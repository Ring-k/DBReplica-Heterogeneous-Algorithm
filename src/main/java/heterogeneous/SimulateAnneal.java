package heterogeneous;

import constant.Constant;
import cost.CostModel;
import cost.QueryAnalysis;
import datamodel.DataTable;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;
import searchall.SearchAll;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;


public class SimulateAnneal {

  private DataTable data;
  private Query[] queries;

  private int replicaNumber;
  private double temperature;
  private int iteration = 0;
  private int optimalCnt = 0;
  private boolean isNewMethod = true;

  // the solution
  private MultiReplicas multiReplicas = null;
  private BigDecimal optimalCost;
  private List<Double> costHistory = new ArrayList<>();

  /**
   * Constructor
   *
   * @param dataTable, info of the data table
   * @param queries,   workload
   */
  public SimulateAnneal(DataTable dataTable, Query[] queries) {
    this.data = dataTable;
    this.queries = queries;
    this.replicaNumber = Constant.REPLICA_NUMBER;
  }

  /**
   * Constructor
   *
   * @param dataTable,     info of the data table
   * @param queries,       workload
   * @param replicaNumber, number of replica
   */
  public SimulateAnneal(DataTable dataTable, Query[] queries, int replicaNumber, boolean isNewMethod) {
    this.data = dataTable;
    this.queries = queries;
    this.replicaNumber = replicaNumber;
    this.isNewMethod = isNewMethod;
  }


  /*
   * Run the algorithm
   * 1. initialize temperature
   * 2. initialize a multi-replica solution,calculate the cost and record it
   * 3. loop [check if global converge]
   * 4.  | loop [check if local converge]
   * 5.  |  | generate a new multi-replica solution
   * 6.  |  | calculate the cost
   * 7.  |  | if [choose this new multi-replica solution]
   * 8.  |  |  | record the solution and cost
   * 8.  |  | end if
   * 9.  |  end loop
   * 10. |  if [choose local optimal as global optimal]
   * 11. |   |  update global optimal
   * 12. |  else
   * 13. |   |  increase same optimal counter
   * 14. |  end if
   * 15. |  decrease temperature
   * 16. end loop
   * 17. return optimal solution
   */
  public MultiReplicas optimal() throws NoSuchAlgorithmException {
    initTemperature();
    if (multiReplicas == null)
      multiReplicas = initSolutionByOptimalReplica();
    if (isNewMethod)
      optimalCost = CostModel.cost(multiReplicas, queries);
    else
      optimalCost = CostModel.totalCost(multiReplicas, queries);
    costHistory.add(optimalCost.doubleValue());
    CostModel.analysisEachReplica(multiReplicas, queries);// TODO print something here
    while (!isGlobalConverge()) {
      MultiReplicas curMultiReplica = new MultiReplicas(multiReplicas);
      BigDecimal curCost = optimalCost;
      while (!isLocalConverge()) {
        // generate new solution
        MultiReplicas newMultiReplica = generateNewMultiReplica(curMultiReplica);
        BigDecimal newCost;
        if (isNewMethod)
          newCost = CostModel.cost(newMultiReplica, queries);
        else
          newCost = CostModel.totalCost(newMultiReplica, queries);
        if (isChosen(newCost, curCost)) {
          curMultiReplica = newMultiReplica;
          curCost = newCost;
        }
        CostModel.analysisEachReplica(curMultiReplica, queries);//TODO print something here
        costHistory.add(curCost.doubleValue());
        iteration++;
      }
      iteration = 0;
      if (curCost.compareTo(optimalCost) < 0) {
        multiReplicas = new MultiReplicas(curMultiReplica);
//        CostModel.analysisEachReplica(curMultiReplica, queries);//TODO print something here
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
      m = initSolutionRandom();
      BigDecimal curCost;
      if (isNewMethod)
        curCost = CostModel.cost(m, queries);
      else
        curCost = CostModel.totalCost(m, queries);
      if (max == null || max.compareTo(curCost) < 0) max = curCost;
      if (min == null || min.compareTo(curCost) > 0) min = curCost;
    }
//    System.out.println("xxxxxxxxxxxxxxx");
    if (min == null) throw new NullPointerException();
    temperature = min.subtract(max)
            .divide(BigDecimal.valueOf(Math.log(Constant.TEMPERATURE_INIT_SEED)),
                    10, BigDecimal.ROUND_HALF_UP)
            .doubleValue();
  }

  /**
   * Use a replica to initialize SA solution. The initial multi-replica contains multiple
   * replication of the given replica
   *
   * @param r a replica
   */
  public SimulateAnneal initSolution(Replica r) {
    multiReplicas = new MultiReplicas();
    for (int i = 0; i < replicaNumber; i++)
      multiReplicas.add(new Replica(r));
    return this;
  }

  public SimulateAnneal initSolution() {
    int[] order = QueryAnalysis.getRangeQueryNumberOrder(queries);
    Set<Integer>[] replicaOrders = new HashSet[replicaNumber];
    for (int i = 0; i < replicaOrders.length; i++) {
      replicaOrders[i] = new HashSet<>();
      for (int j = 0; j < data.getColNum(); j++) replicaOrders[i].add(j);
    }
    int[][] replicaOrdersArr = new int[replicaNumber][];
    for (int i = 0; i < replicaOrdersArr.length; i++) {
      replicaOrdersArr[i] = new int[data.getColNum()];
      for (int j = 0; j < replicaOrdersArr[i].length; j++)
        replicaOrdersArr[i][j] = -1;
    }
    int orderIndex = 0;
    for (int i = data.getColNum() - 1; i >= 0; i--) {
      for (int j = 0; j < replicaNumber; j++) {
        if (orderIndex >= data.getColNum()) break;
        replicaOrdersArr[j][i] = order[orderIndex];
        replicaOrders[j].remove(order[orderIndex]);
        orderIndex++;
      }
      if (orderIndex >= data.getColNum()) break;
    }
    for (int i = 0; i < replicaNumber; i++) {
      Integer[] its = replicaOrders[i].toArray(new Integer[0]);
      List<Integer> lint = new ArrayList<>();
      Collections.addAll(lint, its);
      Collections.shuffle(lint);
      for (int j = 0; j < lint.size(); j++) replicaOrdersArr[i][j] = lint.get(j);
    }
    multiReplicas = new MultiReplicas();
    for (int i = 0; i < replicaOrdersArr.length; i++) {
      Replica r = new Replica(data, replicaOrdersArr[i]);
      multiReplicas.add(r);
    }
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

  private MultiReplicas initSolutionRandom() {
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

  @Deprecated
  public MultiReplicas getMultiReplicas() {
    return multiReplicas;
  }

}
