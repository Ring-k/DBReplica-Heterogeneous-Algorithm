package divergentdesign.exp;

import constant.Constant;
import cost.CostModel;
import datamodel.DataTable;
import divergentdesign.DivgDesign;
import heterogeneous.SimulateAnneal;
import query.Query;
import replica.MultiReplicas;

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
    DataTable dataTable = getDataTable(10);
    Query[] queries = getQueries(10);

    File f = new File("dive_it_exp.csv");
    if(!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    for(double i = 0.00001; i < 0.01; i+=(0.0001)){
      System.out.print(i + " ");
      for(int j = 1; j <= 1000; j++){
        Constant.EPSILON = i;
        Constant.MAX_ITERATION = j;
        MultiReplicas m = new DivgDesign(dataTable, queries).optimal();
        double cost = CostModel.cost(m, queries).doubleValue();
        String s = "" + i + "," + j+ "," + cost + "\n";
        System.out.print(s);
        fw.write(s);
      }
    }
    fw.close();
  }
}
