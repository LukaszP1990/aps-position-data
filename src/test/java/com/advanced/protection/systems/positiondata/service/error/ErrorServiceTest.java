package com.advanced.protection.systems.positiondata.service.error;

import static com.advanced.protection.systems.positiondata.core.util.ErrorUtil.createError;
import static com.advanced.protection.systems.positiondata.core.util.ErrorUtil.createErrorDto;
import static com.advanced.protection.systems.positiondata.core.util.ErrorUtil.getErrorParam;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.advanced.protection.systems.multisensor.modelservice.constant.ErrorType;
import com.advanced.protection.systems.multisensor.modelservice.dto.ErrorDto;
import com.advanced.protection.systems.positiondata.core.config.kafka.producer.KafkaErrorProducer;
import com.advanced.protection.systems.positiondata.core.converter.ErrorConverter;
import com.advanced.protection.systems.positiondata.core.util.DateUtil;
import com.advanced.protection.systems.positiondata.dao.ErrorRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ErrorServiceTest {

	private final ErrorConverter errorConverter = mock(ErrorConverter.class);
	private final ErrorRepository errorRepository = mock(ErrorRepository.class);
	private final KafkaErrorProducer kafkaErrorProducer = mock(KafkaErrorProducer.class);
	private ErrorService errorService = new ErrorService(errorConverter, errorRepository, kafkaErrorProducer);

	@Test
	void shouldGetErrors() {
		when(errorRepository.findByTextAndTimeAddedBetween(anyString(), any(), any()))
				.thenReturn(Flux.just(createError(1)));

		when(errorConverter.errorDocumentToErrorDto(any()))
				.thenReturn(createErrorDto());

		StepVerifier.create(errorService.getErrors(getErrorParam()))
				.expectSubscription()
				.expectNextMatches(this::isErrorMatch)
				.verifyComplete();
	}

	@Test
	void shouldSaveErrorDto() {

		when(errorRepository.save(any()))
				.thenReturn(Mono.just(createError(1)));

		when(errorConverter.errorDocumentToErrorDto(any()))
				.thenReturn(createErrorDto());

		when(errorConverter.errorDtoToErrorDocument(any()))
				.thenReturn(createError(1));

		when(kafkaErrorProducer.sendMessages(any(), anyString()))
				.thenReturn(Mono.empty());

		StepVerifier.create(errorService.saveErrorDto(ErrorType.DATA_SAVE_ERROR))
				.expectSubscription()
				.verifyComplete();
	}

	private boolean isErrorMatch(ErrorDto errorDto) {
		return Objects.nonNull(errorDto) &&
				ErrorType.DATA_SAVE_ERROR.getText().equals(errorDto.getText())&&
				DateUtil.getRegularDate().equals(errorDto.getTimeAdded());
	}
}