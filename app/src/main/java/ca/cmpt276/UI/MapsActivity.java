package ca.cmpt276.UI;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_REQUEST_CODE = 1000;
    private static final float DEFAULT_ZOOM = 15;
    public static final String TAG = "mapsActivity";
    private GoogleMap mMap;
    RestaurantManager manager = RestaurantManager.getInstance();

    //private FusedLocationProviderClient mLocationClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private boolean locationPermissionGranted = false;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (Restaurant restaurant : manager) {
            LatLng displayRestaurant = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            MarkerOptions options = new MarkerOptions();
            options.position(displayRestaurant);
            options.title(restaurant.getName());
            String snippet;
            if (restaurant.getInspections().isEmpty()) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                snippet = "Address: " + restaurant.getAddress() + "\nNo Inspection Data.";
            } else {
                Inspection inspection = getMostRecentInspection(restaurant);
                String hazardLev = inspection.getHazardLevel().replaceAll("[^a-zA-Z0-9 &]", "");
                Log.d(TAG, inspection.toString());

                if (hazardLev.equalsIgnoreCase("low")) {
                    options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.low_hazard_green_check));
                } else if (hazardLev.equalsIgnoreCase("moderate")) {
                    options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.non_critical_icon));
                } else if (hazardLev.equalsIgnoreCase("high")){
                    options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.critical_icon));
                }
                snippet = "Address: " + restaurant.getAddress() + "\nHazard Level: " + hazardLev;
            }
            options.snippet(snippet);

            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(displayRestaurant));
        }

        if(locationPermissionGranted){
            getUserLocation();


            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED){
                return;
            }
            mMap.setMyLocationEnabled(true);
            //mMap.getUiSettings().setAllGesturesEnabled(true);
        }
    }

    private Inspection getMostRecentInspection(Restaurant restaurant) {
        Inspection inspectionReturn = restaurant.getInspections().get(0);
        for (Inspection inspection : restaurant.getInspections()) {
            if (inspection.getDate().compareTo(inspectionReturn.getDate()) > 0) {
                inspectionReturn = inspection;
            }

        }
        return inspectionReturn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        initMap();
        getLocationPermission();


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        LatLng curLocation = new LatLng(wayLatitude, wayLongitude);
                        if(mFusedLocationProviderClient != null){
                            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                        }
                        moveCamera(curLocation, DEFAULT_ZOOM);
                    }
                }
            }
        };

    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                locationPermissionGranted = true;
                initMap();
                getUserLocation();
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    //initMap();
                    //getUserLocation();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }



    private void getUserLocation(){

        if(locationPermissionGranted){
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Location curLocation = (Location) task.getResult();
                        moveCamera(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), DEFAULT_ZOOM);
                    }
                }
            });
        }

        if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // reuqest for permission
            return;
        }
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();
                    LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    moveCamera(curLocation, DEFAULT_ZOOM);
                }
            }
        });

    }

    private void moveCamera (LatLng latlng, float zoom){
        Log.d(TAG, "move Camera: moving the camera to: lat: " + latlng.latitude + " long: " + latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));

    }

}
