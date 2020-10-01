package com.advanced.protection.systems.positiondata.service.error;

import com.advanced.protection.systems.multisensor.modelservice.constant.ErrorType;
import com.advanced.protection.systems.multisensor.modelservice.dto.ErrorDto;
import com.advanced.protection.systems.multisensor.modelservice.param.ErrorParam;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Component
public class ErrorServiceFacadeImpl implements ErrorServiceFacade{

    private final ErrorService errorService;

    public ErrorServiceFacadeImpl(ErrorService errorService) {
        this.errorService = errorService;
    }

    @Override
    public Flux<ErrorDto> getErrors(ErrorParam errorParam) {
        return errorService.getErrors(errorParam);
    }

    @Override
    public Mono<ErrorDto> saveErrorDto(ErrorType errorType) {
        return errorService.saveErrorDto(errorType);
    }
}
