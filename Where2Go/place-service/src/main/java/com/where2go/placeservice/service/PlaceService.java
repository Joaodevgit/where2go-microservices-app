package com.where2go.placeservice.service;

import com.where2go.placeservice.dto.Place;
import com.where2go.placeservice.dto.PlaceId;
import com.where2go.placeservice.repository.PlaceRepository;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PlaceService {
    @Autowired
    private PlaceRepository placeRepository;
    @Value("${api.key.geocode.name}")
    private String geocodeAPIKey;

    /**
     * Method that in first instance will make a request to Geocode.xyz API and if the request results in a null value it
     * will request the searched place (given place search result) to the DB
     *
     * @param placeSearch result of the place searched
     * @return response 200 with {@link Place} that corresponds to the given place search result, otherwise it will
     * return response 404 with null value
     */
    public ResponseEntity<Place> getPlaceBySearchName(String placeSearch) {
        String[] placeSplitted = placeSearch.split(",");

        Place place = getPlaceBySearchNameAPI(placeSearch).getBody();
        if (place != null) {
            log.info("Trying to get place with search result: " + placeSearch + " by requesting it to Geocode.xyz API...");
            return getPlaceBySearchNameAPI(placeSearch);
        } else {
            log.info("Trying to get place: " + placeSearch + " in DB...");
            Place placeDB = placeRepository.findByNameAndCountryLetter(placeSplitted[0], placeSplitted[1]);
            if (placeDB != null) {
                log.info("Place: " + placeDB + " found in DB");
                return new ResponseEntity<>(placeDB, HttpStatus.OK);
            } else {
                log.info("Place doesn't exists (Not found nor in DB nor in Geocode.xyz API");
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        }
    }

    /**
     * Method responsible for making a GET request to Geocode.xyz API given place search result in order to return
     * a {@link Place}
     *
     * @param placeSearch result of the place searched
     * @return response 200 with {@link Place} that corresponds to the given place search result, otherwise it will
     * return response 404 with null value
     */
    public ResponseEntity<Place> getPlaceBySearchNameAPI(String placeSearch) {
        // Timeout of 10s to connect to the external API and 30s to get all the response from it
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("https://free.geocode.xyz/" + placeSearch + "?json=1&auth=" + geocodeAPIKey)
                .build();

        Response response;
        JSONObject obj;
        try {
            response = client.newCall(request).execute();
            obj = new JSONObject(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (obj.has("city")) {
            log.info("Response with geocode api successful");

            JSONObject standardObj = obj.getJSONObject("standard");
            PlaceId placeId = PlaceId.builder()
                    .latitude(Double.parseDouble(obj.getString("latt")))
                    .longitude(Double.parseDouble(obj.getString("longt")))
                    .build();
            Place place = Place.builder()
                    .placeId(placeId)
                    .name(standardObj.getString("city"))
                    .countryLetter(standardObj.getString("prov"))
                    .build();

            return new ResponseEntity<>(place, HttpStatus.OK);
        }
        log.info("Place with search result: " + placeSearch + " doesn't exist on API");
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    /**
     * Method that in first instance will make a request to Geocode.xyz API and if the request results in a null value it
     * will request the searched place (given latitude and longitude) to the DB
     *
     * @param userLat user latitude
     * @param userLon user longitude
     * @return response 200 with {@link Place} that corresponds to the given latitude and longitude, otherwise it will
     * return response 404 with null value
     */
    public ResponseEntity<Place> getPlaceByUserLatAndUserLon(double userLat, double userLon) {

        ResponseEntity<Place> placeAPI = getPlaceByUsernameLatLonAPI(userLat, userLon);

        if (placeAPI.getBody() != null) {
            return placeAPI;
        } else {
            log.info("Trying to get place with latitude " + userLat + "and longitude" + userLon + " in DB...");
            Place placeDB = placeRepository.findByLatitudeAndLongitude(userLat, userLon);
            if (placeDB != null) {
                log.info("User latitude and longitude found on DB");
                return new ResponseEntity<>(placeDB, HttpStatus.OK);
            } else {
                log.info("Place with latitude " + userLat + "and longitude" + userLon + " not found on DB");
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        }
    }

    /**
     * Method responsible for making a GET request to Geocode.xyz API given user latitude and longitude in order to return
     * a {@link Place}
     *
     * @param userLat user latitude
     * @param userLon user longitude
     * @return response 200 with {@link Place} that corresponds to the given latitude and longitude, otherwise it will
     * return response 404 with null value
     */
    public ResponseEntity<Place> getPlaceByUsernameLatLonAPI(double userLat, double userLon) {
        // Timeout of 10s to connect to the external API and 30s to get all the response from it
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("https://free.geocode.xyz/" + userLat + "," + userLon +
                        "?json=1&auth=" + geocodeAPIKey)
                .build();

        Response response;
        JSONObject obj;
        try {
            response = client.newCall(request).execute();
            obj = new JSONObject(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (obj.has("city")) {
            log.info("Response with geocode api successful");

            String city = obj.get("city").toString();
            String countryLetter = obj.get("prov").toString();
            PlaceId placeId = PlaceId.builder()
                    .latitude(userLat)
                    .longitude(userLon)
                    .build();
            Place place = Place.builder()
                    .placeId(placeId)
                    .name(city)
                    .countryLetter(countryLetter)
                    .build();

            return new ResponseEntity<>(place, HttpStatus.OK);
        }
        log.info("Place with latitude: " + userLat + " and longitude:" + userLon + " doesn't exist on API");
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

}
