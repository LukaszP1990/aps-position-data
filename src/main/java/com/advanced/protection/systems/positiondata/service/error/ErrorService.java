package com.advanced.protection.systems.positiondata.service.error;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.advanced.protection.systems.multisensor.modelservice.constant.ErrorType;
import com.advanced.protection.systems.multisensor.modelservice.dto.ErrorDto;
import com.advanced.protection.systems.multisensor.modelservice.param.ErrorParam;
import com.advanced.protection.systems.positiondata.core.config.kafka.producer.KafkaErrorProducer;
import com.advanced.protection.systems.positiondata.core.converter.ErrorConverter;
import com.advanced.protection.systems.positiondata.dao.ErrorRepository;
import com.advanced.protection.systems.positiondata.domain.ErrorDocument;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
class ErrorService {

	private static final String ERROR = "error-topic";
	private final ErrorConverter errorConverter;
	private final ErrorRepository errorRepository;
	private final KafkaErrorProducer kafkaErrorProducer;

	ErrorService(final ErrorConverter errorConverter,
				 final ErrorRepository errorRepository,
				 KafkaErrorProducer kafkaErrorProducer) {
		this.errorConverter = errorConverter;
		this.errorRepository = errorRepository;
		this.kafkaErrorProducer = kafkaErrorProducer;
	}

	Flux<ErrorDto> getErrors(ErrorParam errorParam) {
		log.info("get errors by errorParam: {} ", errorParam);
		return Try.of(() -> errorParam)
				.map(localDateTimes -> errorRepository.findByTextAndTimeAddedBetween(errorParam.getType(), errorParam.getTimeAddedFrom(), errorParam.getTimeAddedTo()))
				.map(errorDocumentFlux -> errorDocumentFlux.collectList()
						.filter(errorDocuments -> !errorDocuments.isEmpty())
						.map(this::getErrorDtos)
						.flatMapMany(Flux::fromIterable))
				.getOrNull();
	}

	Mono<ErrorDto> saveErrorDto(ErrorType errorType) {
		log.info("save error: {} ", errorType);
		var errorDto = getMessageDto(errorType);
		return Mono.justOrEmpty(errorDto)
				.filter(Objects::nonNull)
				.flatMap(error -> kafkaErrorProducer.sendMessages(error, ERROR))
				.map(senderResult -> {
					kafkaErrorProducer.close();
					return senderResult;
				})
				.flatMap(senderResult -> Mono.just(errorDto))
				.doOnSuccess(senderResult ->  errorRepository.save(errorConverter.errorDtoToErrorDocument(errorDto)));
	}

	private List<ErrorDto> getErrorDtos(List<ErrorDocument> errorDocuments) {
		log.info("convert errorDtosDocuments: {} to errorDtos", errorDocuments);
		return errorDocuments.stream()
				.map(errorConverter::errorDocumentToErrorDto)
				.collect(Collectors.toList());
	}

	private ErrorDto getMessageDto(ErrorType errorType) {
		return ErrorDto.builder()
				.text(errorType.getText())
				.timeAdded(new Date())
				.build();
	}

}
