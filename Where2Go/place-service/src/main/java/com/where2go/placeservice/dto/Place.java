package com.where2go.placeservice.dto;


import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Place implements Serializable {

    @JsonUnwrapped
    @EmbeddedId
    private PlaceId placeId;
    @Column(name = "NAME", length=50, nullable=false)
    private String name;
    @Column(name="COUNTRY_LETTER", length=10, nullable=false)
    private String countryLetter;
}
