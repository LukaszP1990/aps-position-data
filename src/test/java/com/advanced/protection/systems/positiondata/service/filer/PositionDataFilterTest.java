package com.advanced.protection.systems.positiondata.service.filer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.advanced.protection.systems.multisensor.modelservice.constant.SensorType;
import com.advanced.protection.systems.multisensor.modelservice.dto.DataDto;
import com.advanced.protection.systems.positiondata.core.util.PositionDataUtil;

class PositionDataFilterTest {

	private static final DataDto dataDto = PositionDataUtil.createDataDto();

	@Test
	void shouldReturnTrueWhenSensorTypeIsSetToRf() {
		assertTrue(PositionDataFilter.isSensorByType(dataDto, SensorType.POSITION));
	}

	@Test
	void shouldReturnFalseWhenSensorTypeIsSetToPosition() {
		assertFalse(PositionDataFilter.isSensorByType(dataDto, SensorType.RF));
	}

	@Test
	void shouldReturnTrueWhenAllFieldsAreNotNull() {
		assertTrue(PositionDataFilter.arePositionDataFieldsNotNull(dataDto));
	}

	@Test
	void shouldReturnFalseWhenAnyFieldIsNull() {
		assertFalse(PositionDataFilter.arePositionDataFieldsNotNull(
				DataDto.builder().build())
		);
	}
}