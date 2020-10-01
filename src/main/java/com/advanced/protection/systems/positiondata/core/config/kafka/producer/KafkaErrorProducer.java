package com.advanced.protection.systems.positiondata.core.config.kafka.producer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import com.advanced.protection.systems.multisensor.modelservice.dto.DataDto;
import com.advanced.protection.systems.multisensor.modelservice.dto.ErrorDto;
import com.advanced.protection.systems.positiondata.core.util.KafkaMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

@Slf4j
@Component
public class KafkaErrorProducer {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
	private final KafkaSender<String, String> kafkaSender;

	public KafkaErrorProducer(KafkaSender<String, String> kafkaSender) {
		this.kafkaSender = kafkaSender;
	}

	public Mono<SenderResult<Integer>> sendMessages(ErrorDto errorDto,
													String topic) {
		var payload = KafkaMapperUtil.toBinary(errorDto);
		SenderRecord<String, String, Integer> message =
				SenderRecord.create(new ProducerRecord<>(topic, payload), 1);

		return kafkaSender.send(Mono.just(message))
				.next()
				.doOnError(e -> log.error("Send failed", e))
				.doOnSuccess(r -> {
					RecordMetadata metadata = r.recordMetadata();
					log.info("Message: {} sent successfully, on topic: {}, partition: {} offset: {} and timestamp {}",
							r.correlationMetadata(),
							metadata.topic(),
							metadata.partition(),
							metadata.offset(),
							dateFormat.format(new Date(metadata.timestamp())));
					close();
				});
	}

	public void close() {
		kafkaSender.close();
	}
}
