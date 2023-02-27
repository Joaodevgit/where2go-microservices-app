package com.where2go.itineraryservice.externalObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointOfInterest implements Serializable {

    private String xid;
    private String name;
    private String description;
    private double rate;
    private String image;
    private String kinds;
    private double latitude;
    private double longitude;
}
