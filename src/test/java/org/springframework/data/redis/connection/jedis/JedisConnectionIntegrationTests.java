/*
 * Copyright 2010-2011 the original author or authors.
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

package org.springframework.data.redis.connection.jedis;

import org.junit.Test;
import org.springframework.data.redis.SettingsUtils;
import org.springframework.data.redis.connection.AbstractConnectionIntegrationTests;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Transaction;

public class JedisConnectionIntegrationTests extends
		AbstractConnectionIntegrationTests {

	JedisConnectionFactory factory;

	public JedisConnectionIntegrationTests() {
		factory = new JedisConnectionFactory();
		factory.setUsePool(true);
		factory.setPort(SettingsUtils.getPort());
		factory.setHostName(SettingsUtils.getHost());

		factory.afterPropertiesSet();
	}

	protected RedisConnectionFactory getConnectionFactory() {
		return factory;
	}

	@Test
	public void testMulti() throws Exception {
		byte[] key = "key".getBytes();
		byte[] value = "value".getBytes();

		BinaryJedis jedis = (BinaryJedis) connection.getNativeConnection();
		Transaction multi = jedis.multi();
		// connection.set(key, value);
		multi.set(value, key);
		System.out.println(multi.exec());

		connection.multi();
		connection.set(value, key);
		System.out.println(connection.exec());
	}

	// @Test
	// public void setAdd() {
	// connection.sadd("s1", "1");
	// connection.sadd("s1", "2");
	// connection.sadd("s1", "3");
	// connection.sadd("s2", "2");
	// connection.sadd("s2", "3");
	// Set<String> intersection = connection.sinter("s1", "s2");
	// System.out.println(intersection);
	//
	//
	// }
	//
	// @Test
	// public void setIntersectionTests() {
	// RedisTemplate template = new RedisTemplate(clientFactory);
	// RedisSet s1 = new RedisSet(template, "s1");
	// s1.add("1");
	// s1.add("2");
	// s1.add("3");
	// RedisSet s2 = new RedisSet(template, "s2");
	// s2.add("2");
	// s2.add("3");
	// Set s3 = s1.intersection("s3", s1, s2);
	// for (Object object : s3) {
	// System.out.println(object);
	// }
}