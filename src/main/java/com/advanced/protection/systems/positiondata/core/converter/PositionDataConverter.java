package com.advanced.protection.systems.positiondata.core.converter;

import java.util.UUID;

import org.mapstruct.*;

import com.advanced.protection.systems.multisensor.modelservice.constant.DataType;
import com.advanced.protection.systems.multisensor.modelservice.constant.SensorType;
import com.advanced.protection.systems.multisensor.modelservice.domain.Sensor;
import com.advanced.protection.systems.multisensor.modelservice.dto.DataDto;
import com.advanced.protection.systems.multisensor.modelservice.dto.SensorDto;
import com.advanced.protection.systems.positiondata.domain.PositionDataDocument;

@Mapper
public interface PositionDataConverter {

    @Mapping(target = "rssi", ignore = true)
    @Mapping(target = "middleFrequency", ignore = true)
    @Mapping(target = "sensorDto", source = "sensor", qualifiedByName = "setSensorDto")
    DataDto positionDataDocumentToDataDto(PositionDataDocument positionDataDocument);

    @Mapping(target = "targetId", qualifiedByName = "convertTargetIdToString")
    @Mapping(target = "sensor", source = "sensorDto", qualifiedByName = "setSensor")
    PositionDataDocument dataDtoToPositionDataDocument(DataDto dataDto);

    @Named("convertTargetIdToString")
    default String convertTargetIdToString(Long targetId) {
        return String.valueOf(targetId);
    }

    @Named("setSensorDto")
    default SensorDto setSensorDto(Sensor sensor) {
        return SensorDto.builder()
                .configured(sensor.isConfigured())
                .name(sensor.getName())
                .sensorType(SensorType.RF).build();
    }

    @Named("setSensor")
    default Sensor setSensor(SensorDto sensorDto) {
        return Sensor.builder()
                .id(Long.valueOf(UUID.randomUUID().toString()))
                .configured(sensorDto.isConfigured())
                .name(sensorDto.getName())
                .sensorType(SensorType.RF)
                .build();
    }

    @AfterMapping
    default void setDataType(@MappingTarget DataDto dataDto) {
        dataDto.setDataType(DataType.POSITION);
    }

}
