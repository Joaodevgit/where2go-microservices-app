package com.where2go.itineraryservice.repository;

import com.where2go.itineraryservice.dto.Itinerary;
import com.where2go.itineraryservice.dto.ItineraryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItineraryRepository extends JpaRepository<Itinerary, ItineraryId> {

    @Query("SELECT i FROM Itinerary i WHERE i.itineraryId.user_id = ?1 and i.itineraryId.itinerary_id= ?2 ORDER BY i.routeOrder")
    List<Itinerary> findByUserIdAndItineraryIdOrderedByRouteOrder(int userId, int itineraryId);

    @Query("SELECT i FROM Itinerary i WHERE i.itineraryId.user_id = ?1 and i.itineraryId.itinerary_id = ?2 and i.itineraryId.xid = ?3 ORDER BY i.routeOrder")
    Itinerary findByUserIdAndItineraryIdAndXidOrderedByRouteOrder(int userId, int itineraryId, String xid);

    @Query(value = "SELECT i.*" +
            "       FROM ITINERARY i" +
            "       WHERE i.user_id=?1" +
            "       ORDER BY i.date DESC", nativeQuery = true)
    List<Itinerary> findAllUserItineraries(int userId);

    @Query("SELECT MAX(i.itineraryId.itinerary_id) FROM Itinerary i")
    Integer getLastItineraryID();

}
