package edu.asu.ying.mapreduce.rpc.channels;

import java.util.Map;

import edu.asu.ying.mapreduce.ui.Observable;

public interface ChannelTransportSink
	extends Observable
{
	public Map<String, Object> getProperties();
	
	public void close();
}
