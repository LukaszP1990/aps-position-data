package com.advanced.protection.systems.positiondata.feignclient;

import com.advanced.protection.systems.multisensor.modelservice.dto.DataDto;
import com.advanced.protection.systems.positiondata.core.constant.RfDataServiceConstant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = RfDataServiceConstant.RF_DATA_RESOURCE)
public interface FeignRfClient {

    @GetMapping(path = "/rf-data/sensor-name/{sensor-name}")
    Mono<DataDto> downloadBySensorName(@RequestParam(name = "sensor-name") String sensorName);
}
