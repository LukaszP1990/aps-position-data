package com.advanced.protection.systems.positiondata.dao;

import java.util.Date;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.advanced.protection.systems.positiondata.domain.ErrorDocument;

import reactor.core.publisher.Flux;

@Repository
public interface ErrorRepository extends ReactiveMongoRepository<ErrorDocument, String> {

	Flux<ErrorDocument> findByTextAndTimeAddedBetween(String text,
													  Date timeAddedFrom,
													  Date timeAddedTo);
}
