package genetic;

import constant.Constant;
import cost.CostModel;
import datamodel.DataTable;
import heterogeneous.ArrayTransform;
import javafx.util.Pair;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

/**
 * This class implement Genetic Algorithm to find the optimal multi-replicas strategy
 */
public class Genetic {

  DataTable dataTable;
  Query[] queries;

  // parameters of GA
  private int populationSize;
  private int maxIteration;
  private int minIteration;
  private boolean isNewMethod;
  private double crossoverRate;
  private double mutationRate;
  // how many replicas in a multi-replica should be change
  // in mutation, should be less or equal tha the replica number
  private int geneChangeNum;

  private int replicaNumber;
  private double equalRateThreshold = 0.5;

  private MultiReplicas multiReplicas = null;

  /**
   * A constructor using constant parameter values.
   *
   * @param dataTable info of data table
   * @param queries   info of queries
   */
  public Genetic(DataTable dataTable, Query[] queries) {
    this.dataTable = dataTable;
    this.queries = queries;
    this.isNewMethod = Constant.IS_NEW_METHOD;
    this.maxIteration = Constant.GA_MAX_ITERATION;
    this.populationSize = Constant.POPULATION;
    this.crossoverRate = Constant.CROSSOVER_RATE;
    this.mutationRate = Constant.MUTATION_RATE;
    this.replicaNumber = Constant.REPLICA_NUMBER;
    this.minIteration = Constant.GA_MIN_ITERATION;
    this.geneChangeNum = Constant.GENE_CHANGE_NUMBER;
    if (geneChangeNum <= 0 || geneChangeNum > replicaNumber)
      throw new IllegalArgumentException("Gene change number greater than replica number.");
  }

  /**
   * A constructor using customized parameters
   *
   * @param dataTable        info of data table
   * @param queries          info of queries
   * @param replicaNumber    number of replica
   * @param populationSize   size of the population
   * @param minIteration     minimum val of iteration
   * @param maxIteration     max val of iteration
   * @param crossoverRate    crossover rate
   * @param mutationRate     mutation rate
   * @param geneChangeNumber the number of replica in a multi-replica to change during mutation
   * @param isNewMethod      using cask effect cost or not
   */
  public Genetic(DataTable dataTable, Query[] queries, int replicaNumber,
                 int populationSize, int minIteration, int maxIteration,
                 double crossoverRate, double mutationRate, int geneChangeNumber,
                 boolean isNewMethod) {
    this.dataTable = dataTable;
    this.queries = queries;
    this.replicaNumber = replicaNumber;
    this.populationSize = populationSize;
    this.minIteration = minIteration;
    this.maxIteration = maxIteration;
    this.isNewMethod = isNewMethod;
    this.crossoverRate = crossoverRate;
    this.mutationRate = mutationRate;
    this.geneChangeNum = geneChangeNumber;
    if (geneChangeNum <= 0 || geneChangeNum > replicaNumber)
      throw new IllegalArgumentException("Gene change number greater than replica number.");
  }

  /**
   * The GA method
   *
   * @return the optimal multi-replica
   * @throws NoSuchAlgorithmException
   */
  public MultiReplicas optimal() throws NoSuchAlgorithmException {
    // initialize the populationSize, a group of multi-replicas / solutions
    MultiReplicas[] curPopulation = init();
    int curIteration = 0;
    while (true) {
      System.out.println(curIteration); // TODO
      // pick and copy
      curPopulation = copy(curPopulation);
      if (isTerminate(curIteration, curPopulation)) break;
      // crossover
      curPopulation = crossover(curPopulation);
      // mutation
      curPopulation = mutate(curPopulation);
      curIteration++;
    }
    return multiReplicas; // the multiReplica
  }

  /**
   * Process of copying population. First calculate fitness array, and get the picking probability.
   * Continuously generate a random double value , from 0 to 1, and pick the correspond individual
   * to copy, until the size of next generation equals the size of parent generation.
   *
   * @param population the population, a lot of multi-replicas
   * @return the picked new generation
   */
  private MultiReplicas[] copy(MultiReplicas[] population) throws NoSuchAlgorithmException {
    Random random = SecureRandom.getInstanceStrong();
    BigDecimal[] fitArr = fit(population);
    BigDecimal total = new BigDecimal(0);
    for (BigDecimal fitness : fitArr) total = total.add(fitness);
    double[] prob = new double[fitArr.length];
    for (int i = 0; i < fitArr.length; i++)
      prob[i] = fitArr[i].divide(total, 1000, BigDecimal.ROUND_HALF_UP).doubleValue();
    for (int i = 1; i < prob.length; i++) prob[i] += prob[i - 1];
    MultiReplicas[] nextGeneration = new MultiReplicas[population.length];
    for (int i = 0; i < nextGeneration.length; i++) {
      double v = random.nextDouble();
      for (int j = prob.length - 1; j >= 0; j--) {
        if (v >= prob[j]) {
          nextGeneration[i] = new MultiReplicas(population[j + 1]);
          break;
        }
      }
      if (nextGeneration[i] == null) nextGeneration[i] = new MultiReplicas(population[0]);
    }
    return nextGeneration;
  }

  /**
   * Crossover of two individuals (multi-replica). Randomly generate crossover point, from 1 (inclusive)
   * to replica number (exclusive), i.g., {1,2,3,4,5} and {9,8,7,6,5}, the crossover point will be in
   * range [1,4]. If the crossover point is 2, the children are {1,2,7,6,5} and {9,8,3,4,5}.
   *
   * @param m1 one of the parents (multi-replica)
   * @param m2 one of the parents (multi-replica)
   * @return a pair, two children
   */
  private Pair<MultiReplicas, MultiReplicas> crossover(MultiReplicas m1, MultiReplicas m2)
          throws NoSuchAlgorithmException {
    if (m1.getReplicaNum() != m2.getReplicaNum())
      throw new IllegalArgumentException("Replica numbers are inconsistent");
    // save first index(i) of element
    int crossoverPoint = SecureRandom.getInstanceStrong()
            .nextInt(m1.getReplicaNum() - 1) + 1;
    Replica[] mr1 = m1.getReplicasArray(true);
    Replica[] mr2 = m2.getReplicasArray(true);
    for (int i = crossoverPoint; i < m1.getReplicaNum(); i++) {
      Replica temp = new Replica(mr1[i]);
      mr1[i] = new Replica(mr2[i]);
      mr2[i] = new Replica(temp);
    }
    m1 = new MultiReplicas();
    m2 = new MultiReplicas();
    for (int i = 0; i < mr1.length; i++) {
      m1.add(mr1[i]);
      m2.add(mr2[i]);
    }
    return new Pair<>(m1, m2);
  }

  /**
   * Crossover of parent generation. Shuffle the individuals randomly, and pick the ones
   * to participate in crossover according to crossover rate and population size. Replace
   * the parents with their children.
   *
   * @param population original population
   * @return population after crossover
   */
  private MultiReplicas[] crossover(MultiReplicas[] population)
          throws NoSuchAlgorithmException {
    List<MultiReplicas> list = new ArrayList<>();
    Collections.addAll(list, population);
    Collections.shuffle(list);
    population = list.toArray(new MultiReplicas[0]);
    int maxNum = (int) (crossoverRate * population.length / 2);
    for (int i = 0; i < maxNum; i++) {
      Pair p = crossover(population[i], population[population.length - 1 - i]);
      population[i] = (MultiReplicas) p.getKey();
      population[population.length - 1 - i] = (MultiReplicas) p.getValue();
    }
    return population;
  }

  /**
   * Mutate in a population. Decide the number of individuals (multi-replica) participating in
   * mutation, according to mutate rate, population size and replica number (gene size in an
   * individual). Decide the index of individuals to mutate randomly.
   *
   * @param population the original generation
   * @return the generation after mutation
   */
  private MultiReplicas[] mutate(MultiReplicas[] population) throws NoSuchAlgorithmException {
    int number = (int) (population.length * replicaNumber * mutationRate);
    if (number == 0) return population;
    Integer[] mutateIdx = generateRandomArray(0, population.length, number);
    for (int i = 0; i < mutateIdx.length; i++)
      population[mutateIdx[i]] = mutate(population[mutateIdx[i]]);
    return population;
  }

  /**
   * Mutate an individual (a multi-replica). Get replica array of the individual and mutate a number
   * of replicas, using generate new replica method. The number of replica to mutate = geneChangeNum.
   * Reconstruct multi-replica with the new replicas.
   *
   * @param multiReplicas original individual (multi-replica)
   * @return new individual (multi-replica)
   */
  private MultiReplicas mutate(MultiReplicas multiReplicas) throws NoSuchAlgorithmException {
    Replica[] rs = multiReplicas.getReplicasArray(true);
    Integer[] mutateIdx = generateRandomArray(0, multiReplicas.getReplicaNum(), geneChangeNum);
    for (int i = 0; i < mutateIdx.length; i++)
      rs[mutateIdx[i]] = generateNewReplica(rs[mutateIdx[i]]);
    MultiReplicas newMultiReplica = new MultiReplicas();
    for (int i = 0; i < rs.length; i++)
      newMultiReplica.add(rs[i]);
    return newMultiReplica;
  }

  /**
   * Generate an array of random numbers. Pick {len} numbers of integer number from {min} (inclusive) to
   * {min + rangeLen}(exclusive) randomly.
   *
   * @param min,      minimum value
   * @param rangeLen, range length of value.
   * @param len,      length of returned array.
   * @return
   */
  private Integer[] generateRandomArray(int min, int rangeLen, int len) {
    if (rangeLen <= 0 || len <= 0 || len > rangeLen)
      throw new IllegalArgumentException();
    List<Integer> ls = new ArrayList<>();
    for (int i = min; i < min + rangeLen; i++)
      ls.add(i);
    Integer[] ans = new Integer[len];
    Collections.shuffle(ls);
    System.arraycopy(ls.toArray(new Integer[0]), 0, ans, 0, ans.length);
    return ans;
  }


  /**
   * Initialize a population (a group of multi-replica)
   *
   * @return the initialized population/multi-replicas
   */
  private MultiReplicas[] init() {
    MultiReplicas[] multiReplicas = new MultiReplicas[populationSize];
    for (int i = 0; i < multiReplicas.length; i++)
      multiReplicas[i] = initRandomMultiReplicas();
    return multiReplicas;
  }

  /**
   * Initialize an individual/multi-replica randomly
   *
   * @return the initialized individual/multi-replicas
   */
  private MultiReplicas initRandomMultiReplicas() {
    MultiReplicas newMultiReplica = new MultiReplicas();
    for (int i = 0; i < replicaNumber; i++)
      newMultiReplica.add(new Replica(dataTable, ArrayTransform.random(dataTable.getColNum())));
    return newMultiReplica;
  }

  /**
   * Calculate fitness array. for each multi-replica, fitness = 1/cost(multi-replica, queries)
   *
   * @param curPopulation a population/a group multi-replicas
   * @return the array of fitness, each element corresponding to the individual in input array
   */
  private BigDecimal[] fit(MultiReplicas[] curPopulation) {
    BigDecimal[] ans = new BigDecimal[curPopulation.length];
    BigDecimal max = null;

    if (isNewMethod)
      for (int i = 0; i < ans.length; i++)
        ans[i] = BigDecimal.valueOf(1)
                .divide(CostModel.cost(curPopulation[i], queries), 1000, BigDecimal.ROUND_HALF_UP);
    else
      for (int i = 0; i < ans.length; i++)
        ans[i] = BigDecimal.valueOf(1)
                .divide(CostModel.totalCost(curPopulation[i], queries), 1000, BigDecimal.ROUND_HALF_UP);


//    if (isNewMethod) {
//      for (int i = 0; i < ans.length; i++) {
//        ans[i] = BigDecimal.valueOf(1)
//                .divide(CostModel.cost(curPopulation[i], queries), 1000, BigDecimal.ROUND_HALF_UP);
//        if(max == null || max.compareTo(ans[i]) < 0)
//          max = new BigDecimal(ans[i].toString());
//      }
//    }
//    else {
//      for (int i = 0; i < ans.length; i++) {
//        ans[i] = BigDecimal.valueOf(1)
//                .divide(CostModel.totalCost(curPopulation[i], queries), 1000, BigDecimal.ROUND_HALF_UP);
//        if(max == null || max.compareTo(ans[i]) < 0)
//          max = new BigDecimal(ans[i].toString());
//      }
//    }
//    for(int i = 0; i < ans.length; i++) {
//      ans[i] = ans[i].divide(max, 1000, BigDecimal.ROUND_HALF_UP);
////      System.out.println(ans[i].toString());
//      double d = ans[i].doubleValue();
//      d = 1 / (1 + Math.pow(2,- 8 * (d-0.5)));
//      ans[i] = BigDecimal.valueOf(d);
//    }




    return ans;
  }

  /**
   * Check if GA should terminate. The termination is possible only after executing iteration more than
   * min-iteration times. Then analysis the current population. If most of individuals are the same one,
   * or the current iteration number reaches the max-iteration, terminate and update optimal multi-replica
   * with the dominate individual.
   *
   * @param currentIteration current iteration number
   * @param curPopulation    current population / a group of multi-replicas
   * @return true if to terminate, false not to terminate
   */
  private boolean isTerminate(int currentIteration, MultiReplicas[] curPopulation) {
    // if less than minIteration, return false
//    if (currentIteration < minIteration) return false; // TODO
    // analyze
    Map<MultiReplicas, Integer> counter = new HashMap<>();
    for (int i = 0; i < curPopulation.length; i++) {
      if (!counter.keySet().contains(curPopulation[i]))
        counter.put(curPopulation[i], 1);
      else
        counter.put(curPopulation[i], counter.get(curPopulation[i]) + 1);
    }
    int n = 0;
    MultiReplicas m = null;
    for (Map.Entry<MultiReplicas, Integer> en : counter.entrySet()) {
      if (n == 0 || m == null || n < en.getValue()) {
        n = en.getValue();
        m = en.getKey();
      }
    }
    System.out.println(CostModel.cost(m, queries) + " " + n);
    if (currentIteration < minIteration) return false;// TODO
    if (currentIteration == maxIteration) {
      multiReplicas = m;
      return true;
    }
    if (n < equalRateThreshold * curPopulation.length)
      return false;
    else {
      multiReplicas = m;
      return true;
    }
  }

  private Replica generateNewReplica(Replica replica)
          throws NoSuchAlgorithmException {
    Random rand = SecureRandom.getInstanceStrong();
    int columnNum = replica.getDataTable().getColNum();
    int pos0 = 0;
    int pos1 = 0;
    while (pos0 == pos1) {
      pos0 = rand.nextInt(columnNum);
      pos1 = rand.nextInt(columnNum);
    }
    int[] newOrder = null;
    do {
      int len = rand.nextInt(columnNum - pos0) + 1;
      int seed = rand.nextInt(100);
      if (isIn(seed, 0, 5))
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
        newOrder = ArrayTransform.swap(replica.getOrder(), pos0, pos1);
      if (newOrder == null) throw new NullPointerException();
    } while (isSame(newOrder, replica.getOrder()));
    return new Replica(replica.getOriginalDataTable(), newOrder);
  }

  private boolean isSame(int[] arr1, int[] arr2) {
    if (arr1 == null || arr2 == null)
      throw new NullPointerException();
    if (arr1.length != arr2.length) return false;
    for (int i = 0; i < arr1.length; i++)
      if (arr1[i] != arr2[i]) return false;
    return true;
  }

  private boolean isIn(int value, int lowerBound, int upperBound) {
    return value >= lowerBound && value < upperBound;
  }

  public DataTable getDataTable() {
    return dataTable;
  }

  public Query[] getQueries() {
    return queries;
  }
}
