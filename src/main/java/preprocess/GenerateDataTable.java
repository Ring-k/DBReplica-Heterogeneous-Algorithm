package preprocess;

import datamodel.DataTable;
import datamodel.Histogram;
import query.Query;
import query.QueryGenerator;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GenerateDataTable {

  public static void main(String args[]) throws IOException, ParseException, NoSuchAlgorithmException {
    String path = "data\\lineitem.tbl";

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String line = null;
    Histogram[] histograms = new Histogram[7];

    int[] cols = {0, 1, 2, 3, 10, 11, 12};
    for (int i = 0; i < cols.length; i++) {
      System.out.println(i);
      List<Double> ls = new ArrayList<>();
      int index = cols[i];
      BufferedReader reader = new BufferedReader(new FileReader(path));
      while ((line = reader.readLine()) != null) {
        String[] strs = line.split("\\|");
//        System.out.println(Arrays.toString(strs));
        String str = strs[index];
        Double val;
        if (index == 10 || index == 11 || index == 12)
          val = (double) formatter.parse(str).getTime();
        else
          val = Double.parseDouble(str);
        ls.add(val);
      }
      histograms[i] = new Histogram(ls, 100);
    }

    DataTable dataTable = new DataTable(histograms);
    System.out.println(dataTable.toString());
    Query[] queries = new QueryGenerator(10000, dataTable).getQueries();
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(new FileOutputStream("data\\queries"));
      oos.writeObject(queries);
      oos = new ObjectOutputStream(new FileOutputStream("data\\data_table"));
      oos.writeObject(dataTable);
      oos.flush();
      oos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
