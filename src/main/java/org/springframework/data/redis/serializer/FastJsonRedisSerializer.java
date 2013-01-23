package org.springframework.data.redis.serializer;

import java.nio.charset.Charset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private Class<T> type;

	public FastJsonRedisSerializer(Class<T> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public T deserialize(byte[] bytes) throws SerializationException {
		if (SerializationUtils.isEmpty(bytes)) {
			return null;
		}
		try {
			return (T) JSON.parseObject(bytes, type);
		} catch (Exception ex) {
			throw new SerializationException("Could not read JSON: "
					+ ex.getMessage(), ex);
		}
	}

	public byte[] serialize(Object t) throws SerializationException {
		if (t == null) {
			return SerializationUtils.EMPTY_ARRAY;
		}
		try {
			return JSON.toJSONBytes(t, SerializerFeature.WriteMapNullValue);
		} catch (Exception ex) {
			throw new SerializationException("Could not write JSON: "
					+ ex.getMessage(), ex);
		}
	}

}