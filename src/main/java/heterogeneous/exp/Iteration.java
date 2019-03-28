package heterogeneous.exp;

import constant.Constant;
import datamodel.DataTable;
import heterogeneous.SimulateAnneal;
import query.Query;

import java.io.*;
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

  static void writeLocalIterationSolution(int minIt, int maxIt, int minCnt, int maxCnt, DataTable dataTable, Query[] queries, String outPutPath)
          throws IOException, NoSuchAlgorithmException {
    File f = new File(outPutPath);
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    for (int i = minCnt; i <= maxCnt; i++) {
      for (int j = minIt; j <= maxIt; j++) {
        Constant.OPTIMAL_COUNT_THRESHOLD = i;
        Constant.LOCAL_ITERATION_NUM = j;
        SimulateAnneal sa = new SimulateAnneal(dataTable, queries, 3);
        System.out.println("start sa");
        sa.optimal();
        String line = "";
        line += i + "," + j + "," + sa.getOptimalCost() + "\n";
        fw.write(line);
        System.out.println(i + " " + j);
      }
      fw.flush();
    }
    fw.close();
  }

  public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    int start = Integer.parseInt(args[0]);
    int end = Integer.parseInt(args[1]);
//    System.out.println(start + " " + end);

    DataTable dataTable = getDataTable(Integer.parseInt(args[2]));
    Query[] queries = getQueries(Integer.parseInt(args[2]));
////    writeLocalIterationSolution(1, 60, 1, 60, dataTable, queries, "HeterogeneousExp\\exp_on_co_iterations_from_1_to_60_eva.csv");
  writeLocalIterationSolution(1, 60, start, end, dataTable, queries, "exp_on_co_iterations_on"+args[2]+"_from_"+start+"_to_"+end+"_eva.csv");
  }


}
