package com.where2go.itineraryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Builder
public class ItineraryId implements Serializable {

    @Column(name="ITINERARY_ID",nullable=false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itinerary_id;
    @Column(name="USER_ID",nullable=false)
    private int user_id;
    @Column(name="XID",nullable=false)
    private String xid;
}
