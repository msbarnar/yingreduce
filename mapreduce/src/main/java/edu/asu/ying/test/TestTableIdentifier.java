package edu.asu.ying.test;

import org.junit.Assert;
import org.junit.Test;

import edu.asu.ying.wellington.dfs.table.TableIdentifier;

/**
 *
 */
public class TestTableIdentifier {

  @Test
  public void itParsesTableName() {
    TableIdentifier id = TableIdentifier.forString("mytable");
    Assert.assertEquals(id.toString(), "mytable");
    Assert.assertEquals(id.getPageIndex(), id.NO_PAGE);
  }

  @Test
  public void itParsesPageIndices() {
    TableIdentifier id = TableIdentifier.forString("mytable");
    id = TableIdentifier.forString(id.forPage(5).toString());
    Assert.assertEquals(id.toString(), "mytable");
    Assert.assertEquals(id.getPageIndex(), 5);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void itDoesntAcceptNegativePageIndices() {
    TableIdentifier id = TableIdentifier.forString("mytable");
    id = TableIdentifier.forString(id.forPage(-1).toString());
  }
}
