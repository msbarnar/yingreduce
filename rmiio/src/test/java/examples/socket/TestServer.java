/*
Copyright (c) 2012 James Ahlborn

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
USA
*/

package examples.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.healthmarketscience.rmiio.RmiioUtil;
import com.healthmarketscience.rmiio.socket.RMISocket;

/**
 *
 * @author James Ahlborn
 */
public class TestServer {

  public static final int REGISTRY_PORT = Registry.REGISTRY_PORT;
  public static final String QUIT_MSG = "quit";
  private static final String CHARSET = "UTF-8";

  public static class SocketServer implements RemoteSocketServer
  {
    public RMISocket.Source connect(RMISocket.Source remoteSource) 
      throws IOException {
      RMISocket socket = new RMISocket(remoteSource);
      RMISocket.Source source = socket.getSource();
      Thread t = new Thread(new EchoHandler(socket));
      t.setDaemon(true);
      t.start();
      return source;
    }
  }

  private static final class EchoHandler implements Runnable
  {
    private final RMISocket _socket;
    private final InputStream _in;
    private final OutputStream _out;

    private EchoHandler(RMISocket socket)
      throws IOException
    {
      _socket = socket;
      _in = socket.getInputStream();
      _out = socket.getOutputStream();
    }

    public void run() {
      byte[] buf = new byte[1024];
      try {
        while(true) {

          String msg = receiveMessage(_in, buf);
          if(msg == null) {
            break;
          }
          
          System.out.println("EchoServer recieved '" + msg + "'");
          sendMessage(_out, "server says: " + msg, buf);

          if(QUIT_MSG.equals(msg)) {
            break;
          }
        }
        
      } catch(IOException e) {
        System.err.println("EchoHandler failed");
        e.printStackTrace(System.err);
      } finally {
        RmiioUtil.closeQuietly(_socket);
      }
      System.out.println("EchoHandler done");
    }
  }
  
  public static void sendMessage(OutputStream out, String msg, byte[] tmpBuf)
    throws IOException
  {
    byte[] msgBytes = msg.getBytes(CHARSET);
    ByteBuffer bb = ByteBuffer.wrap(tmpBuf);
    bb.putInt(msgBytes.length);
    bb.put(msgBytes);
    out.write(tmpBuf, 0, msgBytes.length + 4);
    out.flush();
  }

  public static String receiveMessage(InputStream in, byte[] tmpBuf)
    throws IOException
  {
    int pos = 0;
    int readLen = 4;
    boolean gotMsgLen = false;
    while(true) {
      int numBytes = in.read(tmpBuf, pos, readLen - pos);
      if(numBytes < 0) {
        return null;
      }
      pos += numBytes;
          
      if(readLen == pos) {
        if(!gotMsgLen) {
          readLen = ByteBuffer.wrap(tmpBuf).getInt();
          gotMsgLen = true;
        } else {
          return new String(tmpBuf, 0, readLen, CHARSET);
        }

        pos = 0;
      }
    }
  }

  public static void main(String[] args) throws Exception
  {
    
    SocketServer server = new SocketServer();
    RemoteSocketServer stub = (RemoteSocketServer)
      UnicastRemoteObject.exportObject(server, 0);

    // bind to registry
    Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);
    registry.bind("RemoteSocketServer", stub);

    System.out.println("Server ready");    
  }
  
}
