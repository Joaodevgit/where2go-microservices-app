package com.where2go.searchhistoryservice.service;

import com.where2go.searchhistoryservice.dto.PointOfInterestSearched;
import com.where2go.searchhistoryservice.dto.PointOfInterestSearchedId;
import com.where2go.searchhistoryservice.repository.SearchHistoryRepository;
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
public class SearchHistoryService {

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    /**
     * Method responsible for returning the last 3 user searches on points of interest
     *
     * @param user_id user id
     * @return if everything goes fine it will return a list of {@link PointOfInterestSearched}and a 200 response,
     * otherwise it will return an empty list and a 404 response
     */
    public ResponseEntity<List<PointOfInterestSearched>> getUserLastFiveSearches(int user_id) {

        List<PointOfInterestSearched> lastFivePointsSearched = searchHistoryRepository.findLastFiveUserSearches(user_id);
        if (lastFivePointsSearched.isEmpty()) {
            log.info("User doesn't have any search history");
            return new ResponseEntity<>(lastFivePointsSearched, HttpStatus.NOT_FOUND);
        }
        log.info("User search history list returned successfully");
        return new ResponseEntity<>(lastFivePointsSearched, HttpStatus.OK);
    }

    /**
     * Method responsible for receiving from the points-of-interest-management-service the information about the points of
     * interest searched by the user and then store it on db
     *
     * @param pointOfInterestSearchReceived {@link PointOfInterestSearched} received from points-of-interest-management-service
     */
    @RabbitListener(queues = "${rabbitmq.consumer.queue.point-of-interest-search-service.name}")
    public void receivePlaceFromPlaceService(@Payload JSONArray pointOfInterestSearchReceived) {
        log.info("OBJ RECEIVED {}", pointOfInterestSearchReceived);
        Integer lastSearchId = searchHistoryRepository.getLastSearchID();
        if (lastSearchId == null) {
            lastSearchId = 0;
        }

        List<PointOfInterestSearched> pointOfInterestSearchedList = new ArrayList<>();

        for (int i = 0; i < pointOfInterestSearchReceived.length(); i++) {
            JSONObject pointOfInterest = (JSONObject) pointOfInterestSearchReceived.get(i);

            PointOfInterestSearched pointOfInterestSearched = PointOfInterestSearched.builder()
                    .pointOfInterestSearchedId(PointOfInterestSearchedId.builder()
                            .search_id(lastSearchId + 1)
                            .user_id(pointOfInterest.getInt("user_id"))
                            .xid(pointOfInterest.getString("xid")).build())
                    .name(pointOfInterest.getString("name"))
                    .description(pointOfInterest.getString("description"))
                    .distance(pointOfInterest.getDouble("distance"))
                    .radius(pointOfInterest.getInt("radius"))
                    .rate(pointOfInterest.getInt("rate"))
                    .kinds(pointOfInterest.getString("kinds"))
                    .image(pointOfInterest.getString("image"))
                    .latitude(pointOfInterest.getDouble("latitude"))
                    .longitude(pointOfInterest.getDouble("longitude"))
                    .searchDate(pointOfInterest.getString("searchDate"))
                    .build();
            log.info("OBJ GENERATED {}", pointOfInterestSearched);
            pointOfInterestSearchedList.add(pointOfInterestSearched);
        }
        searchHistoryRepository.saveAll(pointOfInterestSearchedList);
    }
}
