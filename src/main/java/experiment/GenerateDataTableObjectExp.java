package experiment;

import dataloader.DataLoader;
import datamodel.DataTable;
import experiment.preprocess.GenerateDataTable;

import java.io.IOException;
import java.text.ParseException;

public class GenerateDataTableObjectExp {
  public static void main(String args[]) throws IOException, ParseException {
    String path = args[0];
    int[] cols = {1, 2, 3, 4, 11, 12, 13};
    DataTable dataTable = GenerateDataTable.getDataTableFromCsv(path, cols, 10);
    DataLoader.serialize(dataTable, path + ".obj");
  }
}
