package com.where2go.chosenpointsofinterestservice.dto;

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
public class ChosenPointOfInterestId implements Serializable {

    @Column(name="USER_ID",nullable=false)
    private int user_id;
    @Column(name="XID",nullable=false)
    private String xid;

}
