package edu.asu.barnard.samples.rmi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class CountLetters
{
	public static Mapper getMapper() 
			throws NotBoundException, FileNotFoundException, 
			IOException, ClassNotFoundException {
	
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(Mapper.SERIALIZED_STUB_FILE));
		try {
			return (Mapper) in.readObject();
		} finally  {
			in.close();
		}
	}
	
	public static void main(final String[] args) {
		final String policyPath = "file:/Users/matthew/code/mapreduce/prototyping/RMIClient/client.policy";
		System.setProperty("java.security.policy", policyPath);
		System.setProperty("java.security.policy", policyPath.toString());
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			final String name = "Mapper";
			//final Registry registry = LocateRegistry.getRegistry("127.0.0.1");
			//final Mapper mapper = (Mapper) registry.lookup(name);
			final Mapper mapper = getMapper();
			final String[] result = (String[]) mapper.map(new LetterCounter()).getValue();
			for (String s : result) { 
				System.out.println(s);
			}
			
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
