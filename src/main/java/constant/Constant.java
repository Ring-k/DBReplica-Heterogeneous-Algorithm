package constant;

import java.math.BigDecimal;

public class Constant {

  // Histogram
  public static final double histogramStep = 1;

  // DataTable
  public static BigDecimal ROW_NUM = new BigDecimal("1");

  // Objective System
  public static int REPLICA_NUMBER = 3;

  // CostModel
  public static final BigDecimal COST_SCALE = BigDecimal.valueOf(1);

  // Stimulate Anneal
  public static int LOCAL_ITERATION_NUM = 30;
  public static double TEMPERATURE_DECREASE_RATE = 0.5;
  public static double TEMPERATURE_INIT_SEED = 0.8;
  public static int OPTIMAL_COUNT_THRESHOLD = 60;

  // Divergent Design
  public static  int LOAD_BALANCE_FACTOR = 1;
  public static  int MAX_ITERATION = 1000;
  public static  double EPSILON = 0.001;

  // Rita
  public static double SKEW_FACTOR = 0.5;
  public static boolean IS_NEW_METHOD = false;

  //GA
  public static int GA_MAX_ITERATION = 1000;
  public static int GA_MIN_ITERATION = 40;
  public static int POPULATION = 100;
  public static double CROSSOVER_RATE = 0.5;
  public static double MUTATION_RATE = 0.0001;
  public static int GENE_CHANGE_NUMBER = 1;


}
