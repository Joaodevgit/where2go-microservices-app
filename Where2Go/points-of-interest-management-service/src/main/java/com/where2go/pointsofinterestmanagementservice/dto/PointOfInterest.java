package com.where2go.pointsofinterestmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointOfInterest {

    @Id
    @Column(name = "XID", nullable = false)
    private String xid;
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "DESCRIPTION", length = 1500, nullable = false)
    private String decription;
    @Column(name = "RATE", nullable = false)
    private double rate;
    @Column(name = "IMAGE", length = 500, nullable = false)
    private String image;
    @Column(name = "STARS", nullable = true)
    private Integer stars;
    @Column(name = "BOOKING_URL", length = 500, nullable = true)
    private String bookingUrl;
    @Column(name = "KINDS", length = 500, nullable = false)
    private String kinds;
    @Column(name = "LATITUDE", nullable = false, precision = 10, scale = 2)
    private double latitude;
    @Column(name = "LONGITUDE", nullable = false, precision = 10, scale = 2)
    private double longitude;
}
