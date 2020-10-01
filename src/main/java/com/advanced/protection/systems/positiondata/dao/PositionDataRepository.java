package com.advanced.protection.systems.positiondata.dao;

import java.util.Date;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.advanced.protection.systems.positiondata.domain.PositionDataDocument;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PositionDataRepository extends ReactiveMongoRepository<PositionDataDocument, String> {

	@Query("{'sensor.name': ?0}")
	Mono<PositionDataDocument> findBySensorName(String name);

	@Query("{$and : [" +
			"{ 'lon' : ?0 } , " +
			"{ 'lat' : ?1 } , " +
			"{ 'timeAdded' : { $gte: ?2, $lte: ?3 } } , " +
			"{ 'sensor.name' : ?4 } , " +
			"]} ")
	Flux<PositionDataDocument> findByDataParam(double lon,
											   double lat,
											   Date timeAddedFrom,
											   Date timeAddedTo,
											   String name);
}
