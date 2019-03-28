package heterogeneous.exp;

import constant.Constant;
import cost.CostModel;
import datamodel.DataTable;
import heterogeneous.SimulateAnneal;
import query.Query;
import replica.MultiReplicas;

import java.io.*;
import java.security.NoSuchAlgorithmException;

public class TemperatureCecreaseRateTuning {
  static Query[] getQueries(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("queries_" + colNums));
    return (Query[]) ois.readObject();
  }

  static DataTable getDataTable(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data_table_" + colNums));
    return (DataTable) ois.readObject();
  }

  static void writeSolutions(double minRate, double maxRate, DataTable dataTable, Query[] queries, String outPutPath, int turn)
          throws IOException, NoSuchAlgorithmException {
    File f = new File(outPutPath);
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    for (double i = minRate; i <= maxRate; i += 0.01) {
      Constant.TEMPERATURE_DECREASE_RATE = i;
      SimulateAnneal sa = new SimulateAnneal(dataTable, queries, 3);
      long start = System.currentTimeMillis();
      MultiReplicas m = sa.optimal();
      long time = System.currentTimeMillis() - start;
      fw.write(i +"," + CostModel.cost(m, queries).doubleValue() +","  + time + "\n");
      System.out.println(turn + " " +i);
    }
    fw.close();
  }

  public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    DataTable dataTable = getDataTable(5);
    Query[] queries = getQueries(5);

    double minRate = Double.parseDouble(args[0]);

    double maxRate = Double.parseDouble(args[1]);

    Constant.OPTIMAL_COUNT_THRESHOLD = 60;
    Constant.LOCAL_ITERATION_NUM = 30;
    for (int i = 0; i < 10; i++)
      writeSolutions(minRate, maxRate, dataTable, queries,
              "exp_on_temperature_decrease_rate"+minRate+"_"+maxRate+".csv", i);

//    evaluate("HeterogeneousExp\\exp_on_temperature_decrease_rate.txt",
//            "HeterogeneousExp\\exp_on_temperature_decrease_rate_eva.csv",
//            dataTable, queries, 0.5, 0.9);
  }

}
