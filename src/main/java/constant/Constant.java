package constant;

import java.math.BigDecimal;

public class Constant {

  // histogram constant
  public static final double histogramStep = 1;

  // data table constant
  public static final BigDecimal ROW_NUM = new BigDecimal("1000000");

  public static final int REPLICA_NUMBER = 3;

  // Stimulate Anneal
  public static final int LOCAL_ITERATION_NUM = 20;
  public static final double TEMPERATURE_DECREASE_RATE = 0.7;
  public static final double TEMPERATURE_INIT_SEED = 0.8;
  public static final String HISTORY_STORE_PATH = "history";

  // cost
  public static final BigDecimal COST_SCALE = BigDecimal.valueOf(1);


}