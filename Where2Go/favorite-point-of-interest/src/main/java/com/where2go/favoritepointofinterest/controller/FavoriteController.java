package com.where2go.favoritepointofinterest.controller;

import com.where2go.favoritepointofinterest.dto.Favorite;
import com.where2go.favoritepointofinterest.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/favorite")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Operation(summary = "Get All Favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Get All Favorites",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Favorite.class))
                    })})
    @GetMapping
    public ResponseEntity<List<Favorite>> getFavorites() {
        return this.favoriteService.getFavorites();
    }

    @Operation(summary = "Get specific Favorites By User Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Get specific Favorites By User Id",
                    content = {@Content(mediaType = "application/json")
                    })})
    @GetMapping("user/{userId}")
    public ResponseEntity<List<Favorite>> getFavoriteByUserId(@PathVariable("userId") Long userId) {
        return this.favoriteService.getUserFavorites(userId);
    }

    @Operation(summary = "Get All Favorite Users By Point Of Interest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Get All Favorite Users By Point Of Interest",
                    content = {@Content(mediaType = "application/json")
                    })})
    @GetMapping("point-of-interest/{xid}")
    public ResponseEntity<List<Favorite>> getPointOfInterest(@PathVariable("xid") String xid) {
        return this.favoriteService.getPointOfInterest(xid);
    }

    @Operation(summary = "Get Number Favorite Users By Point Of Interest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Get Number Favorite Users By Point Of Interest",
                    content = {@Content(mediaType = "application/json")
                    })})
    @GetMapping("point-of-interest-count/{xid}")
    public ResponseEntity<Integer> getPointOfInterestCount(@PathVariable("xid") String xid) {
        return this.favoriteService.getPointOfInterestCount(xid);
    }

    @Operation(summary = "Create Favorite")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Create Favorite",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Favorite.class))
                    })})
    @PostMapping
    public ResponseEntity<Favorite> createFavorite(@RequestBody Favorite favorite) {
        return this.favoriteService.createFavorite(favorite);
    }

    @Operation(summary = "Delete Favorite By User ID And Point Of Interest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Delete Favorite By User ID And Point Of Interest",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Favorite.class))
                    })})
    @DeleteMapping("{userId}/{xid}")
    public ResponseEntity<Favorite> deleteFavorite(@PathVariable("userId") Long userId, @PathVariable("xid") String xid) {
        return this.favoriteService.deleteFavorite(userId, xid);
    }

}
