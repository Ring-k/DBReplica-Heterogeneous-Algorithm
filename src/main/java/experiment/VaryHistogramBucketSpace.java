package experiment;

import dataloader.DataLoader;
import datamodel.DataTable;
import experiment.preprocess.GenerateDataTable;
import org.apache.lucene.util.RamUsageEstimator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;


public class VaryHistogramBucketSpace {

  public static void main(String args[]) throws IOException, ClassNotFoundException {
    String reportFile = "exp8_vary_histogram_buckets_from_10_10000.rpt";
    File f = new File(reportFile);
    if (!f.exists()) f.createNewFile();
    FileWriter fw = new FileWriter(f, true);

    int[] cols = {1, 2, 3, 4, 11, 12, 13};
    for (int i = 10; i <= 100; i += 10) {
      String result = "bucket = " + i + "\n";
//      DataTable dataTable = GenerateDataTable.getDataTableFromCsv("lineitem_s1_b", cols, i);
      DataTable dataTable = DataLoader.getDataTable("data_lineitem_s1_b" + i);
      result += "size: " + RamUsageEstimator.sizeOf(dataTable) + "\n";
      System.out.println("size: " + RamUsageEstimator.sizeOf(dataTable));
      result += "shallow size: " + RamUsageEstimator.shallowSizeOf(dataTable) + "\n";
      System.out.println("shallow size: " + RamUsageEstimator.shallowSizeOf(dataTable));
      result += "human size: " + RamUsageEstimator.humanSizeOf(dataTable) + "\n";
      System.out.println("human size: " + RamUsageEstimator.humanSizeOf(dataTable));
      result += "\n";
      fw.write(result);
    }
    fw.close();
  }
}
