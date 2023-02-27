package com.where2go.itineraryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.where2go.itineraryservice.dto.Itinerary;
import com.where2go.itineraryservice.dto.ItineraryId;
import com.where2go.itineraryservice.externalObject.ItineraryGenParams;
import com.where2go.itineraryservice.externalObject.PointOfInterest;
import com.where2go.itineraryservice.externalObject.PointOfInterestSearched;
import com.where2go.itineraryservice.repository.ItineraryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class ItineraryService {

    private ItineraryRepository itineraryRepository;

    private RabbitTemplate template;

    @Value("${rabbitmq.producer.queue.chosen-points-of-interest-search-service.name}")
    private String chosenPointsQueue;
    @Value("${rabbitmq.producer.exchange.chosen-points-of-interest-search-service.name}")
    private String chosenPointsExchange;
    @Value("${rabbitmq.producer.routing.chosen-points-of-interest-search-service.name}")
    private String chosenPointsRoutingKey;


    @Autowired
    public ItineraryService(ItineraryRepository itineraryRepository, RabbitTemplate template) {
        this.itineraryRepository = itineraryRepository;
        this.template = template;
    }

    /**
     * Method responsible for returning a list of details about the {@link PointOfInterest} of an itinerary
     *
     * @param userId      user's id
     * @param itineraryId itinerary's id
     * @return a list of {@link PointOfInterest} of the searched itinerary and user id, ordered by its route_order
     */
    public ResponseEntity<List<Itinerary>> getUserItinerary(int userId, int itineraryId) {

        List<Itinerary> userItineraries = itineraryRepository.findByUserIdAndItineraryIdOrderedByRouteOrder(userId, itineraryId);

        if (userItineraries.isEmpty()) {
            log.info("User with id " + userId + " doesn't has the itinerary with id " + itineraryId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        log.info("Itinerary of user " + userId + " and with id of " + itineraryId + " returned successfully");
        return new ResponseEntity<>(userItineraries, HttpStatus.OK);
    }

    /**
     * Method responsible for returning the last 3 recent user itineraries
     *
     * @param userId user's id
     * @return if everything goes fine it will return a list grouped by itinerary id of {@link PointOfInterest}, otherwise
     * it will return an empty list
     */
    public ResponseEntity<List<List<Itinerary>>> getUserItineraries(int userId) {

        List<Itinerary> userItineraries = itineraryRepository.findAllUserItineraries(userId);
        List<List<Itinerary>> itinerariesArr = new ArrayList<>();

        if (userItineraries.isEmpty()) {
            log.info("User doesn't have itineraries");
            return new ResponseEntity<>(itinerariesArr, HttpStatus.NOT_FOUND);
        }

        List<Integer> itinerariesId = new ArrayList<>();

        // Getting id of each point of interest of the itinerary to be generated
        for (Itinerary itinerary : userItineraries) {
            if (!itinerariesId.contains(itinerary.getItineraryId().getItinerary_id()))
                itinerariesId.add(itinerary.getItineraryId().getItinerary_id());
        }

        // Grouping itineraries by itinerary_id
        for (Integer itineraryId : itinerariesId) {
            List<Itinerary> itineraryArr = new ArrayList<>();
            for (Itinerary itinerary : userItineraries) {
                if (itinerary.getItineraryId().getItinerary_id() == itineraryId) {
                    itineraryArr.add(itinerary);
                }
            }
            itinerariesArr.add(itineraryArr);
        }

        log.info("All user " + userId + " itineraries returned successfully {}", itinerariesArr);
        return new ResponseEntity<>(itinerariesArr, HttpStatus.OK);
    }

    /**
     * Method responsible for generating an itinerary given a list of {@link PointOfInterestSearched} chosen by the user,
     * and it will return points of interest ordered by shortest distance between them
     *
     * @param itineraryGenParams list of {@link ItineraryGenParams} chosen by the user
     * @return if everything goes fine it will return a list of {@link Itinerary} generated and a 204 response, otherwise
     * it will return a null and a 400 response
     */
    public ResponseEntity<List<Itinerary>> generateItinerary(List<ItineraryGenParams> itineraryGenParams) {

        if (itineraryGenParams.isEmpty()) {
            log.info("Couldn't generate an itinerary because the list of itinerary generation parameters is empty");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        if (itineraryGenParams.size() < 3) {
            log.info("Couldn't generate an itinerary because the list of itinerary generation parameters has less " +
                    "than 3 points of interest");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Initialize the path list and the set of unvisited points
        List<ItineraryGenParams> path = new ArrayList<>();
        Set<ItineraryGenParams> unvisitedPoints = new HashSet<>(itineraryGenParams);

        // Start with the first point in the pointList
        ItineraryGenParams currentPoint = itineraryGenParams.get(0);
        path.add(currentPoint);
        unvisitedPoints.remove(currentPoint);

        // Iterate until all points have been visited
        while (!unvisitedPoints.isEmpty()) {
            // Initialize the minimum distance and the next point to be added to the path
            double minDistance = Double.MAX_VALUE;
            ItineraryGenParams nextPoint = null;

            // Iterate over all unvisited points
            for (ItineraryGenParams point : unvisitedPoints) {
                // Calculate the distance between the current point and the unvisited point
                double distance = calculateDistanceBetweenTwoGeoPoints(
                        new Point(currentPoint.getLatitude(), currentPoint.getLongitude()),
                        new Point(point.getLatitude(), point.getLongitude()));
                if (distance < minDistance) {
                    // Update the minimum distance and the next point if a shorter distance is found
                    minDistance = distance;
                    nextPoint = point;
                }
            }
            // Add the next point to the path and remove it from the unvisited points set
            currentPoint = nextPoint;
            path.add(currentPoint);
            unvisitedPoints.remove(currentPoint);
        }

        // Setting the distance between all points of the itinerary
        for (int i = 0; i < path.size() - 1; i++) {
            path.get(i).setDistance(calculateDistanceBetweenTwoGeoPoints(new Point(path.get(i).getLatitude(), path.get(i).getLongitude()),
                    new Point(path.get(i + 1).getLatitude(), path.get(i + 1).getLongitude())));
        }

        Integer lastId = itineraryRepository.getLastItineraryID();
        if (lastId == null) {
            lastId = 0;
        }
        log.info("Current id: " + lastId);
        int finalLastId = lastId + 1;

        List<Itinerary> itinerariesSaved = new ArrayList<>();
        int route_order = 1;
        for (ItineraryGenParams param : path) {
            Itinerary itinerary = Itinerary.builder()
                    .itineraryId(ItineraryId.builder()
                            .itinerary_id(finalLastId)
                            .user_id(param.getUser_id())
                            .xid(param.getXid()).build())
                    .pointOfInterestName(param.getName())
                    .distance(param.getDistance())
                    .latitude(param.getLatitude())
                    .longitude(param.getLongitude())
                    .routeOrder(route_order)
                    .itineraryDate(LocalDate.now()).
                    build();
            itinerariesSaved.add(itinerary);
            route_order++;
        }

        log.info("Itinerary generated: {}", itinerariesSaved);
        return new ResponseEntity<>(itinerariesSaved, HttpStatus.OK);
    }


    /**
     * Method that will save an itinerary, and then it will send the information of the itinerary saved to
     * Chosen-Points-Of-Interest-Service
     *
     * @param itineraryGenerated list of points of interest that belong to the itinerary generated
     * @return if everything goes fine it will save the itinerary generated, and it will return a 204 response, otherwise
     * it will return an empty list and a 400 response
     */
    public ResponseEntity<List<Itinerary>> saveItinerary(List<Itinerary> itineraryGenerated) {

        if (itineraryGenerated.isEmpty()) {
            log.info("There are no points of interest on itinerary generated");
            return new ResponseEntity<>(itineraryGenerated, HttpStatus.BAD_REQUEST);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.writer().withDefaultPrettyPrinter();
            try {
                template.convertAndSend(chosenPointsExchange, chosenPointsRoutingKey,
                        mapper.writeValueAsString(itineraryGenerated));
                log.info("Itinerary generated: " + itineraryGenerated + " were sent to the queue: "
                        + chosenPointsQueue);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            itineraryRepository.saveAll(itineraryGenerated);
            log.info("Itinerary successfully saved");
            return new ResponseEntity<>(itineraryGenerated, HttpStatus.NO_CONTENT);
        }
    }


    /**
     * Method that will delete an itinerary given an itineraryId and a userId
     *
     * @param userId      user id
     * @param itineraryId itinerary id
     * @return if everything goes fine it will delete the specified itinerary, and it will return a 204 response, otherwise
     * it will return an empty list and a 404 response
     */
    public ResponseEntity<List<Itinerary>> deleteItinerary(int userId, int itineraryId) {

        List<Itinerary> itinerary = itineraryRepository.findByUserIdAndItineraryIdOrderedByRouteOrder(userId, itineraryId);

        if (itinerary.isEmpty()) {
            log.info("Itinerary with id " + itineraryId + " and with user id " + userId + "doesn't exists on DB");
            return new ResponseEntity<>(itinerary, HttpStatus.NOT_FOUND);
        } else {
            itineraryRepository.deleteAll(itinerary);
            log.info("Itinerary with id " + itineraryId + " successfully deleted");
            return new ResponseEntity<>(itinerary, HttpStatus.NO_CONTENT);
        }
    }


    /**
     * Method that will delete a certain point of interest of a certain itinerary given an itineraryId, a userId and xid
     *
     * @param userId      user id
     * @param itineraryId itinerary id
     * @param xid         xid of the point of interest to be deleted from the specified itinerary
     * @return if everything goes fine it will delete the specified point of interest of the specified itinerary, and it
     * will return a 204 response, otherwise it will return an empty list and a 404 response
     */
    public ResponseEntity<Itinerary> deletePointsOfInterestOfItinerary(int userId, int itineraryId, String xid) {

        Itinerary itinerary = itineraryRepository.findByUserIdAndItineraryIdAndXidOrderedByRouteOrder(userId, itineraryId, xid);

        if (itinerary == null) {
            log.info("Itinerary with id " + itineraryId + " and with user id " + userId + " doesn't contains point of interest with" +
                    "id " + xid);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            itineraryRepository.delete(itinerary);
            log.info("Point of interest with id " + xid + " from itinerary with id " + itineraryId + " successfully deleted");
            return new ResponseEntity<>(itinerary, HttpStatus.NO_CONTENT);
        }
    }


    /**
     * Method responsible for calculating the distance (in meters) between 2 coordinates given their latitude and longitude
     *
     * @param point1 start point of type {@link Point}
     * @param point2 end point of type {@link Point}
     * @return distance between the 2 coordinates
     */
    public static double calculateDistanceBetweenTwoGeoPoints(Point point1, Point point2) {

        // Converting degrees to radians
        double userLat1 = Math.toRadians(point1.getX());
        double userLon1 = Math.toRadians(point1.getY());
        double pointOfInterestLat1 = Math.toRadians(point2.getX());
        double pointOfInterestLon1 = Math.toRadians(point2.getY());

        // Haversine formula
        double dlon = pointOfInterestLon1 - userLon1;
        double dlat = pointOfInterestLat1 - userLat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(userLat1) * Math.cos(pointOfInterestLat1)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in km. Use 6 371 km
        double r = 6371;

        // Calculate the result (in meters)
        return (c * r) * 1000;
    }

}