package com.example.pointofinterestreviewservice.repository;

import com.example.pointofinterestreviewservice.dto.Review;
import com.example.pointofinterestreviewservice.dto.ReviewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, ReviewId> {

    @Query("SELECT r FROM Review r WHERE r.reviewId.user_id=?1 ORDER BY r.reviewDate DESC")
    List<Review> findByUserId(int userId);

    @Query("SELECT r FROM Review r WHERE r.reviewId.xid=?1 ORDER BY r.reviewDate DESC")
    List<Review> findPointOfInterestReviewsOrderedByDate(String xid);
}
