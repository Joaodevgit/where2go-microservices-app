package com.where2go.favoritepointofinterest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "FavoriteAPI", version = "1.0", description = "Favorite Information"))
public class FavoritePointOfInterestApplication {

    public static void main(String[] args) {
        SpringApplication.run(FavoritePointOfInterestApplication.class, args);
    }

}
