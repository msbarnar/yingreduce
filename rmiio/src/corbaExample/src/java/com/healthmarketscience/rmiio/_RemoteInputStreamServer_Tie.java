// Tie class generated by rmic, do not edit.
// Contents subject to change without notice.

package com.healthmarketscience.rmiio;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.PortableServer.Servant;


public class _RemoteInputStreamServer_Tie extends Servant implements Tie {
    
    private RemoteInputStreamServer target = null;
    
    private static final String[] _type_ids = {
        "RMI:com.healthmarketscience.rmiio.RemoteInputStream:0000000000000000"
    };
    
    public void setTarget(Remote target) {
        this.target = (RemoteInputStreamServer) target;
    }
    
    public Remote getTarget() {
        return target;
    }
    
    public org.omg.CORBA.Object thisObject() {
        return _this_object();
    }
    
    public void deactivate() {
        try{
        _poa().deactivate_object(_poa().servant_to_id(this));
        }catch (org.omg.PortableServer.POAPackage.WrongPolicy exception){
        
        }catch (org.omg.PortableServer.POAPackage.ObjectNotActive exception){
        
        }catch (org.omg.PortableServer.POAPackage.ServantNotActive exception){
        
        }
    }
    
    public ORB orb() {
        return _orb();
    }
    
    public void orb(ORB orb) {
        try {
            ((org.omg.CORBA_2_3.ORB)orb).set_delegate(this);
        }
        catch(ClassCastException e) {
            throw new org.omg.CORBA.BAD_PARAM
                ("POA Servant requires an instance of org.omg.CORBA_2_3.ORB");
        }
    }
    
    public String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectId){
        return _type_ids;
    }
    
    public OutputStream  _invoke(String method, InputStream _in, ResponseHandler reply) throws SystemException {
        try {
            org.omg.CORBA_2_3.portable.InputStream in = 
                (org.omg.CORBA_2_3.portable.InputStream) _in;
            switch (method.length()) {
                case 4: 
                    if (method.equals("skip")) {
                        long arg0 = in.read_longlong();
                        int arg1 = in.read_long();
                        long result;
                        try {
                            result = target.skip(arg0, arg1);
                        } catch (IOException ex) {
                            String id = "IDL:java/io/IOEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,IOException.class);
                            return out;
                        }
                        OutputStream out = reply.createReply();
                        out.write_longlong(result);
                        return out;
                    }
                case 5: 
                    if (method.equals("close")) {
                        boolean arg0 = in.read_boolean();
                        try {
                            target.close(arg0);
                        } catch (IOException ex) {
                            String id = "IDL:java/io/IOEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,IOException.class);
                            return out;
                        }
                        OutputStream out = reply.createReply();
                        return out;
                    }
                case 9: 
                    if (method.equals("available")) {
                        int result;
                        try {
                            result = target.available();
                        } catch (IOException ex) {
                            String id = "IDL:java/io/IOEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,IOException.class);
                            return out;
                        }
                        OutputStream out = reply.createReply();
                        out.write_long(result);
                        return out;
                    }
                case 10: 
                    if (method.equals("readPacket")) {
                        int arg0 = in.read_long();
                        byte[] result;
                        try {
                            result = target.readPacket(arg0);
                        } catch (IOException ex) {
                            String id = "IDL:java/io/IOEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,IOException.class);
                            return out;
                        }
                        org.omg.CORBA_2_3.portable.OutputStream out = 
                            (org.omg.CORBA_2_3.portable.OutputStream) reply.createReply();
                        out.write_value(cast_array(result),byte[].class);
                        return out;
                    }
                case 20: 
                    if (method.equals("usingGZIPCompression")) {
                        boolean result;
                        try {
                            result = target.usingGZIPCompression();
                        } catch (IOException ex) {
                            String id = "IDL:java/io/IOEx:1.0";
                            org.omg.CORBA_2_3.portable.OutputStream out = 
                                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                            out.write_string(id);
                            out.write_value(ex,IOException.class);
                            return out;
                        }
                        OutputStream out = reply.createReply();
                        out.write_boolean(result);
                        return out;
                    }
            }
            throw new BAD_OPERATION();
        } catch (SystemException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new UnknownException(ex);
        }
    }
    
    // This method is required as a work-around for
    // a bug in the JDK 1.1.6 verifier.
    
    private Serializable cast_array(Object obj) {
        return (Serializable)obj;
    }
}