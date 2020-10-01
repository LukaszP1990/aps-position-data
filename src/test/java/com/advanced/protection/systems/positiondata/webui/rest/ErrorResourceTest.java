package com.advanced.protection.systems.positiondata.webui.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.advanced.protection.systems.multisensor.modelservice.constant.ErrorType;
import com.advanced.protection.systems.multisensor.modelservice.dto.ErrorDto;
import com.advanced.protection.systems.positiondata.AbstractIntegrationTest;
import com.advanced.protection.systems.positiondata.core.util.ErrorUtil;
import com.advanced.protection.systems.positiondata.dao.ErrorRepository;

import reactor.core.publisher.Flux;

class ErrorResourceTest extends AbstractIntegrationTest {

	@Autowired
	private ErrorRepository errorRepository;

	@BeforeEach
	void setUp() {
		errorRepository.deleteAll().thenMany(Flux.fromIterable(ErrorUtil.getErrors()))
				.flatMap(errorDocument -> errorRepository.save(errorDocument))
				.doOnNext(errorDocument -> System.out.println("Inserted errorDocument: " + errorDocument))
				.blockLast();
	}

	@Test
	void shouldGetErrorsByErrorParam() {
		var errorParam = ErrorUtil.getErrorParam();
		webTestClient.get().
				uri(uriBuilder ->
						uriBuilder
								.path("/api/position-data/errors")
								.queryParam("type", errorParam.getType())
								.queryParam("timeAddedFrom", errorParam.getTimeAddedFrom())
								.queryParam("timeAddedTo", errorParam.getTimeAddedTo())
								.build())
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(ErrorDto.class)
				.hasSize(4)
				.consumeWith(response ->
						Objects.requireNonNull(response.getResponseBody())
								.forEach(errorDto ->
										assertEquals(ErrorType.DATA_SAVE_ERROR.getText(), errorDto.getText()))
				);
	}

}