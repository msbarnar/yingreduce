package edu.asu.ying.wellington.dfs.server;

import java.io.IOException;

import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 *
 */
public class PageNotAvailableLocallyException extends IOException {

  public PageNotAvailableLocallyException(PageIdentifier id) {
    super(id.toString());
  }

  public PageNotAvailableLocallyException(PageIdentifier id, Throwable cause) {
    super(id.toString(), cause);
  }
}
