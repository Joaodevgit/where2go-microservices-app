package com.where2go.favoritepointofinterest.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Favorite {

    @JsonUnwrapped
    @EmbeddedId
    private FavoriteId favoriteId;
    @Column(nullable=false)
    private String favoriteName;
    @Column(nullable=false)
    private String url;
}
