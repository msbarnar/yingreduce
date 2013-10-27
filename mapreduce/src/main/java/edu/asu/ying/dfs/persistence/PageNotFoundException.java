package edu.asu.ying.dfs.persistence;

import java.io.IOException;

import edu.asu.ying.dfs.PageName;

/**
 *
 */
public class PageNotFoundException extends IOException {

  public PageNotFoundException(PageName id) {
    super(id.toString());
  }
}
