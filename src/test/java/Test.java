import constant.Constant;
import cost.CostModel;
import cost.QueryAnalysis;
import datamodel.DataTable;
import datamodel.Histogram;
import heterogeneous.SimulateAnneal;
import query.MiniQuery;
import query.PointQuery;
import query.Query;
import query.RangeQuery;
import replica.MultiReplicas;
import replica.Replica;
import searchall.SearchAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cost.CostModel.analysisEachReplica;

public class Test {

  @org.junit.Test
  public void testInit() throws IOException, ClassNotFoundException {
    DataTable dataTable = getDataTable();
    Query[] queries = getQueries();
    int[] order = QueryAnalysis.getRangeQueryNumber(queries);
    System.out.println(Arrays.toString(order));
    System.out.println(Arrays.toString(QueryAnalysis.getRangeQueryNumberOrder(order)));
    MultiReplicas m = new SimulateAnneal(dataTable, queries).initSolution().getMultiReplicas();
    System.out.println(m.getOrderString());
  }

  @org.junit.Test
  public void test() throws NoSuchAlgorithmException {
    List<Double> l = new ArrayList<>();
    for (int i = 0; i < 10; i++) l.add((double) i);
    Histogram h1 = new Histogram(l, 10);
    Histogram h2 = new Histogram(l, 10);
    Histogram h3 = new Histogram(l, 10);
    Histogram h4 = new Histogram(l, 10);
    Histogram h5 = new Histogram(l, 10);
    Histogram h6 = new Histogram(l, 10);
    Histogram[] hs = {h1, h2, h3, h4};
    DataTable dataTable = new DataTable(hs);
    Constant.ROW_NUM = BigDecimal.valueOf(10000);

    MiniQuery mq1 = new PointQuery(2);
    MiniQuery mq2 = new PointQuery(3);
    MiniQuery mq3 = new RangeQuery(3, 7);

    MiniQuery[] ms1 = {mq1, mq2, mq3, mq1};
    MiniQuery[] ms2 = {mq2, mq3, mq1, mq1};
    MiniQuery[] ms3 = {mq3, mq2, mq1, mq1};

    Query q1 = new Query(ms1, 1);
    Query q2 = new Query(ms2, 1);
    Query q3 = new Query(ms3, 1);
    Query q4 = new Query(ms3, 1);

    Query[] queries = {q1, q2, q3, q4};
    SimulateAnneal sa = new SimulateAnneal(dataTable, queries);
    MultiReplicas m = sa.optimal();
    System.out.println(m.getOrderString());
  }

  Query[] getQueries() throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data\\queries"));
//    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("queries"));
    return (Query[]) ois.readObject();
  }

  @org.junit.Test
  public void testOrder() throws IOException, ClassNotFoundException {
    Query[] queries = getQueries();
    System.out.println(Arrays.toString(QueryAnalysis.getRangeQueryNumber(queries)));
  }

  static DataTable getDataTable() throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data\\data_table"));
//    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data_table"));
    return (DataTable) ois.readObject();
  }

  @org.junit.Test
  public void testX() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {


  }

  public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
    DataTable dataTable1 = getDataTable();
    Histogram[] his = dataTable1.getColHistograms();
    Histogram[] h = new Histogram[5];
    for (int i = 0; i < h.length; i++)
      h[i] = his[i];

    DataTable dataTable = new DataTable(h);
    System.out.println(dataTable.toString());
    System.out.println(dataTable.getRowNum());

    MiniQuery mp1 = new PointQuery(1000);
    MiniQuery mr1 = new RangeQuery(1, 6000001);
    MiniQuery mp2 = new PointQuery(1000);
    MiniQuery mr2 = new RangeQuery(1, 10001);
    MiniQuery mp3 = new PointQuery(1000);
    MiniQuery mr3 = new RangeQuery(1, 10001);
    MiniQuery mp4 = new PointQuery(2);
    MiniQuery mr4 = new RangeQuery(1, 8);
    MiniQuery mp5 = new PointQuery(8.942816E11);
    MiniQuery mr5 = new RangeQuery(6.942816E11, 9.12441600001E11);
    MiniQuery mp6 = new PointQuery(8.942816E11);
    MiniQuery mr6 = new RangeQuery(6.967872E11, 9.09763200001E11);
    MiniQuery mp7 = new PointQuery(8.942816E11);
    MiniQuery mr7 = new RangeQuery(6.944544E11, 9.15033600001E11);

//    MiniQuery[] ms1 = {mr1,mp2,mp3,mp4,mp5,mp6,mp7};
//    MiniQuery[] ms2 = {mp1,mr2,mp3,mp4,mp5,mp6,mp7};
//    MiniQuery[] ms3 = {mp1,mp2,mr3,mp4,mp5,mp6,mp7};
//    MiniQuery[] ms4 = {mp1,mp2,mp3,mr4,mp5,mp6,mp7};
//    MiniQuery[] ms5 = {mp1,mp2,mp3,mp4,mr5,mp6,mp7};
//    MiniQuery[] ms6 = {mp1,mp2,mp3,mp4,mp5,mr6,mp7};
//    MiniQuery[] ms7 = {mp1,mp2,mp3,mp4,mp5,mp6,mr7};

    MiniQuery[] ms1 = {mr1, mp2, mp3, mp4, mp5};
    MiniQuery[] ms2 = {mp1, mr2, mp3, mp4, mp5};
    MiniQuery[] ms3 = {mp1, mp2, mr3, mp4, mp5};
    MiniQuery[] ms4 = {mp1, mp2, mp3, mr4, mp5};
    MiniQuery[] ms5 = {mp1, mp2, mp3, mp4, mr5};

    Query q1 = new Query(ms1, 1);
    Query q2 = new Query(ms2, 1);
    Query q3 = new Query(ms3, 1);
    Query q4 = new Query(ms4, 1);
    Query q5 = new Query(ms5,1);
//    Query q6 = new Query(ms6, 1);
//    Query q7 = new Query(ms7, 1);

    Constant.ROW_NUM = BigDecimal.valueOf(10E20);

    Query[] queries = {q1, q2, q3, q4, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5, q5};

    Replica r =  new SearchAll(dataTable, queries).optimalReplica();
    System.out.println(CostModel.cost(r, q5));

    /*
    SimulateAnneal sa = new SimulateAnneal(dataTable, queries).initSolution();
    sa.optimal();
    MultiReplicas mtp = sa.getMultiReplicas();
    System.out.println(mtp.getOrderString());
    System.out.println(sa.getOptimalCost());
    analysisEachReplica(mtp, queries);
*/

//    List<Query> q = new ArrayList<>();
//    for(int i = 0; i < 1000; i++) q.add(q1);
//    for(int i = 0; i < 1000; i++) q.add(q2);
//    for(int i = 0; i < 1000; i++) q.add(q3);
//    Replica r = new SearchAll(dataTable, queries).optimalReplica();
//
//    System.out.println(CostModel.cost(r, queries));


//

    int[] o1 = {4, 0, 2, 1, 3};
    int[] o2 = {4, 2, 0, 1, 3};
    int[] o3 = {0, 2, 1, 4, 3};
    Replica r1 = new Replica(dataTable, o1);
    Replica r2 = new Replica(dataTable, o2);
    Replica r3 = new Replica(dataTable, o3);
    MultiReplicas m = new MultiReplicas().add(r1).add(r2).add(r3);
    System.out.println(CostModel.cost(m, queries));

    int[] o4 = {2, 1, 4, 0, 3};
    int[] o5 = {1, 0, 2, 4, 3};
    int[] o6 = {0, 4, 1, 2, 3};
    Replica r4 = new Replica(dataTable, o4);
    Replica r5 = new Replica(dataTable, o5);
    Replica r6 = new Replica(dataTable, o6);
    MultiReplicas m1 = new MultiReplicas().add(r4).add(r5).add(r6);
    System.out.println(CostModel.cost(m1, queries));


  }
}
