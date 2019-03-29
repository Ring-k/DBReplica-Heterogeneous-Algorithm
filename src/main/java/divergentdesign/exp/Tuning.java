package divergentdesign.exp;

import constant.Constant;
import cost.CostModel;
import datamodel.DataTable;
import divergentdesign.DivgDesign;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;
import searchall.SearchAll;

import java.io.*;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

public class Tuning {
  static Query[] getQueries(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("queries_" + colNums));
    return (Query[]) ois.readObject();
  }

  static DataTable getDataTable(int colNums) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data_table_" + colNums));
    return (DataTable) ois.readObject();
  }

  public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

    int minIter = Integer.parseInt(args[0]);
    int maxIter = Integer.parseInt(args[1]);
    int itStep = Integer.parseInt(args[2]);

    double minEpsilon = Double.parseDouble(args[3]);
    double maxEpsilon = Double.parseDouble(args[4]);
    double epsilonStep = Double.parseDouble(args[5]);

    int col = Integer.parseInt(args[6]);

    DataTable dataTable = getDataTable(col);
    Query[] queries = getQueries(col);

    String line = "";
    for(String s : args) line += ("_" + s);

    File f = new File("dvg_it_exp"+line +".csv");
    if(!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    MultiReplicas st = new MultiReplicas();
    SearchAll sa = new SearchAll(dataTable,queries);
    Replica r = sa.optimalReplica();
    for(int i = 0; i < Constant.REPLICA_NUMBER; i++)
      st.add(new Replica(r));

    BigDecimal baseCost = CostModel.cost(st, queries);

    for(double i = minEpsilon; i <= maxEpsilon; i+=epsilonStep){
      System.out.print(i + " ");
      for(int j = minIter; j <= maxIter; j+=itStep){
        Constant.EPSILON = i;
        Constant.MAX_ITERATION = j;
        MultiReplicas m = new DivgDesign(dataTable, queries).optimal();
        double cost = CostModel.cost(m, queries).doubleValue();
        String s = "" + i + "," + j+ "," + cost/baseCost.doubleValue() + "\n";
        System.out.print(s);
        fw.write(s);
        fw.flush();
      }
    }
    fw.close();
  }
}
