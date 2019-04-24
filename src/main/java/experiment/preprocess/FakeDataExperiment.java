package experiment.preprocess;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

public class FakeDataExperiment {


  static int cnt = 0;
  static Stack<Integer> s = new Stack<>();
  static FileWriter fw;

  public static void run(int minv, int maxv, int curnum, int maxnum) throws IOException {
    if (curnum == maxnum) {
      cnt++;
      Integer[] res = s.toArray(new Integer[0]);
      String s = Arrays.toString(res);
      s = s.substring(1, s.length() - 1);
      fw.write(s + "\n");
      fw.flush();
      return;
    }

    for (int i = minv; i <= maxv; i++) {
      s.push(i);
      run(minv, maxv, curnum + 1, maxnum);
      s.pop();
    }
  }


  public static void main(String[] args) throws IOException {
    for (int i = 2; i <= 7; i++) {
      System.out.println(i);
      File f = new File("col" + i + ".csv");
      fw = new FileWriter(f, true);
      if (!f.exists()) f.createNewFile();
      FileWriter fw = new FileWriter(f, true);
      run(1, 10, 0, i);
      fw.close();
    }

  }

}
