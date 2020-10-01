package com.advanced.protection.systems.positiondata.core.converter;


import static com.advanced.protection.systems.positiondata.core.util.PositionDataUtil.CLIENT_IP;
import static com.advanced.protection.systems.positiondata.core.util.PositionDataUtil.POSITION_SENSOR_NAME;
import static com.advanced.protection.systems.positiondata.core.util.PositionDataUtil.createDataDto;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.advanced.protection.systems.multisensor.modelservice.constant.SensorType;
import com.advanced.protection.systems.multisensor.modelservice.dto.DataDto;
import com.advanced.protection.systems.positiondata.core.util.PositionDataUtil;
import com.advanced.protection.systems.positiondata.domain.PositionDataDocument;

class PositionDataConverterTest {

	private static final DataDto dataDto = createDataDto();
	private static final PositionDataDocument positionData = PositionDataUtil.createPositionData(1);
	private PositionDataConverter positionDataConverter = Mappers.getMapper(PositionDataConverter.class);

	@Test
	void shouldConvertPositionDataDocumentToDataDto() {
		var dataDto = positionDataConverter.positionDataDocumentToDataDto(positionData);

		assertNotNull(dataDto);
		assertAll(() -> {
			assertEquals(1, dataDto.getLon());
			assertEquals(1, dataDto.getLat());
			assertEquals(1, dataDto.getAltitude());
			assertEquals(CLIENT_IP, dataDto.getClientIpAddress());
			assertNotNull(dataDto.getTimeAdded());
			assertNotNull(dataDto.getSensorDto());
			assertEquals(POSITION_SENSOR_NAME, dataDto.getSensorDto().getName());
			assertEquals(SensorType.POSITION, dataDto.getSensorDto().getSensorType());
			assertTrue(dataDto.getSensorDto().isConfigured());
		});
	}

	@Test
	void shouldConvertDataDtoToPositionDataDocument() {
		var positionDataDocument = positionDataConverter.dataDtoToPositionDataDocument(dataDto);

		assertNotNull(positionDataDocument);
		assertAll(() -> {
			assertEquals(1, positionDataDocument.getLat());
			assertEquals(1, positionDataDocument.getLon());
			assertEquals(1, positionDataDocument.getAltitude());
			assertEquals(CLIENT_IP, positionDataDocument.getClientIpAddress());
			assertNotNull(positionDataDocument.getTimeAdded());
			assertNotNull(positionDataDocument.getSensor());
			assertNotNull(positionDataDocument.getSensor().getId());
			assertEquals(POSITION_SENSOR_NAME, positionDataDocument.getSensor().getName());
			assertEquals(SensorType.POSITION, positionDataDocument.getSensor().getSensorType());
			assertTrue(positionDataDocument.getSensor().isConfigured());
		});
	}
}