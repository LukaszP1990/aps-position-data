package com.advanced.protection.systems.positiondata.core.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.advanced.protection.systems.multisensor.modelservice.constant.SensorType;
import com.advanced.protection.systems.multisensor.modelservice.domain.Sensor;
import com.advanced.protection.systems.multisensor.modelservice.dto.DataDto;
import com.advanced.protection.systems.multisensor.modelservice.dto.SensorDto;
import com.advanced.protection.systems.multisensor.modelservice.param.DataParam;
import com.advanced.protection.systems.positiondata.domain.PositionDataDocument;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PositionDataUtil {

	public static final String POSITION_SENSOR_NAME = "position-sensor-name";
	public static final String CLIENT_IP = "192.168.10.10";

	public static List<PositionDataDocument> getPositionDataDocuments() {
		return IntStream.rangeClosed(1, 4)
				.mapToObj(PositionDataUtil::createPositionData)
				.collect(Collectors.toList());
	}

	public static DataDto createDataDto() {
		return DataDto.builder()
				.targetId(1L)
				.lat(1)
				.lon(1)
				.altitude(1d)
				.timeAdded(DateUtil.getRegularDate())
				.sensorDto(createSensorDto())
				.clientIpAddress(CLIENT_IP)
				.build();
	}

	public static PositionDataDocument createPositionData(int value) {
		return PositionDataDocument.builder()
				.targetId(String.valueOf(value))
				.lon(value)
				.lat(value)
				.altitude(1d)
				.timeAdded(DateUtil.getRegularDate())
				.sensor(createSensor(String.valueOf(value)))
				.clientIpAddress(CLIENT_IP)
				.build();
	}

	public static DataParam getDataParam() {
		return DataParam.builder()
				.lon(1)
				.lat(1)
				.timeAddedFrom(DateUtil.getRegularDateFrom())
				.timeAddedTo(DateUtil.getRegularDateTo())
				.rssi(1)
				.sensorName(PositionDataUtil.POSITION_SENSOR_NAME.concat(String.valueOf(1)))
				.build();
	}

	private static SensorDto createSensorDto(){
		return SensorDto.builder()
				.sensorType(SensorType.POSITION)
				.name(POSITION_SENSOR_NAME)
				.configured(true)
				.build();
	}

	private static Sensor createSensor(String name){
		return Sensor.builder()
				.id(1L)
				.sensorType(SensorType.POSITION)
				.name(POSITION_SENSOR_NAME.concat(name))
				.configured(true)
				.build();
	}

}
