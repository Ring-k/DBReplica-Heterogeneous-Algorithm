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


  public SearchAll(DataTable dataTable, Query[] queries) {
    this.dataTable = dataTable;
    this.queries = queries;
    this.replicaNum = Constant.REPLICA_NUMBER;
  }


  public MultiReplicas optimal() {
    List<int[]> singleReplicas = Permutation.getPerm(0, dataTable.getColNum() - 1, dataTable.getColNum(), false);
    List<int[]> replicasOrder = Permutation.getPerm(0, singleReplicas.size()-1, Constant.REPLICA_NUMBER, true);
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

  public Replica optimalReplica(){
    List<int[]> singleReplicas = Permutation.getPerm(0, dataTable.getColNum() - 1, dataTable.getColNum(), false);
    Replica ans = null;
    int all = singleReplicas.size();
    int current = 1;
    for(int[] i : singleReplicas){
      Replica r = new Replica(dataTable, i);
      BigDecimal cost = CostModel.cost(r, queries);
//      System.out.println(current++ + "/" + all);
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
