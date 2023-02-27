package com.where2go.searchhistoryservice.repository;

import com.where2go.searchhistoryservice.dto.PointOfInterestSearched;
import com.where2go.searchhistoryservice.dto.PointOfInterestSearchedId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<PointOfInterestSearched, PointOfInterestSearchedId> {
    @Query("SELECT MAX(p.pointOfInterestSearchedId.search_id) FROM PointOfInterestSearched p")
    Integer getLastSearchID();

    @Query(value = "SELECT p.*" +
            "       FROM point_of_interest_searched p" +
            "       WHERE p.user_id = ?1 AND p.search_id IN (SELECT p.search_id" +
            "                                                FROM (SELECT p.search_id, MAX(p.date) AS max_date" +
            "                                                      FROM point_of_interest_searched p" +
            "                                                      GROUP BY search_id" +
            "                                                      ORDER BY max_date DESC" +
            "                                                      LIMIT 5 ) AS p)" +
            "       ORDER BY p.date DESC",nativeQuery = true)
    List<PointOfInterestSearched> findLastFiveUserSearches(int userId);

}
