package replica;


import java.util.*;

public class MultiReplicas {
  private Map<Replica, Integer> replicas;

  public MultiReplicas(MultiReplicas multiReplicas) {
    replicas = new HashMap<>();
    for (Map.Entry<Replica, Integer> en : multiReplicas.getReplicas().entrySet())
      replicas.put(new Replica(en.getKey()), en.getValue());
  }

  public MultiReplicas(Map<Replica, Integer> multiReplicas) {
    replicas = new HashMap<>();
    for (Map.Entry<Replica, Integer> en : multiReplicas.entrySet())
      replicas.put(en.getKey(), en.getValue());
  }

  public MultiReplicas() {
    replicas = new HashMap<>();
  }

  public MultiReplicas(Replica[] replicas) {
    this.replicas = new HashMap<>();
    for (int i = 0; i < replicas.length; i++) {
      if (this.replicas.keySet().contains(replicas[i]))
        this.replicas.put(replicas[i], this.replicas.get(replicas[i]) + 1);
      else
        this.replicas.put(replicas[i], 1);
    }
  }

  public int getReplicaNum() {
    if (replicas.size() == 0) return 0;
    int sum = 0;
    for (Integer i : replicas.values()) sum += i;
    return sum;
  }

  public MultiReplicas add(Replica replica) {
    if (this.replicas.keySet().contains(replica))
      this.replicas.put(replica, this.replicas.get(replica) + 1);
    else
      this.replicas.put(replica, 1);
    return this;
  }

  public String getOrderString() {
    String ans = "orders: { ";
    for (Map.Entry<Replica, Integer> en : replicas.entrySet()) {
      for (int i = 0; i < en.getValue(); i++)
        ans += Arrays.toString(en.getKey().getOrder());
    }
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
