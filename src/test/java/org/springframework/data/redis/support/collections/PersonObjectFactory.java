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

import java.util.UUID;

import org.springframework.data.redis.Address;
import org.springframework.data.redis.Person;

/**
 * @author Costin Leau
 */
public class PersonObjectFactory implements ObjectFactory<Person> {

	private int counter = 0;

	@Override
	public Person instance() {
		String uuid = UUID.randomUUID().toString();
		return new Person(uuid, uuid, ++counter, new Address(uuid, counter));
	}
}