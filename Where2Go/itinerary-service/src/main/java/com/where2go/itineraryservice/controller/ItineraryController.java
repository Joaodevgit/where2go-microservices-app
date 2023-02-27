package com.where2go.itineraryservice.controller;

import com.where2go.itineraryservice.dto.Itinerary;
import com.where2go.itineraryservice.externalObject.ItineraryGenParams;
import com.where2go.itineraryservice.service.ItineraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itineraries")
public class ItineraryController {

    @Autowired
    private ItineraryService itineraryService;

    @Operation(summary = "Get points of interest of a certain itinerary given user id and itinerary id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the points of interest of the specified itinerary",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Itinerary.class)))
                    }),
            @ApiResponse(responseCode = "404", description = "User id and itinerary id not found",
                    content = @Content)})
    @GetMapping("/{userId}/{itineraryId}")
    public ResponseEntity<List<Itinerary>> getUserItineraryDetails(@PathVariable int userId, @PathVariable int itineraryId) {
        return itineraryService.getUserItinerary(userId, itineraryId);
    }

    @Operation(summary = "Get all user itineraries given the user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found all user itineraries",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ItineraryGenParams.class)))
                    }),
            @ApiResponse(responseCode = "404", description = "User id not found | User id dont have itineraries associated",
                    content = @Content)})
    @GetMapping("/{userId}")
    public ResponseEntity<List<List<Itinerary>>> getUserItineraries(@PathVariable int userId) {
        return itineraryService.getUserItineraries(userId);
    }

    @Operation(summary = "Generate an itinerary given chosen points of interest by the user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    examples = {
                            @ExampleObject(name = "List of chosen points of interest Example Json",
                                    value = "[{\"xid\":\"string\",\"name\":\"string\",\"latitude\":0,\"longitude\":0}]\n",
                                    description = "List of user's chosen points of interest")})))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Itinerary successfully generated containing a list of the chosen points of interest " +
                            "based on shortest distance between them",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Itinerary.class)))
                    }),
            @ApiResponse(responseCode = "400", description = "List of chosen points of interest is empty | Size of list of " +
                    "chosen points of interest is less than 3",
                    content = @Content)})
    @PostMapping("/itinerary")
    public ResponseEntity<List<Itinerary>> generateItinerary(@RequestBody List<ItineraryGenParams> itineraryGenParams) {
        return itineraryService.generateItinerary(itineraryGenParams);
    }

    @Operation(summary = "Save itinerary given a list of points of a certain itinerary",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    examples = {
                            @ExampleObject(name = "List of itinerary's points of interest Example Json",
                                    value = "[{\"itinerary_id\":0,\"user_id\":0,\"xid\":\"string\",\"pointOfInterestName\":\"string\",\"distance\":0,\"latitude\":0,\"longitude\":0,\"routeOrder\":0,\"itineraryDate\":\"string\"}]\n",
                                    description = "List of itinerary's points of interest")})))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Itinerary successfully saved",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Itinerary.class)))
                    }),
            @ApiResponse(responseCode = "400", description = "If there are no points of interest on itinerary of the " +
                    "response body",
                    content = @Content)})
    @PostMapping("/itinerary/save")
    public ResponseEntity<List<Itinerary>> saveItinerary(@RequestBody List<Itinerary> itinerary) {
        return itineraryService.saveItinerary(itinerary);
    }

    @Operation(summary = "Delete an itinerary given an itinerary id and a user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Itinerary successfully removed"),
            @ApiResponse(responseCode = "404", description = "Itinerary searched does not exists",
                    content = @Content)})
    @DeleteMapping("/itinerary/{userId}/{itineraryId}")
    public ResponseEntity<List<Itinerary>> deleteItinerary(@PathVariable int userId, @PathVariable int itineraryId) {
        return itineraryService.deleteItinerary(userId, itineraryId);
    }

    @Operation(summary = "Delete a certain point of interest of a certain itinerary given an itinerary id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Point of interest of the specified itinerary successfully removed"),
            @ApiResponse(responseCode = "404", description = "Point of interest of the specified itinerary does not exists there",
                    content = @Content)})
    @DeleteMapping("/itinerary/{userId}/{itineraryId}/{xid}")
    public ResponseEntity<Itinerary> deletePointsOfInterest(@PathVariable int userId, @PathVariable int itineraryId,
                                                            @PathVariable String xid) {
        return itineraryService.deletePointsOfInterestOfItinerary(userId, itineraryId, xid);
    }


}
