package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import java.rmi.server.ExportException;

import javax.annotation.Nullable;

import edu.asu.ying.common.event.EventHandler;
import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.PageMetadata;
import edu.asu.ying.wellington.dfs.Table;
import edu.asu.ying.wellington.dfs.TableIdentifier;
import edu.asu.ying.wellington.dfs.TableNotFoundException;

/**
 *
 */
public class DFSServer implements DFSService {

  private final RemoteDFSService proxy;

  private final IncomingPageHandler pageDepository = new IncomingPageHandler();

  @Inject
  private DFSServer(DFSServiceExporter exporter) {
    try {
      this.proxy = exporter.export(this);
    } catch (ExportException e) {
      throw new RuntimeException(e);
    }

    // TODO: testing
    pageDepository.onIncomingPage.attach(new EventHandler<PageMetadata>() {
      @Override
      public boolean onEvent(Object sender, @Nullable PageMetadata args) {
        System.out.println("Got page! ".concat(args != null ? args.getID().toString() : ""));
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
  public Sink<PageMetadata> getPageDepository() {
    return pageDepository;
  }

  @Override
  public RemoteDFSService asRemote() {
    return proxy;
  }
}
