package com.advanced.protection.systems.positiondata.core.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaMapperUtil {

	public static <T>  String toBinary(T object) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T fromBinary(String object, Class<T> resultType) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(object, resultType);
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
