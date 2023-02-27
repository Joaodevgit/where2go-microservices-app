package com.where2go.pointsofinterestmanagementservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Points of Interest Management Service API", version = "1.0",
        description = "Points of Interest Management Information"))
public class PointsOfInterestManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PointsOfInterestManagementServiceApplication.class, args);
    }

}
