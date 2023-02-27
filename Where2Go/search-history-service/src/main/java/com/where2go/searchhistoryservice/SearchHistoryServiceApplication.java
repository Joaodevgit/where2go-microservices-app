package com.where2go.searchhistoryservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Search History Service API", version = "1.0", description = "Search History Information"))
public class SearchHistoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchHistoryServiceApplication.class, args);
    }

}
