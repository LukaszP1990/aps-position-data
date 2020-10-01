package com.advanced.protection.systems.positiondata.core.config.kafka.producer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.advanced.protection.systems.multisensor.modelservice.dto.ErrorDto;

import lombok.extern.slf4j.Slf4j;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

@Slf4j
@Configuration
public class KafkaErrorProducerConfig {

	private static final String BOOTSTRAP_SERVERS = "localhost:9092";

	@Bean
	private Map<String, Object> producerConfigs() {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		configuration.put(ProducerConfig.ACKS_CONFIG, "all");
		configuration.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configuration.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return configuration;
	}

	@Bean
	SenderOptions<String, ErrorDto> kafkaSenderOptions() {
		return SenderOptions.create(producerConfigs());
	}

	@Bean
	public KafkaSender<String, ErrorDto> kafkaSender(SenderOptions<String, ErrorDto> kafkaSenderOptions) {
		return KafkaSender.create(kafkaSenderOptions);
	}

}
