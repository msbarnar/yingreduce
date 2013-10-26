package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;

import edu.asu.ying.wellington.dfs.PageName;

/**
 *
 */
public class PageNotAvailableLocallyException extends IOException {

  public PageNotAvailableLocallyException(PageName id) {
    super(id.toString());
  }

  public PageNotAvailableLocallyException(PageName id, Throwable cause) {
    super(id.toString(), cause);
  }
}
