/*
 * Copyright 2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.redis.core;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationUtils;
import org.springframework.util.Assert;

/**
 * Internal base class used by various RedisTemplate XXXOperations
 * implementations.
 * 
 * @author Costin Leau
 */
abstract class AbstractOperations<K, V> {

	// utility methods for the template internal methods
	abstract class ValueDeserializingRedisCallback implements RedisCallback<V> {
		private Object key;

		public ValueDeserializingRedisCallback(Object key) {
			this.key = key;
		}

		public final V doInRedis(RedisConnection connection) {
			byte[] result = inRedis(rawKey(key), connection);
			return deserializeValue(result);
		}

		protected abstract byte[] inRedis(byte[] rawKey,
				RedisConnection connection);
	}

	RedisTemplate<K, V> template;

	AbstractOperations(RedisTemplate<K, V> template) {
		this.template = template;
	}

	RedisSerializer keySerializer() {
		return template.getKeySerializer();
	}

	RedisSerializer valueSerializer() {
		return template.getValueSerializer();
	}

	RedisSerializer hashKeySerializer() {
		return template.getHashKeySerializer();
	}

	RedisSerializer hashValueSerializer() {
		return template.getHashValueSerializer();
	}

	RedisSerializer stringSerializer() {
		return template.getStringSerializer();
	}

	<T> T execute(RedisCallback<T> callback, boolean b) {
		return template.execute(callback, b);
	}

	public RedisOperations<K, V> getOperations() {
		return template;
	}

	@SuppressWarnings("unchecked")
	byte[] rawKey(Object key) {
		Assert.notNull(key, "non null key required");
		return keySerializer().serialize(key);
	}

	byte[] rawString(String key) {
		return stringSerializer().serialize(key);
	}

	@SuppressWarnings("unchecked")
	byte[] rawValue(Object value) {
		return valueSerializer().serialize(value);
	}

	@SuppressWarnings("unchecked")
	byte[][] rawValue(Object... values) {
		byte[][] result = new byte[values.length][];
		for (int i = 0; i < values.length; i++) {
			result[i] = valueSerializer().serialize(values[i]);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	<HK> byte[] rawHashKey(HK hashKey) {
		Assert.notNull(hashKey, "non null hash key required");
		return hashKeySerializer().serialize(hashKey);
	}

	@SuppressWarnings("unchecked")
	<HV> byte[] rawHashValue(HV value) {
		return hashValueSerializer().serialize(value);
	}

	byte[][] rawKeys(K key, K otherKey) {
		final byte[][] rawKeys = new byte[2][];

		rawKeys[0] = rawKey(key);
		rawKeys[1] = rawKey(key);
		return rawKeys;
	}

	byte[][] rawKeys(Collection<K> keys) {
		return rawKeys(null, keys);
	}

	byte[][] rawKeys(K key, Collection<K> keys) {
		final byte[][] rawKeys = new byte[keys.size() + (key != null ? 1 : 0)][];

		int i = 0;

		if (key != null) {
			rawKeys[i++] = rawKey(key);
		}

		for (K k : keys) {
			rawKeys[i++] = rawKey(k);
		}

		return rawKeys;
	}

	@SuppressWarnings("unchecked")
	Set<V> deserializeValues(Set<byte[]> rawValues) {
		return SerializationUtils.deserialize(rawValues, valueSerializer());
	}

	@SuppressWarnings("unchecked")
	Set<TypedTuple<V>> deserializeTupleValues(Set<Tuple> rawValues) {
		Set<TypedTuple<V>> set = new LinkedHashSet<TypedTuple<V>>(
				rawValues.size());
		for (Tuple rawValue : rawValues) {
			set.add(new DefaultTypedTuple(valueSerializer().deserialize(
					rawValue.getValue()), rawValue.getScore()));
		}
		return set;
	}

	@SuppressWarnings("unchecked")
	List<V> deserializeValues(List<byte[]> rawValues) {
		return SerializationUtils.deserialize(rawValues, valueSerializer());
	}

	@SuppressWarnings("unchecked")
	<T> Set<T> deserializeHashKeys(Set<byte[]> rawKeys) {
		return SerializationUtils.deserialize(rawKeys, hashKeySerializer());
	}

	@SuppressWarnings("unchecked")
	<T> List<T> deserializeHashValues(List<byte[]> rawValues) {
		return SerializationUtils.deserialize(rawValues, hashValueSerializer());
	}

	@SuppressWarnings("unchecked")
	<HK, HV> Map<HK, HV> deserializeHashMap(Map<byte[], byte[]> entries) {
		// connection in pipeline/multi mode
		if (entries == null) {
			return null;
		}

		Map<HK, HV> map = new LinkedHashMap<HK, HV>(entries.size());

		for (Map.Entry<byte[], byte[]> entry : entries.entrySet()) {
			map.put((HK) deserializeHashKey(entry.getKey()),
					(HV) deserializeHashValue(entry.getValue()));
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	K deserializeKey(byte[] value) {
		return (K) keySerializer().deserialize(value);
	}

	@SuppressWarnings("unchecked")
	V deserializeValue(byte[] value) {
		return (V) valueSerializer().deserialize(value);
	}

	String deserializeString(byte[] value) {
		return (String) stringSerializer().deserialize(value);
	}

	@SuppressWarnings({ "unchecked" })
	<HK> HK deserializeHashKey(byte[] value) {
		return (HK) hashKeySerializer().deserialize(value);
	}

	@SuppressWarnings("unchecked")
	<HV> HV deserializeHashValue(byte[] value) {
		return (HV) hashValueSerializer().deserialize(value);
	}
}