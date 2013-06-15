package edu.asu.ying.mapreduce.rmi.activator.kad;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 *
 */
public interface RemoteTest
	extends Remote
{
	public String getString() throws RemoteException;
}
