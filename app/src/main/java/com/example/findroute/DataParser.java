package com.example.findroute;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataParser {

    public static List<Place> getPlaces(String jsonData) throws JSONException {

        JSONArray jsonArray = null;
        JSONObject json = new JSONObject(jsonData);

        jsonArray = json.getJSONArray("results");

        List<Place> places = new ArrayList<>();

        for (int i = 0; i<jsonArray.length(); i++) {

            String placeName = "N/A";
            String vicinity = "N/A";
            String latitude = "";
            String longtude = "";
            String refrence = "";
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                if (!jsonObject.isNull("name")) {
                    placeName = jsonObject.getString("name");
                }
                if (!jsonObject.isNull("vicinity")) {
                    vicinity = jsonObject.getString("vicinity");
                }
                JSONObject geometry = jsonObject.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                latitude = location.getString("lat");
                longtude = location.getString("lng");
                refrence = jsonObject.getString("reference");

                Place place = new Place(placeName, vicinity, latitude, longtude, refrence);


                Log.d("arr", "places" + place);
                places.add(place);

            } catch (JSONException e) {
                e.getStackTrace();
            }

        }
        return  places;
    }

}
