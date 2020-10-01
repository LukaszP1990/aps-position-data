package com.advanced.protection.systems.positiondata.service.data;

import com.advanced.protection.systems.multisensor.modelservice.dto.DataDto;
import com.advanced.protection.systems.multisensor.modelservice.param.DataParam;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

@Component
public class PositionDataServiceFacadeImpl implements PositionDataServiceFacade{

    private final PositionDataService positionDataService;

    public PositionDataServiceFacadeImpl(PositionDataService positionDataService) {
        this.positionDataService = positionDataService;
    }

    @Override
    public Flux<DataDto> getPositionData(DataParam dataParam) {
        return positionDataService.getPositionData(dataParam);
    }

    @Override
    public Flux<DataDto> consumePositionData() {
        return positionDataService.consumePositionData();
    }

    @Override
    public Mono<DataDto> getPositionDataBySensorName(String name) {
        return positionDataService.getPositionDataBySensorName(name);
    }
}
