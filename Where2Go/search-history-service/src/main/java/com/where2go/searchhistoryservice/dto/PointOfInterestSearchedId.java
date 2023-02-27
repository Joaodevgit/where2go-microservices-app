package com.where2go.searchhistoryservice.dto;

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
public class PointOfInterestSearchedId implements Serializable {

    // Auto increment id
    @Column(name="SEARCH_ID",nullable=false)
    private int search_id;
    @Column(name="USER_ID",nullable=false)
    private int user_id;
    @Column(name="XID",nullable=false)
    private String xid;

}
