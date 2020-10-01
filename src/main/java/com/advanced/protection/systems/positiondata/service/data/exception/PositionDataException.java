package com.advanced.protection.systems.positiondata.service.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.advanced.protection.systems.multisensor.modelservice.constant.ErrorType;

public class PositionDataException extends ResponseStatusException{
	private final static HttpStatus STATUS = HttpStatus.BAD_REQUEST;

	public PositionDataException(ErrorType errorType) {
		super(STATUS, errorType.getText());
	}
}