package edu.asu.ying.mapreduce.rpc.channels;

import java.io.Serializable;
import java.util.Map;

public interface ChannelTransportSink
{
	public Map<String, Object> getProperties();
	
	public void close();
}
