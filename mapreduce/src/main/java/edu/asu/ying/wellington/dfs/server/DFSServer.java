package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nullable;

import edu.asu.ying.common.event.EventHandler;
import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.dfs.Table;
import edu.asu.ying.wellington.dfs.TableIdentifier;
import edu.asu.ying.wellington.dfs.TableNotFoundException;
import edu.asu.ying.wellington.mapreduce.server.LocalNode;

/**
 *
 */
@Singleton
public class DFSServer implements DFSService {

  private final LocalNode localNode;

  private final IncomingPageHandler pageDepository = new IncomingPageHandler();

  @Inject
  private DFSServer(LocalNode localNode) {
    this.localNode = localNode;

    // TODO: testing
    pageDepository.onIncomingPage.attach(new EventHandler<Page>() {
      @Override
      public boolean onEvent(Object sender, @Nullable Page args) {
        System.out.println("Got page! ".concat(args != null ? args.getPageID().toString() : ""));
        return true;
      }
    });
  }

  @Override
  public void start() {
  }

  @Override
  public Table getTable(TableIdentifier id) throws TableNotFoundException {
    throw new TableNotFoundException(id);
  }

  @Override
  public Sink<Page> getPageDepository() {
    return pageDepository;
  }
}
