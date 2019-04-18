package cassandra;

public class TableGenerateCommand {
  public static void main(String args[]){
      String s = "L_ORDERKEY,L_PARTKEY,L_SUPPKEY,L_LINENUMBER,L_SHIPDATE,L_COMMITDATE,L_RECEIPTDATE";
      String[] ss = s.split(",");
      int[] index = {1,0,4,2,3,5,6};
      String ans = "";
      for(int i : index){
        ans += ss[i] + ",";
      }
      System.out.println(ans);
  }
}
