package com.where2go.pointsofinterestmanagementservice.repository;

import com.where2go.pointsofinterestmanagementservice.dto.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, String> {

    // Works in PostgresSQL
//    @Query(value = "SELECT p.* FROM point_of_interest p WHERE p.kinds SIMILAR TO :kinds", nativeQuery = true)
//    @Query(value = "SELECT p FROM PointOfInterest p WHERE p.kinds LIKE CONCAT('%', :kinds, '%')")

    List<PointOfInterest> findPointOfInterestByNameContainsIgnoreCase(String name);

}
