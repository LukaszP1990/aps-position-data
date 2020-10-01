package com.advanced.protection.systems.positiondata.core.converter;

import com.advanced.protection.systems.multisensor.modelservice.dto.ErrorDto;
import com.advanced.protection.systems.positiondata.domain.ErrorDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ErrorConverter {

    @Mapping(target = "id", ignore = true)
    ErrorDocument errorDtoToErrorDocument(ErrorDto errorDto);

    ErrorDto errorDocumentToErrorDto(ErrorDocument errorDocument);
}
