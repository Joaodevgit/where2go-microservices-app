package com.where2go.itineraryservice.externalObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointOfInterestSearched {

    private int search_id;
    private int user_id;
    private String xid;
    private String pointOfInterestName;
    private String description;
    private double distance;
    private int radius;
    private double rate;
    private String image;
    private double latitude;
    private double longitude;
    private String chosenInitialPoint;

}
