import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;

public class Output {
  public static void main(String args[]) throws IOException {
    BufferedReader bf = new BufferedReader(new FileReader("nohup.out"));
    String line;
    File f = new File("com_3_200.csv");
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    String vals = "";
    while ((line = bf.readLine()) != null) {
      String[] strs = line.split(",");
      System.out.println(strs[0]);
      if (strs[0].equals("SA: 7")) {
//        System.out.println("ss");
        if (!vals.equals("")) {
          vals += "\n";
          System.out.println(vals);
          fw.write(vals);
        }
        vals = strs[1];
      } else if (strs[0].equals("Divergent Design")) {
        vals += "," + strs[3];
      } else if (strs[0].equals("RITA")) {
        vals += "," + strs[5];
      }
    }
    if (vals != null)
      fw.write(vals);
    fw.close();
  }
}
