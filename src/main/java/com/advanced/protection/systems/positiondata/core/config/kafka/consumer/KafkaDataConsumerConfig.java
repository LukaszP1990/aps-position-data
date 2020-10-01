package com.advanced.protection.systems.positiondata.core.config.kafka.consumer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

@Configuration
public class KafkaDataConsumerConfig {

	private static final String BOOTSTRAP_SERVERS = "localhost:9092";
	private static final String GROUP_ID = "position-data-consumer";

	@Bean
	private Map<String, Object> consumerConfigs() {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		configuration.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
		configuration.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configuration.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configuration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		return configuration;
	}

	@Bean
	ReceiverOptions<String, String> kafkaReceiverOptions() {
		return ReceiverOptions.create(consumerConfigs());
	}

	@Bean
	KafkaReceiver<String, String> kafkaReceiver(ReceiverOptions<String, String> kafkaReceiverOptions) {
		return KafkaReceiver.create(kafkaReceiverOptions);
	}
}
