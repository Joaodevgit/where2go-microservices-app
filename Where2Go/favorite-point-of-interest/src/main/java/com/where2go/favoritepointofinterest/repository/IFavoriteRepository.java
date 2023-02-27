package com.where2go.favoritepointofinterest.repository;

import com.where2go.favoritepointofinterest.dto.Favorite;
import com.where2go.favoritepointofinterest.dto.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IFavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    @Query("SELECT favorite FROM Favorite favorite WHERE favorite.favoriteId.user_id = ?1 and favorite.favoriteId.xid = ?2")
    Optional<Favorite> findFavoriteById(Long user_id, String xid);

    @Query("SELECT favorite FROM Favorite favorite WHERE favorite.favoriteId.user_id = ?1")
    List<Favorite> findFavoritesByUserId(Long userId);

    @Query("SELECT favorite FROM Favorite favorite WHERE favorite.favoriteId.xid = ?1")
    List<Favorite> findPointOfInterestById(String xid);
}
