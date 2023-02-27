package com.where2go.pointsofinterestmanagementservice.controller;

import com.where2go.pointsofinterestmanagementservice.dto.PointOfInterest;
import com.where2go.pointsofinterestmanagementservice.externalObjects.PointOfInterestSearched;
import com.where2go.pointsofinterestmanagementservice.service.PointOfInterestManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pointsOfInterest")
@AllArgsConstructor
@Slf4j
public class PointOfInterestManagementController {

    @Autowired
    private PointOfInterestManagementService pointOfInterestManagementService;

    @Operation(summary = "Get a certain point of interest given its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the point of interest specified",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PointOfInterest.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Specified point of interest not found",
                    content = @Content)})
    @GetMapping("/{xid}")
    public ResponseEntity<PointOfInterest> getPointOfInterestDetails(@PathVariable String xid) {
        return pointOfInterestManagementService.getPointOfInterestDetails(xid);
    }

    @Operation(summary = "Get all points of interest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Returned all points of interest",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PointOfInterest.class)))
                    }),
            @ApiResponse(responseCode = "404", description = "There are no points of interest",
                    content = @Content)})
    @GetMapping("/")
    public ResponseEntity<List<PointOfInterest>> getPointOfInterest() {
        return pointOfInterestManagementService.getAllPointsOfInterest();
    }

    @Operation(summary = "Get all points of interest with the given search parameters (latitude, longitude, radius, kinds" +
            " and nameSearched)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Returned a list of points of interest with the given parameters (latitude, longitude, radius" +
                            " kinds (values separated by comma) and name searched)",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PointOfInterest.class)))
                    }),
            @ApiResponse(responseCode = "404", description = "There are no points of interest with the given parameters" +
                    "(latitude, longitude, radius and limit)",
                    content = @Content)})
    @GetMapping("/searchPointsInterest/radius={radius}_lon={userLon}_lat={userLat}_kinds={kinds}_search={nameSearched}_{userId}")
    public ResponseEntity<List<PointOfInterestSearched>> getPointOfInterest(@PathVariable double userLat, @PathVariable double userLon,
                                                                            @PathVariable int radius, @PathVariable String kinds,
                                                                            @PathVariable String nameSearched, @PathVariable int userId) {
        return pointOfInterestManagementService.getPointsOfInterestsByRadius(userLat, userLon, radius, kinds, nameSearched, userId);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of points of interest successfully returned",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PointOfInterest.class)))
                    }),
            @ApiResponse(responseCode = "400", description = "List of points of interest xid is empty",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "There are no points of interest",
                    content = @Content)})
    @PostMapping("/listXid")
    public ResponseEntity<List<PointOfInterest>> getPointOfInterestByListOfXids(@RequestBody List<String> xids) {
        return pointOfInterestManagementService.getPointOfInterestDetailsByListXid(xids);
    }
}

