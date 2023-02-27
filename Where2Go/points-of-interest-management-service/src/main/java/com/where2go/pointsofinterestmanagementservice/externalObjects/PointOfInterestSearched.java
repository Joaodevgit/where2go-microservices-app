package com.where2go.pointsofinterestmanagementservice.externalObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointOfInterestSearched {

    private String xid;
    private int user_id;
    private String name;
    private String description;
    private String kinds;
    private double distance;
    private int radius;
    private double rate;
    private String image;
    private double latitude;
    private double longitude;
    private String searchDate;

}
