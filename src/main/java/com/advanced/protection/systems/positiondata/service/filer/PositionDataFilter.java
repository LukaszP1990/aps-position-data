package com.advanced.protection.systems.positiondata.service.filer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.advanced.protection.systems.multisensor.modelservice.constant.SensorType;
import com.advanced.protection.systems.multisensor.modelservice.dto.DataDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PositionDataFilter {

    public static boolean isSensorByType(DataDto dataDto,
                                         SensorType type) {
        var sensorType = dataDto.getSensorDto().getSensorType();
        return Objects.nonNull(sensorType) && type.equals(sensorType);
    }

    public static boolean arePositionDataFieldsNotNull(DataDto dataDto) {
        var positionDataFields = Stream.of(
                dataDto.getTargetId(),
                dataDto.getRssi(),
                dataDto.getAltitude(),
                dataDto.getMiddleFrequency(),
                dataDto.getTimeAdded(),
                dataDto.getSensorDto()
        ).collect(Collectors.toList());
        return validateFields(positionDataFields);
    }

    private static <T> boolean validateFields(List<T> dataDto) {
        return Optional.ofNullable(dataDto)
                .map(field -> isField(dataDto))
                .orElse(false);
    }

    private static <T> boolean isField(List<T> dataDto) {
        return IntStream.rangeClosed(0, dataDto.size() - 1)
                .allMatch(value -> Objects.nonNull(dataDto.get(value)));
    }

}
