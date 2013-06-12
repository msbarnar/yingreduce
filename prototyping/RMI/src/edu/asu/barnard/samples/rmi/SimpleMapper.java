package edu.asu.barnard.samples.rmi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class SimpleMapper 
	implements Mapper {
	
	private final String[] values = new String[] {"apples", "oranges", "bananas"};
	
	final ExecutorService pool = Executors.newFixedThreadPool(3);

	final class Map implements Callable<MapResult> {
		final Mappable mappable;
		
		public Map(final Mappable mappable) {
			this.mappable = mappable;
		}
		
		@Override
		public MapResult call() throws Exception {
			final String[] ret = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				ret[i] = String.valueOf(mappable.apply(values[i]));
			}
			return new MapResult(ret);
		}
	}
	
	public SimpleMapper() {
	}

	@Override
	public MapResult map(Mappable mappable) {
	
		try {
			return (new Map(mappable)).call();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void writeObject(final Object obj) throws FileNotFoundException, IOException {
		final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Mapper.SERIALIZED_STUB_FILE));
		try {
			out.writeObject(obj);
		} finally {
			out.close();
		}
	}
	
	public static void writeClass(final Class<?> clazz) throws FileNotFoundException, IOException {
		
	}
	
	public static void main(final String[] args) {
		System.setProperty("java.rmi.server.hostname", "127.0.0.1"); 
		System.setProperty("java.rmi.server.codebase", (new SimpleMapper()).getClass()
	            .getProtectionDomain().getCodeSource().getLocation().toString());
		System.out.println(System.getProperty("java.rmi.server.codebase"));
		final String policyPath = "file:/Users/matthew/code/mapreduce/prototyping/RMI/server.policy";
		System.setProperty("java.security.policy", policyPath);
		
		// Get a security manager to allow class definition transfer
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			final String name = "Mapper";
			final Mapper mapper = new SimpleMapper();
			final Mapper stub = (Mapper) UnicastRemoteObject.exportObject(mapper, 8888);
			writeObject(stub);
			
			System.out.println("Mapper stub written to file");
			
			System.out.println("Mapper bound to localhost");
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
