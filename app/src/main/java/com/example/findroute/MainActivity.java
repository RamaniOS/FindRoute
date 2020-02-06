package com.example.findroute;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private static final int REQUEST_CODE = 1;

    private Double latitude, longitude, dest_lat, dest_lng;
    public static boolean isDirectionRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getUserLocation();
        initMap();
    }

    public void onClick(View v) {
        Object[] data;
        switch (v.getId()) {
            case R.id.btn_rest:
                data = new Object[2];
                data[0] = mMap;
                data[1] = getURL(latitude, longitude, "restaurant");
                //
                GetNearByPlace getNearByPlace = new GetNearByPlace();
                getNearByPlace.execute(data);
                Toast.makeText(this, "Restaurant", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_distance:
            case R.id.btn_directions:
                data = new Object[3];
                data[0] = mMap;
                data[1] = getDirectionURL(latitude, longitude);
                data[2] = new LatLng(dest_lat, dest_lng);
                //
                GetDirections directions = new GetDirections();
                directions.execute(data);
                Toast.makeText(this, "Directions", Toast.LENGTH_SHORT).show();
                isDirectionRequested = v.getId() == R.id.btn_directions;
                break;
            case R.id.btn_clear:
                break;
        }
    }

    private String getDirectionURL(double lat, double lng) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        url.append("origin="+lat+","+lng);
        url.append("&destination="+dest_lat+","+dest_lng);
        url.append("&key="+getString(R.string.google_maps_key));
        return url.toString();
    }

    private String getURL(double lat, double lng, String nearBy) {
        StringBuilder placeURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        placeURL.append("location="+lat+","+lng);
        placeURL.append("&radius="+1500);
        placeURL.append("&type="+nearBy);
        placeURL.append("&key="+getString(R.string.google_maps_key));
        return placeURL.toString();
    }

    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                dest_lat = latLng.latitude;
                dest_lng =latLng.longitude;
                setMarker(latLng);
            }
        });
        if(!checkPermission())
            requestPermission();
        else
          fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void setMarker(LatLng location) {
        MarkerOptions markerOptions = new MarkerOptions().position(location)
                .title("Your Destination")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true);
        mMap.addMarker(markerOptions);
    }

    private void getUserLocation() {
        // init location client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // init location request
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
        setHomeMarker();
    }

    private boolean checkPermission() {
        int isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return isGranted == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE);
    }

    private void setHomeMarker() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //super.onLocationResult(locationResult);
                for (Location location: locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(userLocation)
                            .zoom(15)
                            .bearing(0)
                            .tilt(45)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    MarkerOptions markerOptions = new MarkerOptions().position(userLocation)
                            .title("Your Location")
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_action_marker))
                            .snippet("You are here");
                    mMap.addMarker(markerOptions);
                }
            }
        };
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setHomeMarker();
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }
}
