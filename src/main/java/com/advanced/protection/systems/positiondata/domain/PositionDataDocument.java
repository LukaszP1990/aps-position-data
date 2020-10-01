package com.advanced.protection.systems.positiondata.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.advanced.protection.systems.multisensor.modelservice.domain.Sensor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "positiondata")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class PositionDataDocument {

    @Id
    private String targetId;
    private double lat;
    private double lon;
    private Double altitude;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date timeAdded;
    private Sensor sensor;
    private String clientIpAddress;
}
