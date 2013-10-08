package edu.asu.ying.mapreduce.daemon.web;

import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 *
 */
public final class JobStatusRestlet extends ServerResource {

  String jobId;

  @Override
  protected void doInit() throws ResourceException {
    this.jobId = (String) getRequest().getAttributes().get("jobid");
  }

  @Get("xml")
  public final Representation represent() {
    try {
      final DomRepresentation dom = new DomRepresentation(MediaType.TEXT_XML);

      final Document doc = dom.getDocument();
      final Element eleJob = doc.createElement("job");
      doc.appendChild(eleJob);

      final Element eleId = doc.createElement("id");
      eleId.appendChild(doc.createTextNode(this.jobId));
      eleJob.appendChild(eleId);

      final Element eleTable = doc.createElement("table");
      eleTable.appendChild(doc.createTextNode("lipsum"));
      eleJob.appendChild(eleTable);

      final Element eleMap = doc.createElement("fmap");
      eleMap.appendChild(doc.createTextNode("letterfreq"));
      eleJob.appendChild(eleMap);

      final Element eleState = doc.createElement("state");
      eleState.appendChild(doc.createTextNode("complete"));
      eleJob.appendChild(eleState);

      final Element eleStats = doc.createElement("statistics");
      final Element eleStatTime = doc.createElement("duration");
      eleStatTime.appendChild(
          doc.createTextNode(String.format("%.4f", 300.00 + (new Random()).nextDouble() * 220.00)));
      eleStats.appendChild(eleStatTime);
      eleJob.appendChild(eleStats);

      final Element eleHist = doc.createElement("history");
      eleJob.appendChild(eleHist);

      List<String> nodes = new ArrayList<>();

      for (int i = 0; i < 10; i++) {
        MessageDigest md = null;
        try {
          md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
        }
        String kk = byteArrayToHexString(md.digest(UUID.randomUUID().toString().getBytes()));
        nodes.add(kk);
      }

      for (int i = 0; i < 10 + (new Random()).nextInt(20); i++) {
        int j = (new Random()).nextInt(nodes.size());
        final Element en = doc.createElement("node");
        eleHist.appendChild(en);
        final Element enid = doc.createElement("peer-id");
        enid.appendChild(doc.createTextNode(nodes.get(j)));
        en.appendChild(enid);
      }

      doc.normalizeDocument();
      return dom;

    } catch (final IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static String byteArrayToHexString(byte[] b) {
    String result = "";
    String samp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_+=";
    for (int i = 0; i < Math.min(b.length, 22); i++) {
      int j = (b[i] & 0xFF) % samp.length();
      result += samp.substring(j, j + 1);
    }
    return result;
  }
}
