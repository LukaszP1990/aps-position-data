package com.advanced.protection.systems.positiondata.webui.rest;

import static com.advanced.protection.systems.positiondata.core.util.PositionDataUtil.POSITION_SENSOR_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.advanced.protection.systems.multisensor.modelservice.constant.DataType;
import com.advanced.protection.systems.multisensor.modelservice.dto.DataDto;
import com.advanced.protection.systems.positiondata.AbstractIntegrationTest;
import com.advanced.protection.systems.positiondata.core.util.PositionDataUtil;
import com.advanced.protection.systems.positiondata.dao.PositionDataRepository;
import com.advanced.protection.systems.positiondata.feignclient.FeignRfClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class PositionDataResourceTest extends AbstractIntegrationTest {

	@Autowired
	private PositionDataRepository positionDataRepository;

	@MockBean
	private FeignRfClient feignRfClient;

	@BeforeEach
	void setUp() {
		positionDataRepository.deleteAll().thenMany(Flux.fromIterable(PositionDataUtil.getPositionDataDocuments()))
				.flatMap(positionDataDocument -> positionDataRepository.save(positionDataDocument))
				.doOnNext(positionDataDocument -> System.out.println("Inserted positionDataDocument: " + positionDataDocument))
				.blockLast();
	}

	@Test
	void shouldDownloadByDataParam() {
		var dataParam = PositionDataUtil.getDataParam();
		webTestClient.get().
				uri(uriBuilder ->
						uriBuilder
								.path("/api/position-data")
								.queryParam("lon", dataParam.getLon())
								.queryParam("lat", dataParam.getLat())
								.queryParam("timeAddedFrom", dataParam.getTimeAddedFrom())
								.queryParam("timeAddedTo", dataParam.getTimeAddedTo())
								.queryParam("rssi", dataParam.getRssi())
								.queryParam("sensorName", dataParam.getSensorName())
								.build())
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(DataDto.class)
				.hasSize(4)
				.consumeWith(response ->
						Objects.requireNonNull(response.getResponseBody())
								.forEach(dataDto ->
										assertEquals(DataType.POSITION, dataDto.getDataType()))
				);
	}

	@Test
	void downloadBySensorName() {
		var sensorName = POSITION_SENSOR_NAME.concat(String.valueOf(1));
		webTestClient.get().
				uri(uriBuilder ->
						uriBuilder
								.path("/api/position-data")
								.queryParam("sensorName", sensorName)
								.build())
				.exchange()
				.expectStatus().isOk()
				.expectBody(DataDto.class)
				.consumeWith(response ->
						assertEquals(DataType.POSITION, Objects.requireNonNull(response.getResponseBody()).getDataType())
				);
	}

	@Test
	void shouldConsumePositionData() {
		when(feignRfClient.downloadBySensorName(anyString()))
				.thenReturn(Mono.empty());

		webTestClient.get()
				.uri("/api/position-data/consume")
				.exchange()
				.expectStatus().isOk();
	}

}