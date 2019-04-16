package dataloader;

import datamodel.DataTable;
import query.Query;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * This class is used to deserialize file form disk
 */
public class DataLoader {


  private DataLoader() {
  }

  /**
   * Read an array of queries from disk, given the file path.
   *
   * @param path file path
   * @return the object, the array of queries
   */
  public static Query[] getQueries(String path) throws IOException, ClassNotFoundException {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
      return (Query[]) ois.readObject();
    }
  }

  /**
   * Read an array of queries from disk, using default file path - "data\\queries"
   *
   * @return the object, the array of queries
   */
  public static Query[] getQueries() throws IOException, ClassNotFoundException {
    return getQueries("data\\queries");
  }

  /**
   * Read a data table object from disk, using default file path - "data\\data_table"
   *
   * @return the object, an instance of DataTable
   */
  public static DataTable getDataTable() throws IOException, ClassNotFoundException {
    return getDataTable("data\\data_table");
  }

  /**
   * Read a data table object from disk, using customized file path.
   *
   * @param path the given file path
   * @return the object, an instance of DataTable
   */
  public static DataTable getDataTable(String path) throws IOException, ClassNotFoundException {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
      return (DataTable) ois.readObject();
    }
  }

}
