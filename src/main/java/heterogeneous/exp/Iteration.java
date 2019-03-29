package heterogeneous.exp;

import constant.Constant;
import cost.CostModel;
import datamodel.DataTable;
import heterogeneous.SimulateAnneal;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;
import searchall.SearchAll;

import java.io.*;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

public class Iteration {
  static Query[] getQueries(int colNums) throws IOException, ClassNotFoundException {
//    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data\\queries_" + colNums));
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("queries_" + colNums));
    return (Query[]) ois.readObject();
  }

  static DataTable getDataTable(int colNums) throws IOException, ClassNotFoundException {
//    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data\\data_table_" + colNums));
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data_table_" + colNums));
    return (DataTable) ois.readObject();
  }

  static void writeLocalIterationSolution(int minIt, int maxIt, int itStep, int minCnt, int maxCnt,int cntStep, DataTable dataTable, Query[] queries, String outPutPath)
          throws IOException, NoSuchAlgorithmException {
    File f = new File(outPutPath);
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    Replica initReplica = new SearchAll(dataTable, queries).optimalReplica();
    MultiReplicas optSingles = new MultiReplicas();
    for (int i = 0; i < Constant.REPLICA_NUMBER; i++)
      optSingles.add(new Replica(initReplica));
    BigDecimal cost = CostModel.cost(optSingles, queries);
//    System.out.println(cost);

    for (int i = minCnt; i <= maxCnt; i+=cntStep) {
      for (int j = minIt; j <= maxIt; j+=itStep) {
        Constant.OPTIMAL_COUNT_THRESHOLD = i;
        Constant.LOCAL_ITERATION_NUM = j;
        SimulateAnneal sa = new SimulateAnneal(dataTable, queries, 3).initSolution(initReplica);
        sa.optimal();
        String line = "";
        line += i + "," + j + "," + sa.getOptimalCost() / cost.doubleValue() + "\n";
        fw.write(line);
        System.out.println(line);
      }
      fw.flush();
    }
    fw.close();
  }

  public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    args = new String[]{"30", "40","5", "30", "40","5","7" };
    int minCnt = Integer.parseInt(args[0]);
    int maxCnt = Integer.parseInt(args[1]);
    int cntStep = Integer.parseInt(args[2]);
    int minLocalItr = Integer.parseInt(args[3]);
    int maxLocalItr = Integer.parseInt(args[4]);
    int localItStep = Integer.parseInt(args[5]);

    String line = "";
    for (String s : args) line += ("_" + s);

//    System.out.println(start + " " + end);

    DataTable dataTable = getDataTable(Integer.parseInt(args[6]));
    Query[] queries = getQueries(Integer.parseInt(args[6]));
////    writeLocalIterationSolution(1, 60, 1, 60, dataTable, queries, "HeterogeneousExp\\exp_on_co_iterations_from_1_to_60_eva.csv");
    writeLocalIterationSolution(minLocalItr, maxLocalItr,localItStep, minCnt, maxCnt,cntStep, dataTable, queries,
            "SA_itr" + line + "_eva.csv");
  }


}
