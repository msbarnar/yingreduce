package edu.asu.ying.test;

import org.junit.Assert;
import org.junit.Test;

import edu.asu.ying.wellington.dfs.table.TableIdentifier;

/**
 *
 */
public class TestTableIdentifier {

  final String tableName = "mytable";
  final int pageIndex = 5;

  @Test
  public void itParsesTableName() {
    TableIdentifier id = TableIdentifier.forString("mytable");
    Assert.assertEquals(id.toString(), this.tableName.concat("~-1"));
    Assert.assertEquals(id.getTableName(), this.tableName);
    Assert.assertEquals(id.getPageIndex(), TableIdentifier.NO_PAGE);
    Assert.assertFalse(id.isPageSpecified());
  }

  @Test
  public void itParsesPageIndices() {
    TableIdentifier id = TableIdentifier.forString(this.tableName);
    id = TableIdentifier.forString(id.forPage(this.pageIndex).toString());
    Assert.assertEquals(id.toString(), String.format("%s~%d", this.tableName, this.pageIndex));
    Assert.assertEquals(id.getTableName(), this.tableName);
    Assert.assertEquals(id.getPageIndex(), this.pageIndex);
    Assert.assertTrue(id.isPageSpecified());
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void itDoesntAcceptNegativePageIndices() {
    TableIdentifier id = TableIdentifier.forString("mytable");
    id = TableIdentifier.forString(id.forPage(-1).toString());
  }
}
