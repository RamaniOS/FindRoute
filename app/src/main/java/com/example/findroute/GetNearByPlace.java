package com.example.findroute;

import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class GetNearByPlace extends AsyncTask<Object, String, String> {
    GoogleMap googleMap;
    String placeData;
    String url;

    @Override
    protected String doInBackground(Object... objects) {
        googleMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        try {
            String data = GoogleStore.readURL(url);
            if (data != null) {
                placeData = data;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return placeData;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            List<Place> places = DataParser.getPlaces(s);
            showMarkers(places);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMarkers(List<Place> places) {
        for (int i = 0; i<places.size(); i++) {
            Place place = places.get(i);

            LatLng location = new LatLng(Double.valueOf(place.latitude), Double.valueOf(place.longitude));
            MarkerOptions markerOptions = new MarkerOptions().position(location)
                    .title(place.name + " : " + place.vicinity)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            googleMap.addMarker(markerOptions);
        }
    }
}
