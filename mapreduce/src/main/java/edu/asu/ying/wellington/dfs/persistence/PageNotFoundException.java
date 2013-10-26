package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;

import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 *
 */
public class PageNotFoundException extends IOException {

  public PageNotFoundException(PageIdentifier id) {
    super(id.toString());
  }
}
