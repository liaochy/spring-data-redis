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

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisListCommands.Position;
import org.springframework.util.CollectionUtils;

/**
 * Default implementation of {@link ListOperations}.
 * 
 * @author Costin Leau
 */
class DefaultListOperations<K, V> extends AbstractOperations<K, V> implements
		ListOperations<K, V> {

	DefaultListOperations(RedisTemplate<K, V> template) {
		super(template);
	}

	public V index(K key, final long index) {
		return execute(new ValueDeserializingRedisCallback(key) {

			protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
				return connection.lIndex(rawKey, index);
			}
		}, true);
	}

	public V leftPop(K key) {
		return execute(new ValueDeserializingRedisCallback(key) {

			protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
				return connection.lPop(rawKey);
			}
		}, true);
	}

	public V leftPop(K key, long timeout, TimeUnit unit) {
		final int tm = (int) unit.toSeconds(timeout);

		return execute(new ValueDeserializingRedisCallback(key) {

			protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
				List<byte[]> lPop = connection.bLPop(tm, rawKey);
				return (CollectionUtils.isEmpty(lPop) ? null : lPop.get(1));
			}
		}, true);
	}

	public Long leftPush(K key, V... value) {
		final byte[] rawKey = rawKey(key);
		final byte[][] rawValue = rawValue(value);
		return execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) {
				return connection.lPush(rawKey, rawValue);
			}
		}, true);
	}

	public Long leftPushIfPresent(K key, V... value) {
		final byte[] rawKey = rawKey(key);
		final byte[][] rawValue = rawValue(value);
		return execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) {
				return connection.lPushX(rawKey, rawValue);
			}
		}, true);
	}

	public Long leftPush(K key, V pivot, V value) {
		final byte[] rawKey = rawKey(key);
		final byte[] rawPivot = rawValue(pivot);
		final byte[] rawValue = rawValue(value);
		return execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) {
				return connection.lInsert(rawKey, Position.BEFORE, rawPivot,
						rawValue);
			}
		}, true);
	}

	public Long size(K key) {
		final byte[] rawKey = rawKey(key);
		return execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) {
				return connection.lLen(rawKey);
			}
		}, true);
	}

	public List<V> range(K key, final long start, final long end) {
		final byte[] rawKey = rawKey(key);
		return execute(new RedisCallback<List<V>>() {
			public List<V> doInRedis(RedisConnection connection) {
				return deserializeValues(connection.lRange(rawKey, start, end));
			}
		}, true);
	}

	public Long remove(K key, final long count, Object value) {
		final byte[] rawKey = rawKey(key);
		final byte[] rawValue = rawValue(value);
		return execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) {
				return connection.lRem(rawKey, count, rawValue);
			}
		}, true);
	}

	public V rightPop(K key) {
		return execute(new ValueDeserializingRedisCallback(key) {

			protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
				return connection.rPop(rawKey);
			}
		}, true);
	}

	public V rightPop(K key, long timeout, TimeUnit unit) {
		final int tm = (int) unit.toSeconds(timeout);

		return execute(new ValueDeserializingRedisCallback(key) {

			protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
				List<byte[]> bRPop = connection.bRPop(tm, rawKey);
				return (CollectionUtils.isEmpty(bRPop) ? null : bRPop.get(1));
			}
		}, true);
	}

	public Long rightPush(K key, V... value) {
		final byte[] rawKey = rawKey(key);
		final byte[][] rawValue = rawValue(value);
		return execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) {
				return connection.rPush(rawKey, rawValue);
			}
		}, true);
	}

	public Long rightPushIfPresent(K key, V... value) {
		final byte[] rawKey = rawKey(key);
		final byte[][] rawValue = rawValue(value);
		return execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) {
				return connection.rPushX(rawKey, rawValue);
			}
		}, true);
	}

	public Long rightPush(K key, V pivot, V value) {
		final byte[] rawKey = rawKey(key);
		final byte[] rawPivot = rawValue(pivot);
		final byte[] rawValue = rawValue(value);

		return execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) {
				return connection.lInsert(rawKey, Position.AFTER, rawPivot,
						rawValue);
			}
		}, true);
	}

	public V rightPopAndLeftPush(K sourceKey, K destinationKey) {
		final byte[] rawDestKey = rawKey(destinationKey);

		return execute(new ValueDeserializingRedisCallback(sourceKey) {

			protected byte[] inRedis(byte[] rawSourceKey,
					RedisConnection connection) {
				return connection.rPopLPush(rawSourceKey, rawDestKey);
			}
		}, true);
	}

	public V rightPopAndLeftPush(K sourceKey, K destinationKey, long timeout,
			TimeUnit unit) {
		final int tm = (int) unit.toSeconds(timeout);
		final byte[] rawDestKey = rawKey(destinationKey);

		return execute(new ValueDeserializingRedisCallback(sourceKey) {

			protected byte[] inRedis(byte[] rawSourceKey,
					RedisConnection connection) {
				return connection.bRPopLPush(tm, rawSourceKey, rawDestKey);
			}
		}, true);
	}

	public void set(K key, final long index, V value) {
		final byte[] rawValue = rawValue(value);
		execute(new ValueDeserializingRedisCallback(key) {

			protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
				connection.lSet(rawKey, index, rawValue);
				return null;
			}
		}, true);
	}

	public void trim(K key, final long start, final long end) {
		execute(new ValueDeserializingRedisCallback(key) {

			protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
				connection.lTrim(rawKey, start, end);
				return null;
			}
		}, true);
	}
}