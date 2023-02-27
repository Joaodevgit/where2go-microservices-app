package com.example.pointofinterestreviewservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review implements Serializable {

    @EmbeddedId
    @JsonUnwrapped
    private ReviewId reviewId;
    @Column(name = "RATE", nullable = false)
    private double rate;
    @Column(name = "COMMENTARY", length = 1000, nullable = false)
    private String commentary;
    @Column(name = "Date", nullable = false)
    private LocalDateTime reviewDate;
}
