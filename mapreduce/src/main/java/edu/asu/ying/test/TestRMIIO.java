package edu.asu.ying.test;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.LogManager;

/**
 *
 */
public class TestRMIIO {

  @Test
  public void remoteStreaming() throws Exception {
    LogManager.getLogManager().reset();
    int port = 9007;
    File tempFile = File.createTempFile("incoming", ".dat");

    TestServer server = new TestServer(tempFile);
    Registry registry = LocateRegistry.createRegistry(port);
    registry.bind("RemoteServer", UnicastRemoteObject.exportObject(server, 0));

    TestClient client = new TestClient(tempFile, port);
    client.send();
    client.retrieve();
  }

  private final class TestClient {

    File file;
    Registry registry;
    final String message = "Hello, world!";

    TestClient(File file, int port) throws RemoteException {
      this.file = file;
      this.registry = LocateRegistry.getRegistry(port);
    }

    void send() throws IOException {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      DataOutputStream ostream = new DataOutputStream(stream);
      ostream.writeUTF(message);
      ostream.flush();
      byte[] data = stream.toByteArray();

      try (SimpleRemoteInputStream istream = new SimpleRemoteInputStream(
          new ByteArrayInputStream(data))) {
        RemoteServer server = (RemoteServer) registry.lookup("RemoteServer");
        server.send(istream.export());

      } catch (NotBoundException e) {
        throw new IOException(e);
      }
    }

    boolean retrieve() throws IOException {
      try (DataInputStream istream = new DataInputStream(new FileInputStream(file))) {
        String msg = istream.readUTF();
        return msg.equals(message);
      }
    }
  }

  private interface RemoteServer extends Remote {

    void send(RemoteInputStream stream) throws IOException;
  }

  private final class TestServer implements RemoteServer {

    File file;

    TestServer(File file) {
      this.file = file;
    }

    public void send(RemoteInputStream stream) throws IOException {
      InputStream input = RemoteInputStreamClient.wrap(stream);
      FileOutputStream ostream = null;
      try {
        ostream = new FileOutputStream(file);

        byte[] buf = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buf)) >= 0) {
          ostream.write(buf, 0, bytesRead);
        }
        ostream.flush();
      } finally {
        try {
          if (input != null) {
            input.close();
          }
        } finally {
          if (ostream != null) {
            ostream.close();
          }
        }
      }
    }
  }
}
