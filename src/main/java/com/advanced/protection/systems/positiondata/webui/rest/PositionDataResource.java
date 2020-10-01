package com.advanced.protection.systems.positiondata.webui.rest;

import com.advanced.protection.systems.multisensor.modelservice.dto.DataDto;
import com.advanced.protection.systems.multisensor.modelservice.param.DataParam;
import com.advanced.protection.systems.positiondata.service.data.PositionDataServiceFacade;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

@Slf4j
@RestController
@RequestMapping("/api/position-data")
public class PositionDataResource {

    private final PositionDataServiceFacade positionDataServiceFacade;

    public PositionDataResource(final PositionDataServiceFacade positionDataServiceFacade) {
        this.positionDataServiceFacade = positionDataServiceFacade;
    }

    @GetMapping(value = "/consume", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<DataDto>consumePositionData(){
        return positionDataServiceFacade.consumePositionData();
    }

    @GetMapping
    public Flux<DataDto> download(DataParam dataParam) {
        log.info("download by dataParam: {}", dataParam);
        return positionDataServiceFacade.getPositionData(dataParam);
    }

    @GetMapping(path = "/sensor-name/{sensor-name}")
    public Mono<DataDto> downloadBySensorName(@RequestParam(name = "sensor-name") String sensorName) {
        log.info("downloadBySensorName - sensor-name: {}", sensorName);
        return positionDataServiceFacade.getPositionDataBySensorName(sensorName);
    }
}
