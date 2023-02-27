package com.where2go.searchhistoryservice.controller;

import com.where2go.searchhistoryservice.dto.PointOfInterestSearched;
import com.where2go.searchhistoryservice.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/searchHistory")
public class SearchHistoryController {

    @Autowired
    SearchHistoryService searchHistoryService;

    /**
     * GET method that is responsible for returning the last 5 searches made by the user given the user id
     *
     * @param user_id user id
     * @return a list of {@link PointOfInterestSearched} that corresponds to the last 5 searches made by the user
     */
    @Operation(summary = "Get the last 5 searches made by the user given the user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Returned last 5 searches made by the user)",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PointOfInterestSearched.class)))
                    }),
            @ApiResponse(responseCode = "404", description = "There are no searches made by the user",
                    content = @Content)})
    @GetMapping("/searches/{user_id}")
    public ResponseEntity<List<PointOfInterestSearched>> getPointOfInterest(@PathVariable int user_id) {
        return searchHistoryService.getUserLastFiveSearches(user_id);
    }

}
