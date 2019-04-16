package preprocess;

import java.io.*;

public class ChangeSeperator {

  public static void main(String args[]) throws IOException {
    BufferedReader bf = new BufferedReader(new FileReader("data\\lineitem2.tbl"));
    String readline = null;
    File f = new File("data\\lineitem3.tbl");
    if(f.exists()) f.delete();
    f.createNewFile();
    FileWriter fw = new FileWriter(f, true);
    while((readline = bf.readLine()) != null){
      String newline = readline.substring(0, readline.length()-1);
      fw.write(newline + "\n");
    }
    fw.flush();
    fw.close();
  }
}
