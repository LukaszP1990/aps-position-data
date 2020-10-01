package com.advanced.protection.systems.positiondata.core.converter;

import static com.advanced.protection.systems.multisensor.modelservice.constant.ErrorType.DATA_SAVE_ERROR;
import static com.advanced.protection.systems.positiondata.core.util.ErrorUtil.createError;
import static com.advanced.protection.systems.positiondata.core.util.ErrorUtil.createErrorDto;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.advanced.protection.systems.multisensor.modelservice.dto.ErrorDto;
import com.advanced.protection.systems.positiondata.domain.ErrorDocument;

class ErrorConverterTest {

	private static final ErrorDto errorDto = createErrorDto();
	private static final ErrorDocument error = createError(1);
	private ErrorConverter errorConverter = Mappers.getMapper(ErrorConverter.class);

	@Test
	void shouldConvertErrorDtoToErrorDocument() {
		ErrorDocument errorDocument = errorConverter.errorDtoToErrorDocument(errorDto);

		assertNotNull(errorDocument);
		assertAll(() -> {
			assertNotNull(errorDocument.getTimeAdded());
			assertEquals(DATA_SAVE_ERROR.getText(), errorDocument.getText());
			assertNull(errorDocument.getId());
		});
	}

	@Test
	void shouldConvertErrorDocumentToErrorDto() {
		ErrorDto errorDto = errorConverter.errorDocumentToErrorDto(error);

		assertNotNull(errorDto);
		assertAll(() -> {
			assertNotNull(errorDto.getTimeAdded());
			assertEquals(DATA_SAVE_ERROR.getText(), errorDto.getText());
		});

	}
}