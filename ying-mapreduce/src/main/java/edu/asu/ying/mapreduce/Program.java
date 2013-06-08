package edu.asu.ying.mapreduce;


/* Program entry point. Loads configuration, configures loggers, and starts an instance
 * of the daemon.
 */
public final class Program
{
	public static void main(String[] args) throws Throwable {
		Daemon d = Daemon.INSTANCE;
		d.run();
	}
}
