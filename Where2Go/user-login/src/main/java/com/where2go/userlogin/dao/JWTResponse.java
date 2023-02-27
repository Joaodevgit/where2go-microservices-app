package com.where2go.userlogin.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JWTResponse {
    private Boolean auth;
    private String token;
}
