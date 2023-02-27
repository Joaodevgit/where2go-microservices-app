package com.where2go.chosenpointsofinterestservice.controller;

import com.where2go.chosenpointsofinterestservice.dto.ChosenPointOfInterest;
import com.where2go.chosenpointsofinterestservice.service.ChosenPointsOfInterestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chosenPointsOfInterest")
@AllArgsConstructor
public class ChosenPointOfInterestController {

    @Autowired
    private ChosenPointsOfInterestService chosenPointsOfInterestService;

    @Operation(summary = "Get all chosen points of interest of a certain user given user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of chosen points of interest of the specified user successfully returned",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ChosenPointOfInterest.class)))
                    }),
            @ApiResponse(responseCode = "404", description = "User doesn't have chosen points of interest",
                    content = @Content)})
    @GetMapping("/{userId}")
    public ResponseEntity<List<ChosenPointOfInterest>> getUserChosenPointsOfInterest(@PathVariable int userId) {
        return chosenPointsOfInterestService.getAllUserChosenPointsInterest(userId);
    }

    @Operation(summary = "Save user chosen point of interest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "User chosen point of interest successfully saved"),
            @ApiResponse(responseCode = "409", description = "User chosen point of interest already exists",
                    content = @Content)})
    @PostMapping("/save")
    public ResponseEntity<ChosenPointOfInterest> getUserChosenPointsOfInterest(@RequestBody ChosenPointOfInterest chosenPointOfInterest) {
        return chosenPointsOfInterestService.saveUserChosenPointsInterest(chosenPointOfInterest);
    }

    @Operation(summary = "Delete a certain chosen point of interest of a certain user given user id and xid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "User chosen point of interest successfully removed"),
            @ApiResponse(responseCode = "409", description = "User chosen point of interest does not exists",
                    content = @Content)})
    @DeleteMapping("/{userId}/{xid}")
    public ResponseEntity<ChosenPointOfInterest> deleteUserChosenPointsOfInterest(@PathVariable int userId, @PathVariable String xid) {
        return chosenPointsOfInterestService.deleteUserChosenPointsInterest(userId, xid);
    }

}

