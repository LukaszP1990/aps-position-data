package com.advanced.protection.systems.positiondata.service.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.advanced.protection.systems.multisensor.modelservice.constant.DataType;
import com.advanced.protection.systems.multisensor.modelservice.constant.ErrorType;
import com.advanced.protection.systems.multisensor.modelservice.constant.SensorType;
import com.advanced.protection.systems.multisensor.modelservice.dto.DataDto;
import com.advanced.protection.systems.multisensor.modelservice.param.DataParam;
import com.advanced.protection.systems.positiondata.core.converter.PositionDataConverter;
import com.advanced.protection.systems.positiondata.core.util.KafkaMapperUtil;
import com.advanced.protection.systems.positiondata.dao.PositionDataRepository;
import com.advanced.protection.systems.positiondata.domain.PositionDataDocument;
import com.advanced.protection.systems.positiondata.feignclient.FeignRfClient;
import com.advanced.protection.systems.positiondata.service.data.exception.PositionDataException;
import com.advanced.protection.systems.positiondata.service.error.ErrorServiceFacade;
import com.advanced.protection.systems.positiondata.service.filer.PositionDataFilter;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

@Slf4j
@Component
class PositionDataService {

	private final PositionDataRepository positionDataRepository;
	private final PositionDataConverter positionDataConverter;
	private final KafkaReceiver<String, String> kafkaReceiver;
	private final FeignRfClient feignRfClient;
	private final ErrorServiceFacade errorServiceFacade;

	PositionDataService(final PositionDataRepository positionDataRepository,
						final PositionDataConverter positionDataConverter,
						final KafkaReceiver<String, String> kafkaReceiver,
						final FeignRfClient feignRfClient,
						final ErrorServiceFacade errorServiceFacade) {
		this.positionDataRepository = positionDataRepository;
		this.positionDataConverter = positionDataConverter;
		this.kafkaReceiver = kafkaReceiver;
		this.feignRfClient = feignRfClient;
		this.errorServiceFacade = errorServiceFacade;
	}

	Flux<DataDto> getPositionData(DataParam dataParam) {
		log.info("get positionDatas by dataParam: {} ", dataParam);
		return Try.of(() -> dataParam)
				.map(localDateTimes ->
						positionDataRepository.findByDataParam(
								dataParam.getLon(), dataParam.getLat(), dataParam.getTimeAddedFrom(), dataParam.getTimeAddedTo(), dataParam.getSensorName()
						)
				)
				.map(positionDataDocumentFlux -> positionDataDocumentFlux.collectList()
						.filter(positionDataDocuments -> !positionDataDocuments.isEmpty())
						.map(this::getPositionDataDtos)
						.flatMapMany(Flux::fromIterable))
				.getOrNull();
	}

	Flux<DataDto> consumePositionData() {
		CountDownLatch latch = new CountDownLatch(20);
		return kafkaReceiver.receive()
				.checkpoint("RfData being consumed")
				.flatMap(receiverRecord -> savePositionData(latch, receiverRecord));
	}

	private Flux<DataDto> savePositionData(CountDownLatch latch,
										   ReceiverRecord<String, String> receiverRecord) {
		var offset = receiverRecord.receiverOffset();
		var dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
		log.info("Received message: topic-partition: {},  offset: {}, timestamp: {}, receivedKey; {} and receivedValue: {}",
				offset.topicPartition(),
				offset.offset(),
				dateFormat.format(new Date(receiverRecord.timestamp())),
				receiverRecord.key(),
				receiverRecord.value());
		offset.acknowledge();
		latch.countDown();
		return saveBySensorName(KafkaMapperUtil.fromBinary(receiverRecord.value(), DataDto.class))
				.flatMapMany(Flux::just);
	}

	private Mono<DataDto> saveBySensorName(DataDto dataDto) {
		log.info("save positionData by sensor unique name: {}", dataDto.getSensorDto().getName());
		var sensorName = dataDto.getSensorDto().getName();
		return Mono.just(dataDto)
				.filter(Objects::nonNull)
				.map(rfData -> getPositionDataBySensorName(sensorName))
				.flatMap(rfDataDocumentMono -> rfDataDocumentMono
						.filter(Objects::isNull)
						.flatMap(data -> feignRfClient.downloadBySensorName(sensorName))
						.filter(Objects::isNull)
						.flatMap(rfData -> saveBySensorType(dataDto))
						.switchIfEmpty(Mono.defer(() -> getError(ErrorType.SENSOR_UNIQUE_NAME_ERROR))));
	}

	private Mono<DataDto> saveBySensorType(DataDto dataDto) {
		log.info("save rfData by sensor type: {}", dataDto.getSensorDto().getSensorType());
		return Mono.just(dataDto)
				.filter(Objects::nonNull)
				.filter(rfData -> PositionDataFilter.isSensorByType(dataDto, SensorType.RF))
				.map(positionDataConverter::dataDtoToPositionDataDocument)
				.flatMap(positionDataRepository::save)
				.map(positionDataConverter::positionDataDocumentToDataDto)
				.map(this::setDataType)
				.switchIfEmpty(Mono.defer(() -> getError(ErrorType.SENSOR_TYPE_RF_DATA_ERROR)));
	}

	private Mono<DataDto> getError(ErrorType sensorTypeRfDataError) {
		errorServiceFacade.saveErrorDto(sensorTypeRfDataError)
				.subscribe();
		return Mono.error(new PositionDataException(sensorTypeRfDataError));
	}

	Mono<DataDto> getPositionDataBySensorName(String name) {
		log.info("get positionDataDocument by sensor name: {}", name);
		return positionDataRepository.findBySensorName(name)
				.filter(Objects::nonNull)
				.map(positionDataConverter::positionDataDocumentToDataDto);
	}

	private DataDto setDataType(DataDto dataDto) {
		dataDto.setDataType(DataType.POSITION);
		return dataDto;
	}

	private List<DataDto> getPositionDataDtos(List<PositionDataDocument> positionDataDocuments) {
		log.info("convert positionDataDocuments: {} to positionDataDto", positionDataDocuments);
		return positionDataDocuments.stream()
				.map(positionDataConverter::positionDataDocumentToDataDto)
				.collect(Collectors.toList());
	}

}
