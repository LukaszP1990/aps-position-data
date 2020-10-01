package com.advanced.protection.systems.positiondata.service.error;

import com.advanced.protection.systems.multisensor.modelservice.constant.ErrorType;
import com.advanced.protection.systems.multisensor.modelservice.dto.ErrorDto;
import com.advanced.protection.systems.multisensor.modelservice.param.ErrorParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

public interface ErrorServiceFacade {

    Flux<ErrorDto> getErrors(ErrorParam errorParam);

    Mono<ErrorDto> saveErrorDto(ErrorType errorType);
}
