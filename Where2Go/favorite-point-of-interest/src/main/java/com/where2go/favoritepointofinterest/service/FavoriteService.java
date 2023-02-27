package com.where2go.favoritepointofinterest.service;

import com.where2go.favoritepointofinterest.dto.Favorite;
import com.where2go.favoritepointofinterest.dto.FavoriteId;
import com.where2go.favoritepointofinterest.repository.IFavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {
    private final IFavoriteRepository favoriteRepository;

    @Autowired
    public FavoriteService(IFavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public ResponseEntity<List<Favorite>> getFavorites() {
        List<Favorite> favoriteList = favoriteRepository.findAll();

        return new ResponseEntity<>(favoriteList, HttpStatus.OK);
    }

    public ResponseEntity<Favorite> createFavorite(Favorite favorite) {
        Long userId = favorite.getFavoriteId().getUser_id();
        String xid = favorite.getFavoriteId().getXid();
        String favoriteName = favorite.getFavoriteName();
        String url = favorite.getUrl();

        Optional<Favorite> favoriteOptional = favoriteRepository.findFavoriteById(userId, xid);

        if (favoriteOptional.isPresent())
            throw new IllegalStateException("Favorite Taken");

        FavoriteId favoriteId = new FavoriteId(userId, xid);

        Favorite eFavorite = new Favorite(favoriteId, favoriteName, url);

        favoriteRepository.save(eFavorite);

        return new ResponseEntity<>(favorite, HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<List<Favorite>> getUserFavorites(Long userId) {
        List<Favorite> favoriteList = favoriteRepository.findFavoritesByUserId(userId);

        if (favoriteList.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(favoriteList, HttpStatus.OK);
    }

    public ResponseEntity<List<Favorite>> getPointOfInterest(String xid) {
        List<Favorite> favoriteList = favoriteRepository.findPointOfInterestById(xid);

        if (favoriteList.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(favoriteList, HttpStatus.OK);
    }

    public ResponseEntity<Favorite> deleteFavorite(Long userId, String xid) {
        FavoriteId favoriteId = new FavoriteId(userId, xid);

        boolean exist = favoriteRepository.existsById(favoriteId);

        if (!exist) {
            throw new IllegalStateException("User With id " + userId + " And Place " + xid + " does not exists");
        }

        favoriteRepository.deleteById(favoriteId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<Integer> getPointOfInterestCount(String xid) {
        List<Favorite> favoriteList = favoriteRepository.findPointOfInterestById(xid);

        if (favoriteList.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(favoriteList.size(), HttpStatus.OK);
    }
}
