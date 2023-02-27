package com.where2go.searchhistoryservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointOfInterestSearched {

    @JsonUnwrapped
    @EmbeddedId
    private PointOfInterestSearchedId pointOfInterestSearchedId;
    @Column(name = "POINTOFINTEREST_NAME", nullable = false)
    private String name;
    @Column(name = "DESCRIPTION", length = 1500, nullable = false)
    private String description;
    @Column(name = "DISTANCE", nullable = false)
    private double distance;
    @Column(name = "RADIUS", nullable = false)
    private int radius;
    @Column(name = "RATE", nullable = false)
    private double rate;
    @Column(name = "IMAGE", length = 500, nullable = false)
    private String image;
    @Column(name = "KINDS", nullable = false)
    private String kinds;
    @Column(name = "LATITUDE", nullable = false)
    private double latitude;
    @Column(name = "LONGITUDE", nullable = false)
    private double longitude;
    @Column(name = "DATE", nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private String searchDate;

}
