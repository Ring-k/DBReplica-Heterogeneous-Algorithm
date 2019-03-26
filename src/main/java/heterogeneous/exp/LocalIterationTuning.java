package heterogeneous.exp;

import constant.Constant;
import cost.CostModel;
import datamodel.DataTable;
import heterogeneous.SimulateAnneal;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;

import java.io.*;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class LocalIterationTuning {

  static Query[] getQueries(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data\\queries_" + colNums));
    return (Query[]) ois.readObject();
  }

  static DataTable getDataTable(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data\\data_table_" + colNums));
    return (DataTable) ois.readObject();
  }

  static void writeSolutions(int minIt, int maxIt, DataTable dataTable, Query[] queries, String outPutPath)
          throws IOException, NoSuchAlgorithmException {
    File f = new File(outPutPath);
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    for (int i = minIt; i <= maxIt; i++) {
      Constant.LOCAL_ITERATION_NUM = i;
      SimulateAnneal sa = new SimulateAnneal(dataTable, queries, 3);
      fw.write(sa.optimal().getOrderString() + i + "\n");
    }
    fw.close();
  }

  static void evaluate(String inputPath, String outputPath,
                       DataTable dataTable, Query[] qs,
                       int minIteration, int maxIteration) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(inputPath));
    String line = null;
    List<BigDecimal>[] ans = new List[100];
    for (int i = minIteration; i <= maxIteration; i++)
      ans[i] = new ArrayList<>();

    int it;
    while ((line = br.readLine()) != null) { // a strategy, a local iteration number

      String[] strs = line.split("\\[");
      it = Integer.parseInt(line.split("}")[1]);

      MultiReplicas m = new MultiReplicas();
      for (int i = 1; i < strs.length; i++) { // a replica
        String[] orders = strs[i].split("]")[0].split(", ");
        int[] order = new int[orders.length];
        for (int j = 0; j < order.length; j++)
          order[j] = Integer.parseInt(orders[j]);
//        System.out.println(Arrays.toString(order));
        m.add(new Replica(dataTable, order));
      }
      try {
        ans[it].add(CostModel.cost(m, qs));
      }catch (NullPointerException e){
        break;
      }
    }

    File f = new File(outputPath);
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    for (int i = minIteration; i <= maxIteration; i++) {
      String outLine = "" + i;
      for (BigDecimal cost : ans[i])
        outLine += ", " + cost.toString();
      outLine += "\n";
      fw.write(outLine);
    }
    fw.close();
  }


  /**
   * experience on local iteration number,
   * ROW_NUM = 100000
   * REPLICA_NUMBER = 3
   * TEMPERATURE_DECREASE_RATE = 0.7;
   * TEMPERATURE_INIT_SEED = 0.8;
   * OPTIMAL_COUNT_THRESHOLD = 30;
   */
  public static void main(String args[]) throws IOException,
          ClassNotFoundException, NoSuchAlgorithmException {

    DataTable dataTable = getDataTable(5);
    Query[] queries = getQueries(5);

//    for (int i = 0; i < 10; i++)
//      writeLocalIterationSolution(1, 9, dataTable, queries,
//              "HeterogeneousExp\\exp_on_local_iterations_col5_it60.txt");

    evaluate("HeterogeneousExp\\exp_on_local_iterations_col5_it60.txt",
            "HeterogeneousExp\\exp_on_local_iterations_col5_it60_eva.csv",
            dataTable, queries, 1, 60);
  }
}
