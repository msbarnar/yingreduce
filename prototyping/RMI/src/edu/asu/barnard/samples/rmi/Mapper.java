package edu.asu.barnard.samples.rmi;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Future;

public interface Mapper
	extends Remote {
	
	public static final File SERIALIZED_STUB_FILE = new File("/Users/matthew/code/mapreduce/prototyping/RMI/stub.bin");
	public MapResult map(final Mappable mappable) throws RemoteException;
}
