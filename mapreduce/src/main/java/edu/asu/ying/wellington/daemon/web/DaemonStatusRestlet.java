package edu.asu.ying.wellington.daemon.web;

import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Map;

import edu.asu.ying.wellington.daemon.DaemonSingleton;

/**
 *
 */
final class DaemonStatusRestlet extends ServerResource {

  @Get("xml")
  public final Representation represent() {
    try {
      final DomRepresentation dom = new DomRepresentation(MediaType.TEXT_XML);

      final Document doc = dom.getDocument();
      final Element eleDaemon = doc.createElement("daemon");
      doc.appendChild(eleDaemon);

      final Element elePeerID = doc.createElement("peer-id");
      elePeerID.appendChild(doc.createTextNode(
          DaemonSingleton.get(this.getHostRef().getHostPort()).getId()));
      eleDaemon.appendChild(elePeerID);

      final Element eleState = doc.createElement("state");
      eleState.appendChild(doc.createTextNode("running"));
      eleDaemon.appendChild(eleState);

      final Element eleTables = doc.createElement("tables");
      eleDaemon.appendChild(eleTables);
      final
      Map<String, Integer>
          tables =
          DaemonSingleton.get(this.getHostRef().getHostPort()).getTables();
      for (final String table : tables.keySet()) {
        final Element j = doc.createElement("table");
        eleTables.appendChild(j);
        final Element jid = doc.createElement("id");
        jid.appendChild(doc.createTextNode(table));
        j.appendChild(jid);
        final Element js = doc.createElement("page-count");
        js.appendChild(doc.createTextNode(String.valueOf(tables.get(table))));
        j.appendChild(js);
        if (this.getHostRef().getHostPort() == 8001) {
          final Element jr = doc.createElement("is-responsible-node");
          j.appendChild(jr);
        }
      }

      final Element eleJobs = doc.createElement("jobs");
      eleDaemon.appendChild(eleJobs);
      final
      Map<String, String>
          jobs =
          DaemonSingleton.get(this.getHostRef().getHostPort()).getJobs();
      for (final String job : jobs.keySet()) {
        final Element j = doc.createElement("job");
        eleJobs.appendChild(j);
        final Element jid = doc.createElement("id");
        jid.appendChild(doc.createTextNode(job));
        j.appendChild(jid);
        final Element js = doc.createElement("state");
        js.appendChild(doc.createTextNode(jobs.get(job)));
        j.appendChild(js);
      }

      doc.normalizeDocument();
      return dom;

    } catch (final IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
