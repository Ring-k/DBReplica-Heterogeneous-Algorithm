package experiment.cassandra;

public class TableGenerateCommand {


  static int[] parse(String[] strs) {
    int[] res = new int[strs.length];
    for (int i = 0; i < strs.length; i++) {
      res[i] = Integer.parseInt(strs[i]);
    }
    return res;
  }

  static int[][] parse(String str) {
    String[] ss = str.split("]\\[");
    int[][] res = new int[ss.length][ss[0].length()];
    for (int i = 0; i < ss.length; i++) {
      res[i] = parse(ss[i].split(","));
    }
    return res;
  }

  public static void main(String args[]) {
    String in = "0,5,2,4,1,6,3][0,5,2,4,1,6,3][0,5,2,4,1,6,3";

    String s = "L_ORDERKEY,L_PARTKEY,L_SUPPKEY,L_LINENUMBER,L_SHIPDATE,L_COMMITDATE,L_RECEIPTDATE";
    String[] ss = s.split(",");
    int[][] ins = parse(in);
    for(int i = 0; i < ins.length; i++){
      int[] index = ins[i];
      String ans = "";
      for (int j : index) {
        ans += ss[j] + ",";
      }
      System.out.println(ans);
    }


  }
}
