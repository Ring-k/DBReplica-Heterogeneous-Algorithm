package constant;

import java.math.BigDecimal;

public class Constant {

  // Histogram
  public static final double HISTOGRAM_STEP = 1;

  // Objective System
  public static final int REPLICA_NUMBER = 3;

  // CostModel
  public static final BigDecimal COST_SCALE = BigDecimal.valueOf(1);

  // Stimulate Anneal
  public static final int LOCAL_ITERATION_NUM = 30;
  public static final double TEMPERATURE_DECREASE_RATE = 0.5;
  public static final double TEMPERATURE_INIT_SEED = 0.8;
  public static final int OPTIMAL_COUNT_THRESHOLD = 60;

  // Divergent Design
  public static  final int LOAD_BALANCE_FACTOR = 1;
  public static  final int MAX_ITERATION = 1000;
  public static  final double EPSILON = 0.001;

  // Rita
  public static final double SKEW_FACTOR = 0.5;
  public static final boolean IS_NEW_METHOD = false;

  //GA
  public static final int GA_MAX_ITERATION = 1000;
  public static final int GA_MIN_ITERATION = 40;
  public static final int POPULATION = 100;
  public static final double CROSSOVER_RATE = 0.5;
  public static final double MUTATION_RATE = 0.0001;
  public static final int GENE_CHANGE_NUMBER = 1;


}
