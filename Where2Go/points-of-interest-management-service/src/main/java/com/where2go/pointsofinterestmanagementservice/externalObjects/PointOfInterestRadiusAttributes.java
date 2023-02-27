package com.where2go.pointsofinterestmanagementservice.externalObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointOfInterestRadiusAttributes {

    private String xid;
    private double distance;
    private int radius;

}
