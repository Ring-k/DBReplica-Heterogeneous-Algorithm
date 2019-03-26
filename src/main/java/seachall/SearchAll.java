package seachall;

import constant.Constant;
import cost.CostModel;
import datamodel.DataTable;
import query.Query;
import replica.MultiReplicas;
import replica.Replica;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SearchAll {

  private DataTable dataTable;
  private Query[] queries;
  private int replicaNum;

  private BigDecimal optimalCost = null;
  private MultiReplicas multiReplicas;


  public SearchAll(DataTable dataTable, Query[] queries) {
    this.dataTable = dataTable;
    this.queries = queries;
    this.replicaNum = Constant.REPLICA_NUMBER;
  }


  public MultiReplicas optimal() {
    List<int[]> singleReplicas = Permutation.getPerm(0, dataTable.getColNum() - 1, dataTable.getColNum(), false);
    List<int[]> replicasOrder = Permutation.getPerm(0, Constant.REPLICA_NUMBER - 1, Constant.REPLICA_NUMBER, true);
    for (int[] ro : replicasOrder) {
      MultiReplicas m = new MultiReplicas();
      for (int replciaIdx : ro)
        m.add(new Replica(dataTable, singleReplicas.get(replciaIdx)));
      BigDecimal cost = CostModel.cost(m, queries);
      if(optimalCost == null || optimalCost.compareTo(cost) > 0){
        optimalCost = cost;
        multiReplicas = new MultiReplicas(m);
      }
    }
    return multiReplicas;
  }

  public BigDecimal getOptimalCost() {
    return optimalCost;
  }
}
