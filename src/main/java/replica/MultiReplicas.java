package replica;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class represent a strategy for multiple replicas design. It contains a map from Replica class to Integer.
 * The key, Replica, is a replica design in it, while the Integer indicates duplication, that is the number of such
 * replica.
 */
public class MultiReplicas {
  private Map<Replica, Integer> replicas;

  /**
   * Copy constructor, construct a multi-replica using another one. Deep copy.
   *
   * @param multiReplicas, another multi-replica.
   */
  public MultiReplicas(MultiReplicas multiReplicas) {
    replicas = new HashMap<>();
    for (Map.Entry<Replica, Integer> en : multiReplicas.getReplicas().entrySet())
      replicas.put(new Replica(en.getKey()), en.getValue());
  }

  /**
   * Constructor, construct an empty multi-replica strategy.
   */
  public MultiReplicas() {
    replicas = new HashMap<>();
  }

//  /**
//   * Constructor, using a list of replicas to construct a new multi-replica strategy.
//   * First create a new empty multi-replica, then traverse all replicas in the list, put
//   * them to the map.
//   * @param replicas, a list of replicas
//   */
//  public MultiReplicas(Replica[] replicas) {
//    this.replicas = new HashMap<>();
//    for(Replica replica : replicas){
//      if(this.replicas.keySet().contains(replica))
//        this.replicas.put(replica, this.replicas.get(replica) + 1);
//      else
//        this.replicas.put(replica, 1);
//    }
//  }

  public Replica[] getReplicasArray(boolean isDuplicated){
    if(isDuplicated) {
      Replica[] ans = new Replica[getReplicaNum()];
      int cnt = 0;
      for (Map.Entry<Replica, Integer> en : replicas.entrySet())
        for (int i = 0; i < en.getValue(); i++)
          ans[cnt++] = new Replica(en.getKey());
      return ans;
    }else{
      return replicas.keySet().toArray(new Replica [0]).clone();
    }
  }

  /**
   * Get the number of replicas
   * @return the number of replicas
   */
  public int getReplicaNum() {
    if (replicas.size() == 0) return 0;
    int sum = 0;
    for (Integer i : replicas.values()) sum += i;
    return sum;
  }

  /**
   * Add replicas into the multi-replica solution
   * @param replica, a new replica
   * @return the result of multi-replica solution
   */
  public MultiReplicas add(Replica replica) {
    if (this.replicas.keySet().contains(replica))
      this.replicas.put(replica, this.replicas.get(replica) + 1);
    else
      this.replicas.put(replica, 1);
    return this;
  }

  /**
   * Get a string, describing the orders of replicas in current multi-replica solution.
   * @return, a string, orders
   */
  public String getOrderString() {
    String ans = "orders: { ";
    for (Map.Entry<Replica, Integer> en : replicas.entrySet())
      for (int i = 0; i < en.getValue(); i++)
        ans += Arrays.toString(en.getKey().getOrder());
    ans += " }";
    return ans;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MultiReplicas that = (MultiReplicas) o;
    return Objects.equals(replicas, that.replicas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(replicas);
  }

  @Override
  public String toString() {
    String str = "MultiReplicas: \n";
    int cnt = 0;
    for (Map.Entry<Replica, Integer> i : replicas.entrySet())
      for (int j = 0; j < i.getValue(); j++)
        str += "replica " + cnt++ + ": \n" + i.getKey().toString();

    str += "\n";
    return str;
  }

  public Map<Replica, Integer> getReplicas() {
    return replicas;
  }
}
