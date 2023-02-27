package com.where2go.placeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class PlaceId implements Serializable {

    @Column(name = "LATITUDE", nullable = false, precision = 10, scale = 2)
    private double latitude;
    @Column(name = "LONGITUDE", nullable = false, precision = 10, scale = 2)
    private double longitude;

}
