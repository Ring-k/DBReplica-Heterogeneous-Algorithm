package preprocess;

import java.io.*;

public class AddPartitionKey {
  public static void main(String args[]) throws IOException {
    String path = "data\\lineitem.tbl";
    String newPath = "data\\lineitem2.tbl";

    File f = new File(newPath);
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    BufferedReader reader = new BufferedReader(new FileReader(path));
    String readLine = null;
    while((readLine = reader.readLine())!=null){
      String newLine = "1|" + readLine+"\n";
      fw.write(newLine);
    }
    fw.flush();
    fw.close();
    reader.close();
  }
}
