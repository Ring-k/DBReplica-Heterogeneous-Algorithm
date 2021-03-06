package searchall;

import constant.Constant;
import cost.CostModel;
import datamodel.DataTable;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SearchAll {

  private DataTable dataTable;
  private Query[] queries;
  private int replicaNum;

  private BigDecimal optimalCost = null;
  private MultiReplicas multiReplicas;

  private List<BigDecimal> history = new ArrayList<>();


  /**
   * Constructor of search all (brute force) method, using default replica number
   * @param dataTable info of the data table
   * @param queries info of the queries
   */
  public SearchAll(DataTable dataTable, Query[] queries) {
    this.dataTable = dataTable;
    this.queries = queries;
    this.replicaNum = Constant.REPLICA_NUMBER;
  }

  /**
   * Constructor of search all (brute force) method
   * @param dataTable info of the data table
   * @param queries info of the queries
   * @param replicaNumber customized replica number
   */
  public SearchAll(DataTable dataTable, Query[] queries, int replicaNumber){
    this.dataTable = dataTable;
    this.queries = queries;
    this.replicaNum = replicaNumber;
  }


  /**
   * generate optimal multi-replicas
   * @return
   */
  public MultiReplicas optimal() {
    List<int[]> singleReplicas = new Permutation().getPerm(0, dataTable.getColNum() - 1, dataTable.getColNum(), false);
    List<int[]> replicasOrder = new Permutation().getPerm(0, singleReplicas.size()-1, replicaNum, true);
    int counter = 0;
    for (int[] ro : replicasOrder) {
      MultiReplicas m = new MultiReplicas();
      for (int replicaIdx : ro)
        m.add(new Replica(dataTable, singleReplicas.get(replicaIdx)));
      BigDecimal cost = CostModel.cost(m, queries);
      System.out.println(counter++ + "/" + replicasOrder.size());
      history.add(cost);
      if(optimalCost == null || optimalCost.compareTo(cost) > 0){
        optimalCost = cost;
        multiReplicas = new MultiReplicas(m);
      }
    }
    return multiReplicas;
  }

  /**
   * Generate an optimal replica
   * @return
   */
  public Replica optimalReplica(){
    List<int[]> singleReplicas = new Permutation().getPerm(0, dataTable.getColNum() - 1, dataTable.getColNum(), false);
    Replica ans = null;
    for(int[] i : singleReplicas){
      Replica r = new Replica(dataTable, i);
      BigDecimal cost = CostModel.cost(r, queries);
      if(optimalCost == null || optimalCost.compareTo(cost) > 0){
        optimalCost = cost;
        ans = r;
      }
      history.add(optimalCost);
    }
    return ans;
  }

  public BigDecimal getOptimalCost() {
    return optimalCost;
  }

  public List<BigDecimal> getHistory() {
    return history;
  }
}
