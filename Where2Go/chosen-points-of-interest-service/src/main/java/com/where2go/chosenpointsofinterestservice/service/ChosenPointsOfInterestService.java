package com.where2go.chosenpointsofinterestservice.service;

import com.where2go.chosenpointsofinterestservice.dto.ChosenPointOfInterest;
import com.where2go.chosenpointsofinterestservice.dto.ChosenPointOfInterestId;
import com.where2go.chosenpointsofinterestservice.repository.ChosenPointsOfInterestRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class ChosenPointsOfInterestService {

    @Autowired
    private ChosenPointsOfInterestRepository chosenPointsOfInterestRepository;

    /**
     * Method that will delete a list of chosen points of interest received from itinerary-service
     *
     * @param chosenPointsOfInterestReceived JSONArray with chosen points of interest used to generate the itinerary
     */
    @RabbitListener(queues = "${rabbitmq.consumer.queue.chosen-points-of-interest-search-service.name}")
    public void receiveChosenPointsOfInterestFromItineraryService(@Payload JSONArray chosenPointsOfInterestReceived) {
        log.info("OBJ RECEIVED {}", chosenPointsOfInterestReceived);

        List<ChosenPointOfInterest> chosenPointsOfInterest = new ArrayList<>();

        for (int i = 0; i < chosenPointsOfInterestReceived.length(); i++) {
            JSONObject pointOfInterest = (JSONObject) chosenPointsOfInterestReceived.get(i);

            ChosenPointOfInterest chosenPointOfInterest =
                    ChosenPointOfInterest.builder()
                            .chosenPointOfInterestId(ChosenPointOfInterestId.builder()
                                    .user_id(pointOfInterest.getInt("user_id"))
                                    .xid(pointOfInterest.getString("xid")).build())
                            .name(pointOfInterest.getString("pointOfInterestName"))
                            .build();

            chosenPointsOfInterest.add(chosenPointOfInterest);
        }

        log.info("Chosen points generated: {}", chosenPointsOfInterest);
        chosenPointsOfInterestRepository.deleteAll(chosenPointsOfInterest);
    }

    /**
     * Method that will return a list of {@link ChosenPointOfInterest} with all the points of interest chosen by the user
     *
     * @param userId user id
     * @return if the user has chosen points of interest list of {@link ChosenPointOfInterest} with all the points of
     * interest chosen, otherwise it will return an empty list
     */
    public ResponseEntity<List<ChosenPointOfInterest>> getAllUserChosenPointsInterest(int userId) {
        List<ChosenPointOfInterest> chosenPointOfInterests = chosenPointsOfInterestRepository.findByUserId(userId);

        if (chosenPointOfInterests.isEmpty()) {
            log.info("User " + userId + " doesn't have any chosen points of interest");
            return new ResponseEntity<>(chosenPointOfInterests, HttpStatus.NOT_FOUND);
        }

        log.info("User " + userId + " has chosen the following points of interest: {}", chosenPointOfInterests);
        return new ResponseEntity<>(chosenPointOfInterests, HttpStatus.OK);
    }

    /**
     * Method that will save an user {@link ChosenPointOfInterest}
     *
     * @param chosenPointOfInterest {@link ChosenPointOfInterest} to be saved
     * @return if everything goes fine it will return a list of {@link ChosenPointOfInterest} saved and a 204 response,
     * otherwise if the {@link ChosenPointOfInterest} already exists it will return a null and a 409 response
     */
    public ResponseEntity<ChosenPointOfInterest> saveUserChosenPointsInterest(ChosenPointOfInterest chosenPointOfInterest) {

        ChosenPointOfInterest chosenPointOfInterestDB = chosenPointsOfInterestRepository.findByUserIdAndXid(
                chosenPointOfInterest.getChosenPointOfInterestId().getUser_id(), chosenPointOfInterest.getChosenPointOfInterestId().getXid());

        if (chosenPointOfInterestDB == null) {
            chosenPointsOfInterestRepository.save(chosenPointOfInterest);
            log.info("User chosen point of interest {} ", chosenPointOfInterest + " saved on DB");
            return new ResponseEntity<>(chosenPointOfInterest, HttpStatus.NO_CONTENT);
        } else {
            log.info("User chosen point of interest {} ", chosenPointOfInterest + " already exists on DB");
            return new ResponseEntity<>(chosenPointOfInterest, HttpStatus.CONFLICT);
        }
    }


    /**
     * Method that will delete an user {@link ChosenPointOfInterest}
     *
     * @param userId id of the user {@link ChosenPointOfInterest}
     * @param xid xid {@link ChosenPointOfInterest} to be deleted
     * @return if everything goes fine it will return a list of {@link ChosenPointOfInterest} saved and a 204 response,
     * otherwise it will return a null and a 409 response
     */
    public ResponseEntity<ChosenPointOfInterest> deleteUserChosenPointsInterest(int userId, String xid) {

        ChosenPointOfInterest chosenPointOfInterestToBeDeleted = chosenPointsOfInterestRepository.findByUserIdAndXid(
                userId, xid);

        if (chosenPointOfInterestToBeDeleted != null) {
            chosenPointsOfInterestRepository.delete(chosenPointOfInterestToBeDeleted);
            log.info("User chosen point of interest {} ", chosenPointOfInterestToBeDeleted + " deleted from DB");
            return new ResponseEntity<>(chosenPointOfInterestToBeDeleted, HttpStatus.NO_CONTENT);
        } else {
            log.info("User with id" + userId + " and with chosen point of interest " + xid + " doesn't exists on DB");
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }
}
