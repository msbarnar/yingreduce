package edu.asu.ying.mapreduce.rpc.net;

import java.io.Serializable;

public interface Distributable
	extends Serializable
{
	public NetworkKey getNetworkKey();
}
