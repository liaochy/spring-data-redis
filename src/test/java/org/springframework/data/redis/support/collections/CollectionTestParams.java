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
package org.springframework.data.redis.support.collections;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.data.redis.Person;
import org.springframework.data.redis.SettingsUtils;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.OxmSerializer;
import org.springframework.oxm.xstream.XStreamMarshaller;

/**
 * @author Costin Leau
 */
public abstract class CollectionTestParams {

	public static Collection<Object[]> testParams() {
		// XStream serializer
		XStreamMarshaller xstream = new XStreamMarshaller();
		try {
			xstream.afterPropertiesSet();
		} catch (Exception ex) {
			throw new RuntimeException("Cannot init XStream", ex);
		}
		OxmSerializer serializer = new OxmSerializer(xstream, xstream);
		JacksonJsonRedisSerializer<Person> jsonSerializer = new JacksonJsonRedisSerializer<Person>(
				Person.class);

		// create Jedis Factory
		ObjectFactory<String> stringFactory = new StringObjectFactory();
		ObjectFactory<Person> personFactory = new PersonObjectFactory();

		JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();
		jedisConnFactory.setUsePool(false);
		jedisConnFactory.setPort(SettingsUtils.getPort());
		jedisConnFactory.setHostName(SettingsUtils.getHost());

		jedisConnFactory.afterPropertiesSet();

		RedisTemplate<String, String> stringTemplate = new StringRedisTemplate(
				jedisConnFactory);
		RedisTemplate<String, Person> personTemplate = new RedisTemplate<String, Person>();
		personTemplate.setConnectionFactory(jedisConnFactory);
		personTemplate.afterPropertiesSet();

		RedisTemplate<String, String> xstreamStringTemplate = new RedisTemplate<String, String>();
		xstreamStringTemplate.setConnectionFactory(jedisConnFactory);
		xstreamStringTemplate.setDefaultSerializer(serializer);
		xstreamStringTemplate.afterPropertiesSet();

		RedisTemplate<String, Person> xstreamPersonTemplate = new RedisTemplate<String, Person>();
		xstreamPersonTemplate.setConnectionFactory(jedisConnFactory);
		xstreamPersonTemplate.setValueSerializer(serializer);
		xstreamPersonTemplate.afterPropertiesSet();

		// json
		RedisTemplate<String, Person> jsonPersonTemplate = new RedisTemplate<String, Person>();
		jsonPersonTemplate.setConnectionFactory(jedisConnFactory);
		jsonPersonTemplate.setValueSerializer(jsonSerializer);
		jsonPersonTemplate.afterPropertiesSet();

		return Arrays.asList(new Object[][] {
				{ stringFactory, stringTemplate },
				// { stringFactory, stringTemplateJR },
				// { personFactory, personTemplateJR },
				{ personFactory, personTemplate },
				{ stringFactory, xstreamStringTemplate },
				{ personFactory, xstreamPersonTemplate },
				// { stringFactory, xstreamStringTemplateJR },
				// { personFactory, xstreamPersonTemplateJR },
				{ personFactory, jsonPersonTemplate } });
	}
}
