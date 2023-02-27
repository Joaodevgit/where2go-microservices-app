package com.where2go.itineraryservice.externalObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItineraryGenParams {

    private String xid;
    private String name;
    private int user_id;
    private double distance;
    private double latitude;
    private double longitude;
}
