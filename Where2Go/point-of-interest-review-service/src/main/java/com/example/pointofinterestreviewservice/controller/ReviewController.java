package com.example.pointofinterestreviewservice.controller;

import com.example.pointofinterestreviewservice.dto.Review;
import com.example.pointofinterestreviewservice.service.ReviewService;
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
@RequestMapping("/review")
@AllArgsConstructor
@Slf4j
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Operation(summary = "Get a certain point of interest review given its userId and xid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the review of the point of interest specified",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Review.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Specified point of interest not found",
                    content = @Content)})
    @GetMapping("/reviews/{userId}/{xid}")
    public ResponseEntity<Review> getUserReview(@PathVariable int userId, @PathVariable String xid) {
        return reviewService.getUserReviewDetail(xid, userId);
    }

    @Operation(summary = "Get all user points of interest reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Returned all user points of interest reviews (ordered by recent review date)",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Review.class)))
                    }),
            @ApiResponse(responseCode = "404", description = "User doesn't have reviews written",
                    content = @Content)})
    @GetMapping("/reviews/users/{userId}")
    public ResponseEntity<List<Review>> getUserReviews(@PathVariable int userId) {
        return reviewService.getUserReviews(userId);
    }

    @Operation(summary = "Get all points of interest reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Returned all points of interest reviews (ordered by recent date)",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Review.class)))
                    }),
            @ApiResponse(responseCode = "404", description = "Point of interest doesn't have reviews written",
                    content = @Content)})
    @GetMapping("/reviews/pointsOfInterest/{xid}")
    public ResponseEntity<List<Review>> getPointOfInterestReviews(@PathVariable String xid) {
        return reviewService.getPointOfInterestReviews(xid);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Review added added successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Review.class))
                    }),
            @ApiResponse(responseCode = "404", description = "User review for that point of interest was already written",
                    content = @Content)})
    @PostMapping("/reviews")
    public ResponseEntity<Review> addReview(@RequestBody Review review) {
        return reviewService.addUserReview(review);
    }

    @Operation(summary = "Get rate of a point of interest based on users rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Return 0.0 if the point of interest doesn't have any review | Return calculated rate" +
                            " if the point of interest has at least 1 review received",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Double.class))
                    })})
    @GetMapping("/reviews/rate/{xid}")
    public ResponseEntity<Double> getPointOfInterestCalculatedRate(@PathVariable String xid) {
        return reviewService.calculatePointOfInterestRate(xid);
    }
}

