package heterogeneous.exp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TemperatureCsvTool {

  public static void main(String args[]) throws IOException {
    BufferedReader bf = new BufferedReader(new FileReader("HeterogeneousExp\\temperature\\temperature.csv"));
    String line = null;
    while((line = bf.readLine())!=null){
      System.out.println(line);
    }
  }

}
