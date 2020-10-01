package com.advanced.protection.systems.positiondata.service.data;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.advanced.protection.systems.multisensor.modelservice.dto.DataDto;
import com.advanced.protection.systems.positiondata.core.converter.PositionDataConverter;
import com.advanced.protection.systems.positiondata.core.util.DateUtil;
import com.advanced.protection.systems.positiondata.core.util.PositionDataUtil;
import com.advanced.protection.systems.positiondata.dao.PositionDataRepository;
import com.advanced.protection.systems.positiondata.feignclient.FeignRfClient;
import com.advanced.protection.systems.positiondata.service.error.ErrorServiceFacade;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PositionDataServiceTest {

	private static final String SENSOR_NAME = PositionDataUtil.POSITION_SENSOR_NAME.concat("1");
	private final PositionDataRepository positionDataRepository = mock(PositionDataRepository.class);
	private final PositionDataConverter positionDataConverter = mock(PositionDataConverter.class);
	private final KafkaReceiver kafkaReceiver = mock(KafkaReceiver.class);
	private final FeignRfClient feignRfClient = mock(FeignRfClient.class);
	private final ErrorServiceFacade errorServiceFacade = mock(ErrorServiceFacade.class);

	private PositionDataService positionDataService = new PositionDataService(positionDataRepository, positionDataConverter, kafkaReceiver, feignRfClient, errorServiceFacade);

	@Test
	void shouldGetPositionData() {
		when(positionDataRepository.findByDataParam(anyDouble(), anyDouble(), any(), any(), anyString()))
				.thenReturn(Flux.just(PositionDataUtil.createPositionData(1)));

		when(positionDataConverter.positionDataDocumentToDataDto(any()))
				.thenReturn(PositionDataUtil.createDataDto());

		StepVerifier.create(positionDataService.getPositionData(PositionDataUtil.getDataParam()))
				.expectSubscription()
				.expectNextMatches(this::isPositionDataMatch)
				.verifyComplete();
	}

	@Test
	void shouldSavePositionData() {
		when(positionDataRepository.save(any()))
				.thenReturn(Mono.just(PositionDataUtil.createPositionData(1)));

		when(positionDataConverter.dataDtoToPositionDataDocument(any()))
				.thenReturn(PositionDataUtil.createPositionData(1));

		when(positionDataConverter.positionDataDocumentToDataDto(any()))
				.thenReturn(PositionDataUtil.createDataDto());

		StepVerifier.create(positionDataService.consumePositionData())
				.expectSubscription()
				.expectNextCount(1)
				.verifyComplete();
	}

	@Test
	void getPositionDataBySensorName() {
		when(positionDataRepository.findBySensorName(anyString()))
				.thenReturn(Mono.just(PositionDataUtil.createPositionData(1)));

		when(positionDataConverter.positionDataDocumentToDataDto(any()))
				.thenReturn(PositionDataUtil.createDataDto());

		StepVerifier.create(positionDataService.getPositionDataBySensorName(SENSOR_NAME))
				.expectSubscription()
				.expectNextMatches(this::isPositionDataMatch)
				.verifyComplete();

	}

	private boolean isPositionDataMatch(DataDto dataDto) {
		return Objects.nonNull(dataDto) &&
				dataDto.getTargetId() == 1 &&
				dataDto.getLat() == 1d &&
				dataDto.getLon() == 1d &&
				dataDto.getAltitude() == 1d &&
				DateUtil.getRegularDate().equals(dataDto.getTimeAdded()) &&
				Objects.nonNull(dataDto.getSensorDto()) &&
				dataDto.getClientIpAddress().equals(PositionDataUtil.CLIENT_IP);
	}

}