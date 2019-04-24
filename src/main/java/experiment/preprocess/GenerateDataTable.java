package experiment.preprocess;

import datamodel.DataTable;
import datamodel.Histogram;
import enummeration.CsvDataType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class GenerateDataTable {

  public static boolean isDate(String str) {
    String datePattern = "yyyy-MM-dd";
    DateFormat dateFormat = new SimpleDateFormat(datePattern);
    try {
      dateFormat.setLenient(false);
      dateFormat.parse(str);
      Date date = dateFormat.parse(str);
      String newDateStr = dateFormat.format(date);
      return str.equals(newDateStr);
    } catch (ParseException e) {
      return false;
    }
  }

  public static DataTable getDataTableFromCsv(String csvPath, int[] columnIndex, int bucketNumber) throws IOException, ParseException {
    BufferedReader headReader = new BufferedReader(new FileReader(csvPath));
    String[] firstLine = headReader.readLine().split("\\|");
    System.out.println(Arrays.toString(firstLine));
    CsvDataType[] dataTypes = new CsvDataType[columnIndex.length];
    for (int i = 0; i < columnIndex.length; i++) {
      if (isDate(firstLine[columnIndex[i]])) dataTypes[i] = CsvDataType.DATE;
      else dataTypes[i] = CsvDataType.DOUBLE;
    }
    headReader.close();
    String line = null;
    Histogram[] histograms = new Histogram[columnIndex.length];
    for (int i = 0; i < columnIndex.length; i++) {
      int curColumnIndex = columnIndex[i];
      List<Double> columnValues = new ArrayList<>();
      BufferedReader reader = new BufferedReader(new FileReader(csvPath));
      if (dataTypes[i].equals(CsvDataType.DOUBLE)) {
        while ((line = reader.readLine()) != null) {
          System.out.println(line);
          System.out.println(Arrays.toString(line.split("\\|")));
          columnValues.add(Double.parseDouble(line.split("\\|")[curColumnIndex]));
        }
      } else {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        while ((line = reader.readLine()) != null)
          columnValues.add((double) formatter.parse(line.split("\\|")[curColumnIndex]).getTime());
      }
      reader.close();
      histograms[i] = new Histogram(columnValues, bucketNumber);
    }
    return new DataTable(histograms);
  }



}
