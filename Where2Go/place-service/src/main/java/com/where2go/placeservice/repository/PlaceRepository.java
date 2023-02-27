package com.where2go.placeservice.repository;

import com.where2go.placeservice.dto.Place;
import com.where2go.placeservice.dto.PlaceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlaceRepository extends JpaRepository<Place, PlaceId> {
    @Query("SELECT p FROM Place p WHERE p.name = ?1 and p.countryLetter= ?2")
    Place findByNameAndCountryLetter(String name, String countryLetter);

    @Query("SELECT p FROM Place p WHERE p.placeId.latitude = ?1 and p.placeId.longitude= ?2")
    Place findByLatitudeAndLongitude(double lat, double lon);
}
