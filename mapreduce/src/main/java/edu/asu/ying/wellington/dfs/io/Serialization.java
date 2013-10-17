package edu.asu.ying.wellington.dfs.io;

/**
 *
 */
public interface Serialization<T> {

  Serializer<T> getSerializer(Class<T> cls);

  Deserializer<T> getDeserializer(Class<T> cls);
}
