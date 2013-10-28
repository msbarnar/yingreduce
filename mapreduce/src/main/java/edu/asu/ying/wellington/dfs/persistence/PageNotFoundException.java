package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;

import edu.asu.ying.wellington.dfs.PageName;

/**
 *
 */
public class PageNotFoundException extends IOException {

  public PageNotFoundException(PageName name) {
    super(name.toString());
  }
}
