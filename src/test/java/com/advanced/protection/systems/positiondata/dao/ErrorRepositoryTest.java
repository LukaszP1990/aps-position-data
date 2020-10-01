package com.advanced.protection.systems.positiondata.dao;

import static com.advanced.protection.systems.positiondata.core.util.ErrorUtil.getErrors;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import com.advanced.protection.systems.multisensor.modelservice.constant.ErrorType;
import com.advanced.protection.systems.positiondata.core.util.DateUtil;
import com.advanced.protection.systems.positiondata.domain.ErrorDocument;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataMongoTest
@ExtendWith(value = MockitoExtension.class)
@ActiveProfiles("test")
class ErrorRepositoryTest {

	private static List<ErrorDocument> errors = getErrors();

	@Autowired
	private ErrorRepository errorRepository;

	@BeforeEach
	void setUp() {
		errorRepository.deleteAll()
				.thenMany(Flux.fromIterable(errors))
				.flatMap(errorRepository::save)
				.doOnNext(item -> System.out.println("Inserted:" + item.toString()))
				.blockLast();
	}

	@Test
	void shouldGetErrorByErrorParam() {
		StepVerifier.create(errorRepository.findByTextAndTimeAddedBetween(ErrorType.DATA_SAVE_ERROR.getText(), DateUtil.getRegularDateFrom(), DateUtil.getRegularDateTo()))
				.expectSubscription()
				.expectNextCount(4)
				.verifyComplete();
	}

	@Test
	void shouldNotGetErrorByErrorParam() {
		StepVerifier.create(errorRepository.findByTextAndTimeAddedBetween(ErrorType.DATA_SAVE_ERROR.getText(), new Date(), new Date()))
				.expectSubscription()
				.verifyComplete();
	}
}