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

public class OptimalCountTuning {
  static Query[] getQueries(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data\\queries_" + colNums));
    return (Query[]) ois.readObject();
  }

  static DataTable getDataTable(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data\\data_table_" + colNums));
    return (DataTable) ois.readObject();
  }

  static void writeSolutions(int minCount, int maxCount, DataTable dataTable, Query[] queries, String outPutPath)
          throws IOException, NoSuchAlgorithmException {
    File f = new File(outPutPath);
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    for (int i = minCount; i <= maxCount; i++) {
      Constant.OPTIMAL_COUNT_THRESHOLD = i;
      SimulateAnneal sa = new SimulateAnneal(dataTable, queries, 3);
      fw.write(sa.optimal().getOrderString() + i + "\n");
    }
    fw.close();
  }

  static void evaluate(String inputPath, String outputPath,
                       DataTable dataTable, Query[] qs,
                       int minCount, int maxCount) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(inputPath));
    String line = null;
    List<BigDecimal>[] ans = new List[100];
    for (int i = minCount; i <= maxCount; i++)
      ans[i] = new ArrayList<>();

    int counter;
    while ((line = br.readLine()) != null) { // a strategy, a local iteration number

      String[] strs = line.split("\\[");
      counter = Integer.parseInt(line.split("}")[1]);

      MultiReplicas m = new MultiReplicas();
      for (int i = 1; i < strs.length; i++) { // a replica
        String[] orders = strs[i].split("]")[0].split(", ");
        int[] order = new int[orders.length];
        for (int j = 0; j < order.length; j++)
          order[j] = Integer.parseInt(orders[j]);
        m.add(new Replica(dataTable, order));
      }

      System.out.println(counter);
      ans[counter].add(CostModel.cost(m, qs));
    }

    File f = new File(outputPath);
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    for (int i = minCount; i <= maxCount; i++) {
      String outLine = "" + i;
      for (BigDecimal cost : ans[i])
        outLine += ", " + cost.toString();
      outLine += "\n";
      fw.write(outLine);
    }
    fw.close();
  }

  public static void main(String args[])
          throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    DataTable dataTable = getDataTable(5);
    Query[] queries = getQueries(5);

//    for (int i = 0; i < 10; i++)
//      writeLocalIterationSolution(1, 30, dataTable, queries,
//              "HeterogeneousExp\\exp_on_optimal_counter.txt");

    evaluate("HeterogeneousExp\\exp_on_optimal_counter.txt",
            "HeterogeneousExp\\exp_on_optimal_counter_eva.csv",
            dataTable, queries, 1, 30);
  }
}
