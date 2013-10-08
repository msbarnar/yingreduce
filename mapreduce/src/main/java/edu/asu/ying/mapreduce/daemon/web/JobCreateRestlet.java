package edu.asu.ying.mapreduce.daemon.web;

import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import edu.asu.ying.mapreduce.daemon.DaemonSingleton;

/**
 *
 */
public final class JobCreateRestlet extends ServerResource {

  @Get("xml")
  public final Representation represent() {
    try {
      final DomRepresentation dom = new DomRepresentation(MediaType.TEXT_XML);

      final Document doc = dom.getDocument();
      final Element eleJob = doc.createElement("job");
      doc.appendChild(eleJob);

      final Element eleId = doc.createElement("id");
      final String jobId = UUID.randomUUID().toString();
      DaemonSingleton.get(8001).addJob(jobId);
      eleId.appendChild(doc.createTextNode(jobId));
      eleJob.appendChild(eleId);

      final Element eleTable = doc.createElement("table");
      eleTable.appendChild(doc.createTextNode(getQuery().getValues("table")));
      eleJob.appendChild(eleTable);

      final Element eleMap = doc.createElement("fmap");
      eleMap.appendChild(doc.createTextNode(getQuery().getValues("map")));
      eleJob.appendChild(eleMap);

      final Random rnd = new Random();

      final Element eleInitialNode = doc.createElement("initial-node");
      eleInitialNode.appendChild(doc.createTextNode(
          DaemonSingleton.get(8000 + rnd.nextInt(2)).getId()));
      eleJob.appendChild(eleInitialNode);

      final Element eleReducers = doc.createElement("reducers");
      for (int i = 0; i < 6; i++) {
        final Element er = doc.createElement("reducer");
        eleReducers.appendChild(er);
        final Element erId = doc.createElement("index");
        erId.appendChild(doc.createTextNode(String.valueOf(i)));
        er.appendChild(erId);
        final Element erPeer = doc.createElement("peer-id");
        erPeer
            .appendChild(doc.createTextNode(DaemonSingleton.get(8000 + rnd.nextInt(100)).getId()));
        er.appendChild(erPeer);
      }

      eleJob.appendChild(eleReducers);

      final Element eleState = doc.createElement("result");
      eleState.appendChild(doc.createTextNode("created"));
      eleJob.appendChild(eleState);

      doc.normalizeDocument();
      return dom;

    } catch (final IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
