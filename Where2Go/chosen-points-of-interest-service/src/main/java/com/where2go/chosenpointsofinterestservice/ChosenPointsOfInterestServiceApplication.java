package com.where2go.chosenpointsofinterestservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Chosen Points of Interest Service API", version = "1.0",
        description = "Chosen Points of Interest Management Information"))
public class ChosenPointsOfInterestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChosenPointsOfInterestServiceApplication.class, args);
    }

}
