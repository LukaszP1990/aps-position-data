package com.advanced.protection.systems.positiondata.dao;

import static com.advanced.protection.systems.positiondata.core.util.PositionDataUtil.POSITION_SENSOR_NAME;
import static com.advanced.protection.systems.positiondata.core.util.PositionDataUtil.getPositionDataDocuments;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import com.advanced.protection.systems.positiondata.core.util.DateUtil;
import com.advanced.protection.systems.positiondata.domain.PositionDataDocument;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataMongoTest
@ExtendWith(value = MockitoExtension.class)
@ActiveProfiles("test")
class PositionDataRepositoryTest {

	private static final String OK_SENSOR_NAME = POSITION_SENSOR_NAME.concat(String.valueOf(1));
	private static final String BAD_SENSOR_NAME = POSITION_SENSOR_NAME.concat(String.valueOf(5));
	private static List<PositionDataDocument> positionDataDocuments = getPositionDataDocuments();

	@Autowired
	private PositionDataRepository positionDataRepository;

	@BeforeEach
	void setUp() {
		positionDataRepository.deleteAll()
				.thenMany(Flux.fromIterable(positionDataDocuments))
				.flatMap(positionDataRepository::save)
				.doOnNext(item -> System.out.println("Inserted:" + item.toString()))
				.blockLast();
	}

	@Test
	void shouldGetPositionDataDocumentBySensorName() {
		StepVerifier.create(positionDataRepository.findBySensorName(OK_SENSOR_NAME))
				.expectSubscription()
				.expectNextCount(1)
				.verifyComplete();
	}

	@Test
	void shouldNotGetPositionDataDocumentBySensorName() {
		StepVerifier.create(positionDataRepository.findBySensorName(BAD_SENSOR_NAME))
				.expectSubscription()
				.verifyComplete();
	}

	@Test
	void shouldGetPositionDataDocumentsByDataParam() {
		StepVerifier.create(positionDataRepository.findByDataParam(1, 1,  DateUtil.getRegularDateFrom(), DateUtil.getRegularDateTo(), OK_SENSOR_NAME))
				.expectSubscription()
				.expectNextCount(1)
				.verifyComplete();
	}

	@Test
	void shouldNotGetPositionDataDocumentsByDataParam() {
		StepVerifier.create(positionDataRepository.findByDataParam(1, 1, new Date(), new Date(), BAD_SENSOR_NAME))
				.expectSubscription()
				.verifyComplete();
	}
}