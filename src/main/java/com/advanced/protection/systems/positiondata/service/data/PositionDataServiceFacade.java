package com.advanced.protection.systems.positiondata.service.data;

import com.advanced.protection.systems.multisensor.modelservice.dto.DataDto;
import com.advanced.protection.systems.multisensor.modelservice.param.DataParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

public interface PositionDataServiceFacade {

    Flux<DataDto> getPositionData(DataParam dataParam);

    Flux<DataDto> consumePositionData();

    Mono<DataDto> getPositionDataBySensorName(String name);
}
