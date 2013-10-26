package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;

import edu.asu.ying.wellington.dfs.PageName;

/**
 *
 */
public class PageExistsException extends IOException {

  public PageExistsException(PageName id) {
    super(id.toString());
  }
}
