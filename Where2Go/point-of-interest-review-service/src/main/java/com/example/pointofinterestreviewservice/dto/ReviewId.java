package com.example.pointofinterestreviewservice.dto;

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
public class ReviewId implements Serializable {

    private static final long serialVersionUID = -5849576224242186350L;
    @Column(name = "XID", nullable = false)
    private String xid;
    @Column(name = "USER_ID", nullable = false)
    private int user_id;
}
