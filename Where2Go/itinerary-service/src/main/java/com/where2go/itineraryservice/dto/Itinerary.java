package com.where2go.itineraryservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Itinerary implements Serializable {

    @JsonUnwrapped
    @EmbeddedId
    private ItineraryId itineraryId;
    @Column(name = "POINTOFINTEREST_NAME")
    private String pointOfInterestName;
    @Column(name = "DISTANCE", nullable = false)
    private double distance; // Distance to the next point
    @Column(name = "LATITUDE", nullable = false)
    private double latitude;
    @Column(name = "LONGITUDE", nullable = false)
    private double longitude;
    @Column(name = "ROUTE_ORDER", nullable = false)
    private int routeOrder;
    @Column(name = "DATE", nullable = false)
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate itineraryDate;

    public Itinerary(String pointOfInterestName, double distance, int routeOrder) {
        this.pointOfInterestName = pointOfInterestName;
        this.distance = distance;
        this.routeOrder = routeOrder;
    }
}
