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
import java.util.Collections;
import java.util.Set;

import org.springframework.data.redis.connection.RedisConnection;

/**
 * Default implementation of {@link SetOperations}.
 * 
 * @author Costin Leau
 */
class DefaultSetOperations<K, V> extends AbstractOperations<K, V> implements
		SetOperations<K, V> {

	public DefaultSetOperations(RedisTemplate<K, V> template) {
		super(template);
	}

	public Boolean add(K key, V... value) {
		final byte[] rawKey = rawKey(key);
		final byte[] rawValue = rawValue(value);
		return execute(new RedisCallback<Boolean>() {

			public Boolean doInRedis(RedisConnection connection) {
				return connection.sAdd(rawKey, rawValue);
			}
		}, true);
	}

	public Set<V> difference(K key, K otherKey) {
		return difference(key, Collections.singleton(otherKey));
	}

	public Set<V> difference(final K key, final Collection<K> otherKeys) {
		final byte[][] rawKeys = rawKeys(key, otherKeys);
		Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {

			public Set<byte[]> doInRedis(RedisConnection connection) {
				return connection.sDiff(rawKeys);
			}
		}, true);

		return deserializeValues(rawValues);
	}

	public Long differenceAndStore(K key, K otherKey, K destKey) {
		return differenceAndStore(key, Collections.singleton(otherKey), destKey);
	}

	public Long differenceAndStore(final K key, final Collection<K> otherKeys,
			K destKey) {
		final byte[][] rawKeys = rawKeys(key, otherKeys);
		final byte[] rawDestKey = rawKey(destKey);
		return execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) {
				return connection.sDiffStore(rawDestKey, rawKeys);
			}
		}, true);
	}

	public Set<V> intersect(K key, K otherKey) {
		return intersect(key, Collections.singleton(otherKey));
	}

	public Set<V> intersect(K key, Collection<K> otherKeys) {
		final byte[][] rawKeys = rawKeys(key, otherKeys);
		Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {

			public Set<byte[]> doInRedis(RedisConnection connection) {
				return connection.sInter(rawKeys);
			}
		}, true);

		return deserializeValues(rawValues);
	}

	public Long intersectAndStore(K key, K otherKey, K destKey) {
		return intersectAndStore(key, Collections.singleton(otherKey), destKey);
	}

	public Long intersectAndStore(K key, Collection<K> otherKeys, K destKey) {
		final byte[][] rawKeys = rawKeys(key, otherKeys);
		final byte[] rawDestKey = rawKey(destKey);
		return execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) {
				connection.sInterStore(rawDestKey, rawKeys);
				return null;
			}
		}, true);
	}

	public Boolean isMember(K key, Object o) {
		final byte[] rawKey = rawKey(key);
		final byte[] rawValue = rawValue(o);
		return execute(new RedisCallback<Boolean>() {

			public Boolean doInRedis(RedisConnection connection) {
				return connection.sIsMember(rawKey, rawValue);
			}
		}, true);
	}

	public Set<V> members(K key) {
		final byte[] rawKey = rawKey(key);
		Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {

			public Set<byte[]> doInRedis(RedisConnection connection) {
				return connection.sMembers(rawKey);
			}
		}, true);

		return deserializeValues(rawValues);
	}

	public Boolean move(K key, V value, K destKey) {
		final byte[] rawKey = rawKey(key);
		final byte[] rawDestKey = rawKey(destKey);
		final byte[] rawValue = rawValue(value);

		return execute(new RedisCallback<Boolean>() {

			public Boolean doInRedis(RedisConnection connection) {
				return connection.sMove(rawKey, rawDestKey, rawValue);
			}
		}, true);
	}

	public V randomMember(K key) {

		return execute(new ValueDeserializingRedisCallback(key) {

			protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
				return connection.sRandMember(rawKey);
			}
		}, true);
	}

	public Boolean remove(K key, Object o) {
		final byte[] rawKey = rawKey(key);
		final byte[] rawValue = rawValue(o);
		return execute(new RedisCallback<Boolean>() {

			public Boolean doInRedis(RedisConnection connection) {
				return connection.sRem(rawKey, rawValue);
			}
		}, true);
	}

	public V pop(K key) {
		return execute(new ValueDeserializingRedisCallback(key) {

			protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
				return connection.sPop(rawKey);
			}
		}, true);
	}

	public Long size(K key) {
		final byte[] rawKey = rawKey(key);
		return execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) {
				return connection.sCard(rawKey);
			}
		}, true);
	}

	public Set<V> union(K key, K otherKey) {
		return union(key, Collections.singleton(otherKey));
	}

	public Set<V> union(K key, Collection<K> otherKeys) {
		final byte[][] rawKeys = rawKeys(key, otherKeys);
		Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {

			public Set<byte[]> doInRedis(RedisConnection connection) {
				return connection.sUnion(rawKeys);
			}
		}, true);

		return deserializeValues(rawValues);
	}

	public Long unionAndStore(K key, K otherKey, K destKey) {
		return unionAndStore(key, Collections.singleton(otherKey), destKey);
	}

	public Long unionAndStore(K key, Collection<K> otherKeys, K destKey) {
		final byte[][] rawKeys = rawKeys(key, otherKeys);
		final byte[] rawDestKey = rawKey(destKey);
		return execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) {
				return connection.sUnionStore(rawDestKey, rawKeys);
			}
		}, true);
	}
}