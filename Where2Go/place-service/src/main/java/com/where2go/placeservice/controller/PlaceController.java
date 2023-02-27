package com.where2go.placeservice.controller;

import com.where2go.placeservice.dto.Place;
import com.where2go.placeservice.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/places")
@AllArgsConstructor
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @Operation(summary = "Get a certain place given its search result name (in format \"City,Country\")")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the place specified",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Place.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Specified place not found",
                    content = @Content)})
    @GetMapping("/place/{placeSearched}")
    public ResponseEntity<Place> getPlace(@PathVariable String placeSearched) {
        return placeService.getPlaceBySearchName(placeSearched);
    }

    @Operation(summary = "Get a certain place given user latitude and longitude")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the place",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Place.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Specified place not found",
                    content = @Content)})
    @GetMapping("/userPlace/{userLat}/{userLon}")
    public ResponseEntity<Place> getUsernamePlace(@PathVariable double userLat, @PathVariable double userLon) {
        return placeService.getPlaceByUserLatAndUserLon(userLat, userLon);
    }

}

