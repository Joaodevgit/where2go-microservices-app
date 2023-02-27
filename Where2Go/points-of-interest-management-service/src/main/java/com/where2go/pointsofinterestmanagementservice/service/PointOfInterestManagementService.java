package com.where2go.pointsofinterestmanagementservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.where2go.pointsofinterestmanagementservice.dto.PointOfInterest;
import com.where2go.pointsofinterestmanagementservice.externalObjects.PointOfInterestRadiusAttributes;
import com.where2go.pointsofinterestmanagementservice.externalObjects.PointOfInterestSearched;
import com.where2go.pointsofinterestmanagementservice.repository.PointOfInterestRepository;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PointOfInterestManagementService {

    private PointOfInterestRepository pointOfInterestRepository;

    private RabbitTemplate template;

    @Value("${rabbitmq.producer.queue.point-of-interest-search-service.name}")
    private String pointOfInterestSearchQueue;
    @Value("${rabbitmq.producer.exchange.point-of-interest-search-service.name}")
    private String pointOfInterestSearchExchange;
    @Value("${rabbitmq.producer.routing.point-of-interest-search-service.name}")
    private String pointOfInterestSearchRoutingKey;

    @Autowired
    public PointOfInterestManagementService(PointOfInterestRepository pointOfInterestRepository, RabbitTemplate template) {
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.template = template;
    }

    @Value("${api.key.pointOfInterest.name}")
    private String pointOfInterestAPIKey;

    /**
     * Method that is responsible to get the details of a {@link PointOfInterest} given its xid
     *
     * @param xid point of interest's xid
     * @return if the GET request is successful it returns the details of {@link PointOfInterest}, otherwise returns null
     */
    public ResponseEntity<PointOfInterest> getPointOfInterestDetails(String xid) {
        PointOfInterest pointOfInterest = getPointOfInterestDetailsAPI(xid);
        if (pointOfInterest != null) {
            log.info("Trying to get point of Interest with id " + xid + " by requesting to OpenTripMap API...");
            return new ResponseEntity<>(pointOfInterest, HttpStatus.OK);
        } else {
            Optional<PointOfInterest> pointOfInterestById = pointOfInterestRepository.findById(xid);
            if (pointOfInterestById.isPresent()) {
                log.info("Point of Interest: " + pointOfInterestById + " found in DB");
                return new ResponseEntity<>(pointOfInterestById.get(), HttpStatus.OK);
            } else {
                log.info("Point of Interest: " + pointOfInterestById + " not found in DB");
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        }
    }

    /**
     * Method responsible for getting a {@link PointOfInterest} from OpenTripMap API given its xid
     *
     * @param xid point of interest xid
     * @return if the GET request is successful it returns the {@link PointOfInterest}, otherwise returns null
     */
    public PointOfInterest getPointOfInterestDetailsAPI(String xid) {

        // Timeout of 10s to connect to the external API and 30s to get all the response from it
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("https://api.opentripmap.com/0.1/en/places/xid/" + xid +
                        "?apikey=" + pointOfInterestAPIKey)
                .build();

        Response response;
        JSONObject obj;
        try {
            response = client.newCall(request).execute();
            obj = new JSONObject(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // If openTripMap API sends a response 200
        if (response.isSuccessful()) {
            if (!obj.getString("name").equals("")) {
                log.info("Response with OpenTripMap API successful");
                log.info("Retrieved point of interest with name " + obj.getString("name") +
                        " and with xid " + obj.getString("xid"));

                JSONObject pointObj = (JSONObject) obj.get("point");

                String description;
                String image;
                double rate;
                String bookingUrl = null;
                int stars = 0;

                // If doesn't have a description
                if (obj.has("wikipedia_extracts")) {
                    JSONObject wikipediaExtractsObj = (JSONObject) obj.get("wikipedia_extracts");
                    description = wikipediaExtractsObj.get("text").toString();
                } else {
                    description = "Não tem descrição";
                }

                // If it doesn't have an image
                if (obj.has("preview")) {
                    JSONObject imageObj = (JSONObject) obj.get("preview");
                    image = imageObj.get("source").toString();
                } else {
                    image = "https://user-images.githubusercontent.com/44362304/209017055-a4005aee-52ab-4c68-85e8-7e3aa3f05ed3.jpg";
                }

                // Converting rate (String) into double and if rate contains the character "h" replaces it with ".5"
                if (obj.get("rate").toString().contains("h")) {
                    String oldRate = obj.get("rate").toString().replace("h", "");
                    rate = Double.parseDouble(oldRate);
                } else {
                    rate = Double.parseDouble(obj.get("rate").toString());
                }

                // If it has an url
                if (obj.has("url")) {
                    bookingUrl = obj.get("url").toString();
                }

                // If it has stars
                if (obj.has("stars")) {
                    stars = Integer.parseInt(obj.get("stars").toString());
                }


                return PointOfInterest.builder()
                        .xid(obj.get("xid").toString())
                        .name(obj.get("name").toString())
                        .decription(description)
                        .rate(rate)
                        .image(image)
                        .bookingUrl(bookingUrl)
                        .stars(stars)
                        .image(image)
                        .latitude(Double.parseDouble(pointObj.get("lat").toString()))
                        .longitude(Double.parseDouble(pointObj.get("lon").toString()))
                        .kinds(obj.get("kinds").toString())
                        .build();
            }
            log.info("Point of interest with " + xid + " exists in OpenTripMap API, however it doesn't have a name");
            return null;
        }

        log.info("Point of interest with " + xid + " doesn't exist in OpenTripMap API");
        return null;
    }

    /**
     * Method that is responsible for returning a list of all {@link PointOfInterest}
     *
     * @return returns a list of all {@link PointOfInterest}
     */
    public ResponseEntity<List<PointOfInterest>> getAllPointsOfInterest() {
        List<PointOfInterest> pointsOfInterest = pointOfInterestRepository.findAll();

        if (!pointsOfInterest.isEmpty()) {
            log.info("List of all points of interest returned successfully");
            return new ResponseEntity<>(pointsOfInterest, HttpStatus.OK);
        } else {
            log.info("There are no points of interest");
            return new ResponseEntity<>(pointsOfInterest, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Method responsible for making at first instance a request to OpenTripMap API of points of interest given user
     * latitude, longitude, that are in the given radius, kinds and points of interest searched name.
     * If the request to OpenTripMap API fails, it will look for {@link PointOfInterest} in database where it will be
     * returned a list of all points of interest and then it will verify if the distance between the user and each point of
     * interest from the list requested are inside the radius given, and finally it orders that list by points of interest
     * with high rating and then with short distance, finally it will tell to search-history-service to save list of
     * {@link PointOfInterestSearched}
     *
     * @param userLat      user latitude
     * @param userLon      user longitude
     * @param radius       points of interest radius (in meters)
     * @param kinds        points of interest kinds/categories
     * @param nameSearched point of interest searched name
     * @param userId       id of the user that made the search
     * @return list of {@link PointOfInterestSearched} that meets with the parameters given user latitude, user longitude,
     * radius value of the points of interest nearby, points of interest kinds/categories, point of interest name searched
     * (optional) and limit of points of interest to be shown.
     */
    public ResponseEntity<List<PointOfInterestSearched>> getPointsOfInterestsByRadius(double userLat, double userLon,
                                                                                      int radius, String kinds,
                                                                                      String nameSearched, int userId) {
        List<PointOfInterestSearched> pointsOfInterestInRadius;
        pointsOfInterestInRadius = getPointOfInterestInRadiusAPI(userLat, userLon, radius, kinds, nameSearched, userId);
        if (pointsOfInterestInRadius.isEmpty()) {

            pointsOfInterestInRadius = getPointsOfInterestsByRadiusDB(userLat, userLon, radius, kinds, nameSearched, userId);

            if (pointsOfInterestInRadius.isEmpty()) {
                log.info("No points of interested found in OpenTripMap API nor in DB");
                return new ResponseEntity<>(pointsOfInterestInRadius, HttpStatus.NOT_FOUND);
            } else {

                if (userId != 0) {
                    // Communicate with Search-History-Service microservice
                    sendPointOfInterestSearchedToSearchHistoryService(pointOfInterestSearchExchange,
                            pointOfInterestSearchRoutingKey, pointsOfInterestInRadius);
                    log.info("Successfully saved points of interested searched by the user that are inside the radius "
                            + radius + " meters obtained from DB");
                }
                log.info("Successfully returned points of interested obtained from DB");
                return new ResponseEntity<>(pointsOfInterestInRadius, HttpStatus.OK);
            }
        } else {
            if (userId != 0) {
                // Communicate with Search-History-Service microservice
                sendPointOfInterestSearchedToSearchHistoryService(pointOfInterestSearchExchange,
                        pointOfInterestSearchRoutingKey, pointsOfInterestInRadius);
                log.info("Successfully saved points of interested searched by the user that are inside the radius "
                        + radius + " meters obtained from OpenTripMap API");
            }
            log.info("Successfully returned points of interested that are inside the radius " + radius + " meters obtained " +
                    "from OpenTripMap API");
            return new ResponseEntity<>(pointsOfInterestInRadius, HttpStatus.OK);
        }
    }

    /**
     * Method responsible for making a request to OpenTripMap API of points of interest given user latitude, longitude,
     * that are in the given radius, kinds and points of interest searched name.
     * If openTripMap API sends a response 200 it will return a list of {@link PointOfInterestSearched} with at least
     * size of 1 otherwise it will return an empty list
     *
     * @param userLat      user latitude
     * @param userLon      user longitude
     * @param radius       points of interest radius (in meters)
     * @param kinds        points of interest kinds/categories
     * @param nameSearched point of interest searched name
     * @param userId       id of the user that made the search
     * @return list of {@link PointOfInterestSearched} that meets with the parameters given
     */
    public List<PointOfInterestSearched> getPointOfInterestInRadiusAPI(double userLat, double userLon, int radius,
                                                                       String kinds,
                                                                       String nameSearched, int userId) {

        // Timeout of 10s to connect to the external API and 30s to get all the response from it
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request requestPointsOfInterestRadius;
        String finalKinds = String.join("%2C", kinds.replaceAll("\\s", "").split(","));
        String defaultKinds = "interesting_places%2Caccomodations%2Cadult%2Camusements%2Csport%2Ctourist_facilities";
        String urlFilters;


        // If user searched by all the filters (userLat, userLon, radius, kinds, name)
        if (!nameSearched.isBlank() && !kinds.isBlank()) {
            urlFilters = "https://api.opentripmap.com/0.1/en/places/autosuggest?name=" + nameSearched + "&radius=" + radius
                    + "&lon=" + userLon + "&lat=" + userLat + "&kinds=" + finalKinds + "&limit=20&format=json&apikey="
                    + pointOfInterestAPIKey;
            // If user did not search by kinds (userLat, userLon, radius, name)
            // Note: autosuggest endpoint already defines all kinds by default
        } else if (kinds.isBlank() && !nameSearched.isBlank()) {
            urlFilters = "https://api.opentripmap.com/0.1/en/places/autosuggest?name=" + nameSearched + "&radius=" + radius
                    + "&lon=" + userLon + "&lat=" + userLat + "&limit=20&format=json&apikey=" +
                    pointOfInterestAPIKey;
            // If user did not search point interest name but searched the rest of the filters
            // (userLat, userLon, radius, kinds)
        } else if (nameSearched.isBlank() && !kinds.isBlank()) {
            urlFilters = "https://api.opentripmap.com/0.1/en/places/radius?radius=" + radius + "&lon=" + userLon +
                    "&lat=" + userLat + "&kinds=" + finalKinds + "&limit=20&format=json&apikey=" + pointOfInterestAPIKey;
            // If user searched only for latitude, longitude and radius (userLat, userLon, radius)
        } else {
            urlFilters = "https://api.opentripmap.com/0.1/en/places/radius?radius=" + radius
                    + "&lon=" + userLon + "&lat=" + userLat + "&limit=20&kinds=" + defaultKinds + "&format=json&apikey="
                    + pointOfInterestAPIKey;
        }

        requestPointsOfInterestRadius = new Request.Builder()
                .url(urlFilters)
                .build();

        Response responsePointsOfInterestRadius;
        JSONArray pointsOfInterestRadiusArr;
        try {
            responsePointsOfInterestRadius = client.newCall(requestPointsOfInterestRadius).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<PointOfInterestSearched> pointOfInterestInRadius = new ArrayList<>();
        // If openTripMap API sends a response 200
        if (responsePointsOfInterestRadius.isSuccessful()) {
            log.info("Response with OpenTripMap API successful");
            try {
                pointsOfInterestRadiusArr = new JSONArray(responsePointsOfInterestRadius.body().string());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            List<String> featuresXids = new ArrayList<>();
            List<PointOfInterestRadiusAttributes> pointsOfInterestSearchedAttributes = new ArrayList<>();
            for (int i = 0; i < pointsOfInterestRadiusArr.length(); i++) {
                JSONObject pointOfInterestProperties = (JSONObject) pointsOfInterestRadiusArr.get(i);
                pointsOfInterestSearchedAttributes.add(PointOfInterestRadiusAttributes.builder()
                        .xid(pointOfInterestProperties.getString("xid"))
                        .distance(pointOfInterestProperties.getDouble("dist"))
                        .radius(radius)
                        .build());
                featuresXids.add(pointOfInterestProperties.getString("xid"));
            }
            if (!featuresXids.isEmpty()) {
                String searchDate = LocalDateTime.now().toString();
                pointsOfInterestSearchedAttributes.forEach(pointRadius -> {
                    PointOfInterest pointOfInterest = getPointOfInterestDetailsAPI(pointRadius.getXid());
                    if (pointOfInterest != null) {
                        pointOfInterestInRadius.add(PointOfInterestSearched.builder()
                                .xid(pointRadius.getXid())
                                .user_id(userId)
                                .name(pointOfInterest.getName())
                                .description(pointOfInterest.getDecription())
                                .kinds(pointOfInterest.getKinds())
                                .distance(pointRadius.getDistance())
                                .radius(pointRadius.getRadius())
                                .rate(pointOfInterest.getRate())
                                .image(pointOfInterest.getImage())
                                .latitude(pointOfInterest.getLatitude())
                                .longitude(pointOfInterest.getLongitude())
                                .searchDate(searchDate)
                                .build());
                    }
                });

                Comparator<PointOfInterestSearched> byDistanceAndRate = Comparator
                        .comparing(PointOfInterestSearched::getRate)
                        .reversed()
                        .thenComparing(PointOfInterestSearched::getDistance);

                log.info("Point of interests returned {}", pointOfInterestInRadius);
                return pointOfInterestInRadius.stream()
                        .sorted(byDistanceAndRate)
                        .limit(20)
                        .collect(Collectors.toList());
            }
        }

        log.info("List of points of interests is empty");
        return pointOfInterestInRadius;
    }

    /**
     * Method responsible for making a request to database given user latitude, longitude, that are in the given radius,
     * kinds and points of interest searched name.
     * It will return a list of {@link PointOfInterestSearched} with at least size of 1 otherwise it will return an
     * empty list
     *
     * @param userLat      user latitude
     * @param userLon      user longitude
     * @param radius       points of interest radius (in meters)
     * @param kinds        points of interest kinds/categories
     * @param nameSearched point of interest searched name
     * @param userId       id of the user that made the search
     * @return It will return a list of {@link PointOfInterestSearched} with at least size of 1 otherwise it will
     * return an empty list
     */
    public List<PointOfInterestSearched> getPointsOfInterestsByRadiusDB(double userLat, double userLon, int radius,
                                                                        String kinds, String nameSearched, int userId) {
        List<PointOfInterestSearched> pointsOfInterestInRadius = new ArrayList<>();
        List<PointOfInterest> pointOfInterests;
        // If user searched by all the filters (userLat, userLon, radius, kinds, name)
        if (!nameSearched.isBlank() && !kinds.isBlank()) {
            pointOfInterests = pointOfInterestRepository.findAll();
            for (PointOfInterest point : pointOfInterests) {
                double distance = calculateDistanceBetweenTwoGeoPoints(userLat, userLon, point.getLatitude(), point.getLongitude());
                if (distance <= radius) {
                    String pointNameLower = point.getName().toLowerCase();
                    String nameSearchedLower = nameSearched.toLowerCase();
                    String[] kindsSplitted = kinds.split(",");
                    boolean containsKinds = verifyIfContainsKinds(kindsSplitted, point);
                    // If the point of interest contains the name searched and if also contains 1 of the searched kinds
                    if (pointNameLower.contains(nameSearchedLower) && containsKinds) {
                        pointsOfInterestInRadius.add(PointOfInterestSearched.builder()
                                .xid(point.getXid())
                                .user_id(userId)
                                .name(point.getName())
                                .description(point.getDecription())
                                .kinds(point.getKinds())
                                .distance(distance)
                                .radius(radius)
                                .rate(point.getRate())
                                .image(point.getImage())
                                .latitude(point.getLatitude())
                                .longitude(point.getLongitude())
                                .searchDate(LocalDateTime.now().toString())
                                .build());
                    }
                }
            }
            // If user did not search by kinds but searched by name (userLat, userLon, radius, name)
        } else if (kinds.isBlank() && !nameSearched.isBlank()) {
            pointOfInterests = pointOfInterestRepository.findPointOfInterestByNameContainsIgnoreCase(nameSearched);
            pointsOfInterestInRadius = buildPointOfInterestInRadius(userLat, userLon, radius, pointOfInterests, userId);
            // If user did not search point interest name but searched for kinds (userLat, userLon, radius, kinds)
        } else if (nameSearched.isBlank() && !kinds.isBlank()) {
            pointOfInterests = pointOfInterestRepository.findAll();
            for (PointOfInterest point : pointOfInterests) {
                double distance = calculateDistanceBetweenTwoGeoPoints(userLat, userLon, point.getLatitude(), point.getLongitude());
                if (distance <= radius) {
                    String[] kindsSplitted = kinds.split(",");
                    boolean containsKinds = verifyIfContainsKinds(kindsSplitted, point);
                    // If the point of interest contains 1 of the searched kinds and not contains the name searched
                    if (containsKinds) {
                        pointsOfInterestInRadius.add(PointOfInterestSearched.builder()
                                .xid(point.getXid())
                                .user_id(userId)
                                .name(point.getName())
                                .description(point.getDecription())
                                .kinds(point.getKinds())
                                .distance(distance)
                                .radius(radius)
                                .rate(point.getRate())
                                .image(point.getImage())
                                .latitude(point.getLatitude())
                                .longitude(point.getLongitude())
                                .searchDate(LocalDateTime.now().toString())
                                .build());
                    }
                }
            }
            // If user searched only for latitude, longitude and radius (userLat, userLon, radius)
        } else {
            pointOfInterests = pointOfInterestRepository.findAll();
            pointsOfInterestInRadius = buildPointOfInterestInRadius(userLat, userLon, radius, pointOfInterests, userId);
        }

        if (pointsOfInterestInRadius.isEmpty()) {
            log.info("No points of interested found in DB");
            return pointsOfInterestInRadius;
        } else {
            /*
             Orders the points of interest with high rating and with shorter distance to the user location (user
             chosen point
             */
            Comparator<PointOfInterestSearched> byDistanceAndRate = Comparator
                    .comparing(PointOfInterestSearched::getRate)
                    .reversed()
                    .thenComparing(PointOfInterestSearched::getDistance);

            log.info("Successfully returned points of interested obtained from DB");
            return pointsOfInterestInRadius.stream()
                    .sorted(byDistanceAndRate)
                    .limit(20).toList();
        }
    }

    /**
     * Method that will return a built array of points of interests that respects certain filters
     *
     * @param userLat          user latitude
     * @param userLon          user longitude
     * @param radius           points of interest radius (in meters)
     * @param pointOfInterests list of {@link PointOfInterest} to be built
     * @param userId           id of the user that made the search
     * @return list of {@link PointOfInterestSearched} built with the information of each point of interest from
     * {@link PointOfInterest} list
     */
    private List<PointOfInterestSearched> buildPointOfInterestInRadius(double userLat, double userLon, int radius, List<PointOfInterest> pointOfInterests, int userId) {
        List<PointOfInterestSearched> pointsOfInterestInRadius = new ArrayList<>();
        for (PointOfInterest point : pointOfInterests) {
            double distance = calculateDistanceBetweenTwoGeoPoints(userLat, userLon, point.getLatitude(), point.getLongitude());
            if (distance <= radius) {
                pointsOfInterestInRadius.add(PointOfInterestSearched.builder()
                        .xid(point.getXid())
                        .user_id(userId)
                        .name(point.getName())
                        .description(point.getDecription())
                        .kinds(point.getKinds())
                        .distance(distance)
                        .radius(radius)
                        .rate(point.getRate())
                        .image(point.getImage())
                        .latitude(point.getLatitude())
                        .longitude(point.getLongitude())
                        .searchDate(LocalDateTime.now().toString())
                        .build());
            }
        }
        return pointsOfInterestInRadius;
    }

    /**
     * Method that will verify if a certain point of interest has a certain kind
     *
     * @param kinds           list of kinds
     * @param pointOfInterest point of interest to be compared with the list of kinds
     * @return point of interest searched contains a certain kind that is in the list of kinds it will return true, otherwise
     * it will return false
     */
    public boolean verifyIfContainsKinds(String[] kinds, PointOfInterest pointOfInterest) {

        int count = 0;
        for (String kind : kinds) {
            Pattern pattern = Pattern.compile("(^|,)(" + kind + ")(,|$)");
            Matcher matcher = pattern.matcher(pointOfInterest.getKinds());
            if (matcher.find()) {
                count++;
            }
        }
        return count != 0;
    }

    /**
     * Method responsible to return a list of {@link PointOfInterest} related to its xid
     *
     * @param xids list of {@link PointOfInterest} xids
     * @return if everything goes fine it will return a list of {@link PointOfInterest} with the given list of xids and
     * a 200 response, otherwise it will return an empty list and a 404 response
     */
    public ResponseEntity<List<PointOfInterest>> getPointOfInterestDetailsByListXid(List<String> xids) {
        List<PointOfInterest> pointOfInterests;
        if (!xids.isEmpty()) {
            pointOfInterests = xids.stream()
                    .map(xid -> getPointOfInterestDetails(xid).getBody())
                    .collect(Collectors.toList());
            if (pointOfInterests.isEmpty()) {
                log.info("Points of interest list is empty");
                return new ResponseEntity<>(pointOfInterests, HttpStatus.NOT_FOUND);
            }
            log.info("Points of interest list returned successfully");
            return new ResponseEntity<>(pointOfInterests, HttpStatus.OK);
        } else {
            log.info("Points of interest xid list is empty");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method responsible for calculating the distance (in meters) between 2 coordinates given their latitude and longitude
     *
     * @param userLat            user latitude
     * @param userLon            user longitude
     * @param pointOfInterestLat point of interest latitude
     * @param pointOfInterestLon point of interest longitude
     * @return distance between the 2 coordinates
     */
    public double calculateDistanceBetweenTwoGeoPoints(double userLat, double userLon, double pointOfInterestLat
            , double pointOfInterestLon) {

        // Converting degrees to radians
        double userLat1 = Math.toRadians(userLat);
        double userLon1 = Math.toRadians(userLon);
        double pointOfInterestLat1 = Math.toRadians(pointOfInterestLat);
        double pointOfInterestLon1 = Math.toRadians(pointOfInterestLon);

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

    /**
     * Method that will send points of interest searched to search-history-service
     *
     * @param exchange                 search-history-service queue's exchange
     * @param routingKey               search-history-service queue's routing key
     * @param pointsOfInterestInRadius list of {@link PointOfInterestSearched} to be sent
     */
    public void sendPointOfInterestSearchedToSearchHistoryService(String exchange, String routingKey,
                                                                  List<PointOfInterestSearched> pointsOfInterestInRadius) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.writer().withDefaultPrettyPrinter();
        try {
            template.convertAndSend(exchange, routingKey,
                    mapper.writeValueAsString(pointsOfInterestInRadius));
            log.info("Points of interest searched: " + pointsOfInterestInRadius + "were sent to the queue: "
                    + pointOfInterestSearchQueue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}