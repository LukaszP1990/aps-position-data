package com.advanced.protection.systems.positiondata.webui.rest;

import com.advanced.protection.systems.multisensor.modelservice.dto.ErrorDto;
import com.advanced.protection.systems.multisensor.modelservice.param.ErrorParam;
import com.advanced.protection.systems.positiondata.service.error.ErrorServiceFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/position-data")
public class ErrorResource {

    private final ErrorServiceFacade errorServiceFacade;

    public ErrorResource(final ErrorServiceFacade errorServiceFacade) {
        this.errorServiceFacade = errorServiceFacade;
    }

    @GetMapping(path = "/errors")
    public Flux<ErrorDto> download(ErrorParam errorParam) {
        log.info("download positionData errors");
        return errorServiceFacade.getErrors(errorParam);
    }
}
