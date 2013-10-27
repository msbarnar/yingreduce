package edu.asu.ying.dfs.persistence;

import java.io.IOException;

import edu.asu.ying.dfs.PageName;

/**
 *
 */
public class PageExistsException extends IOException {

  public PageExistsException(PageName id) {
    super(id.toString());
  }
}
