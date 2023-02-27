package com.example.pointofinterestreviewservice.service;

import com.example.pointofinterestreviewservice.dto.Review;
import com.example.pointofinterestreviewservice.dto.ReviewId;
import com.example.pointofinterestreviewservice.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReviewService {

    private ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }


    /**
     * Method responsible for returning the details of a user {@link Review} made on a point of interest given xid and
     * user id
     *
     * @param xid    point of interest's xid
     * @param userId user's xid
     * @return if the GET request is successful it will return the user review details of the specified point of interest,
     * otherwise it will return null
     */
    public ResponseEntity<Review> getUserReviewDetail(String xid, int userId) {

        Optional<Review> userReview = reviewRepository.findById(new ReviewId(xid, userId));

        if (userReview.isEmpty()) {
            log.info("User with id " + userId + " doesn't have any review written to point of interest with id " + xid);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        log.info("Review of user with id" + userId + " has the following detail: {}", userReview);
        return new ResponseEntity<>(userReview.get(), HttpStatus.OK);
    }

    /**
     * Method responsible for returning a list of user {@link Review}s given user id
     *
     * @param userId user's id
     * @return if the GET request is successful it will return a list of user {@link Review}, otherwise it will return
     * an empty list
     */
    public ResponseEntity<List<Review>> getUserReviews(int userId) {

        List<Review> reviews = reviewRepository.findByUserId(userId);

        if (reviews.isEmpty()) {
            log.info("User with id " + userId + " doesn't have any review written");
            return new ResponseEntity<>(reviews, HttpStatus.NOT_FOUND);
        }

        log.info("User with id " + userId + " has written the following reviews: {}", reviews);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    /**
     * Method responsible for returning a list of all reviews of a point of interest given its xid
     *
     * @param xid point of interest's xid
     * @return if the GET request is successful it will return a list of {@link Review}s the specified point of interest,
     * otherwise it will return an empty list
     */
    public ResponseEntity<List<Review>> getPointOfInterestReviews(String xid) {

        List<Review> pointOfInterestReviews = reviewRepository.findPointOfInterestReviewsOrderedByDate(xid);

        if (pointOfInterestReviews.isEmpty()) {
            log.info("Point of interest with id " + xid + " doesn't have any review written");
            return new ResponseEntity<>(pointOfInterestReviews, HttpStatus.NOT_FOUND);
        }

        log.info("Point of interest with id " + xid + " has the following reviews: {}", pointOfInterestReviews);
        return new ResponseEntity<>(pointOfInterestReviews, HttpStatus.OK);
    }

    /**
     * Method responsible for adding a user {@link Review} on a point of interest
     *
     * @param review {@link Review} given by the user
     * @return if the POST request is successful it will add the user review, if the review rate is less than 0 and greater
     * than 3 it will fail and if the user already has reviewed the point of interest it will also fail
     */
    public ResponseEntity<Review> addUserReview(Review review) {


        if (review.getRate() < 0 || review.getRate() > 3) {
            log.info("Point of interest rate value should be between 1 and 3");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Optional<Review> userReview = reviewRepository.findById(new ReviewId(review.getReviewId().getXid()
                , review.getReviewId().getUser_id()));

        if (userReview.isPresent()) {
            log.info("User with id " + review.getReviewId().getUser_id() + " already written a review on point of " +
                    "interest of id:" + review.getReviewId().getXid());
            return new ResponseEntity<>(userReview.get(), HttpStatus.CONFLICT);
        }

        review.setReviewDate(LocalDateTime.now());

        reviewRepository.save(review);

        log.info("User " + review.getReviewId().getUser_id() + " review with content " + review + " added " +
                "successfully");
        return new ResponseEntity<>(review, HttpStatus.NO_CONTENT);
    }

    /**
     * Method responsible for returning the rate of a point of interest based on users rate
     *
     * @param xid xid of point of interest
     * @return it will return 0.0 if the point of interest doesn't have any review otherwise it will return rate based
     * on calculations between user rates on that point of interest
     */
    public ResponseEntity<Double> calculatePointOfInterestRate(String xid) {

        List<Review> pointsOfInterestRates = reviewRepository.findPointOfInterestReviewsOrderedByDate(xid);

        if (pointsOfInterestRates.isEmpty()) {
            return new ResponseEntity<>(0.0, HttpStatus.OK);
        }

        Double totRate = pointsOfInterestRates
                .stream()
                .map(Review::getRate).toList()
                .stream()
                .reduce(0.0, Double::sum);

        return new ResponseEntity<>(totRate / pointsOfInterestRates.size(), HttpStatus.OK);
    }

}