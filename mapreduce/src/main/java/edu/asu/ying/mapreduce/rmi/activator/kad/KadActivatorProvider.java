package edu.asu.ying.mapreduce.rmi.activator.kad;

import com.google.inject.Provider;
import edu.asu.ying.mapreduce.rmi.activator.Activator;
import edu.asu.ying.mapreduce.rmi.activator.kad.KadServerActivator;


/**
 *
 */
public class KadActivatorProvider
	implements Provider<Activator>
{
	private Activator singletonInstance;

	@Override
	public Activator get() {
		if (this.singletonInstance == null) {
			synchronized (this.singletonInstance) {
				if (this.singletonInstance == null) {
					this.singletonInstance = new KadServerActivator();
				}
			}
		}
		return this.singletonInstance;
	}
}
