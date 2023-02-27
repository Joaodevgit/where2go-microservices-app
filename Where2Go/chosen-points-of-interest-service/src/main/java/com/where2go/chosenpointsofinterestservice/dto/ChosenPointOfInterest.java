package com.where2go.chosenpointsofinterestservice.dto;


import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChosenPointOfInterest implements Serializable {

    @JsonUnwrapped
    @EmbeddedId
    private ChosenPointOfInterestId chosenPointOfInterestId;
    @Column(name = "NAME", nullable=false)
    private String name;
}
