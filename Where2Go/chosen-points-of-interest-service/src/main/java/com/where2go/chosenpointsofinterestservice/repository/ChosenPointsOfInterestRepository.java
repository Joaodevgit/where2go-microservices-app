package com.where2go.chosenpointsofinterestservice.repository;

import com.where2go.chosenpointsofinterestservice.dto.ChosenPointOfInterest;
import com.where2go.chosenpointsofinterestservice.dto.ChosenPointOfInterestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChosenPointsOfInterestRepository extends JpaRepository<ChosenPointOfInterest, ChosenPointOfInterestId> {

    @Query("SELECT c FROM ChosenPointOfInterest c WHERE c.chosenPointOfInterestId.user_id = ?1")
    List<ChosenPointOfInterest> findByUserId(int userId);

    @Query("SELECT c FROM ChosenPointOfInterest c WHERE c.chosenPointOfInterestId.user_id = ?1 and c.chosenPointOfInterestId.xid = ?2")
    ChosenPointOfInterest findByUserIdAndXid(int userId, String xid);
}
