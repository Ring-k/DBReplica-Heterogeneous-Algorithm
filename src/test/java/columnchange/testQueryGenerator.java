package columnchange;

import columnchange.datamodel.DataTable;
import columnchange.query.Query;
import columnchange.query.QueryGenerator;
import org.junit.Test;

import static columnchange.TestDataTable.generateDataTable;

public class testQueryGenerator {
  @Test
  public void test() {
    DataTable dataTable = generateDataTable();
    QueryGenerator qg = new QueryGenerator(100, dataTable);
    Query[] queries = qg.getQueries();
    for(Query q : queries){
      System.out.println(q.toString());
    }
  }
}
