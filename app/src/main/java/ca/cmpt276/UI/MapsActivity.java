package ca.cmpt276.UI;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
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

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_REQUEST_CODE = 1000;
    private static final float DEFAULT_ZOOM = 15;
    public static final String TAG = "mapsActivity";
    public static final String POSITION = "position";
    private GoogleMap mMap;
    RestaurantManager manager = RestaurantManager.getInstance();

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean locationPermissionGranted = false;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public static Intent makeLaunchIntent(Context context, int position) {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra(POSITION,position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = new Intent(this, ReadDataService.class);
        startService(intent);

        Toast.makeText(this, "On Create called", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        initMap();
        getLocationPermission();
        setupSwitchButton();

        // user location updates
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

                        LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());
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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "On Ready called", Toast.LENGTH_SHORT).show();
        Marker marker;
        mMap = googleMap;


        for (Restaurant restaurant : manager) {
            marker = addMarker(restaurant);
            Toast.makeText(this, marker.toString(), Toast.LENGTH_SHORT).show();
            marker.showInfoWindow();
        }

        if(locationPermissionGranted){
            getUserLocation();

            mMap.setMyLocationEnabled(true);
            //mMap.getUiSettings().setAllGesturesEnabled(true);
        }

        if(getIntent().hasExtra(POSITION)){
            int resId = getIntent().getIntExtra(POSITION, 0);
            Restaurant restaurant = manager.retrieve(resId);
            Log.d(TAG, restaurant.toString());
            moveCamera(new LatLng(restaurant.getLatitude(), restaurant.getLongitude()), DEFAULT_ZOOM);
            Log.d(TAG, "After Move Camera");

            launchInfoWindow(restaurant);
            Toast.makeText(this, "index is" + resId, Toast.LENGTH_SHORT).show();
        }

        setupInfoWindows();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng latlng = marker.getPosition();
                Restaurant restaurant = findRestaurantInListFromLatLng(latlng);
                int position = manager.getIndex(restaurant);
                Intent intent=RestaurantActivity.makeLaunchIntent(MapsActivity.this,position, 1);
                startActivity(intent);
            }
        });

    }

    private Marker addMarker(Restaurant restaurant){
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

        Marker marker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(displayRestaurant));
        return marker;
    }

    private void setupInfoWindows() {
        if (mMap!=null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View row = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                    TextView name = row.findViewById(R.id.resName);
                    TextView address = row.findViewById(R.id.resAddress);
                    TextView hazardLevel = row.findViewById(R.id.resHazardLevel);

                    LatLng latlng = marker.getPosition();
                    Restaurant res = findRestaurantInListFromLatLng(latlng);
                    String hazardLev;
                    if (res.getInspections().isEmpty()) {
                        hazardLev = "No inspections found.";
                    } else {
                        Inspection inspection = getMostRecentInspection(res);
                        hazardLev = "Hazard Level: " + inspection.getHazardLevel().replaceAll("[^a-zA-Z0-9 &]", "");
                    }

                    name.setText(res.getName());
                    address.setText(res.getAddress());
                    hazardLevel.setText(hazardLev);
                    return row;
                }
            });
        }

    }

    public void launchInfoWindow(Restaurant restaurant){

        Marker marker = addMarker(restaurant);
        marker.showInfoWindow();
        Toast.makeText(this, "show Info window on", Toast.LENGTH_SHORT).show();
    }

    private Restaurant findRestaurantInListFromLatLng(LatLng latLng) {
        for (Restaurant restaurant:manager) {
            if (restaurant.getLatitude()==latLng.latitude && restaurant.getLongitude()==latLng.longitude) {
                return restaurant;
            }
        }
        return null;
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
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void setupSwitchButton() {
        Button btnSwitch = (Button) findViewById(R.id.btnSwitch);

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, MainActivity.class));
            }
        });
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
                    getUserLocation();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void getUserLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(locationPermissionGranted) {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location curLocation = task.getResult();
                        moveCamera(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), DEFAULT_ZOOM);
                    }
                }
            });
        }
    }

    private void moveCamera (LatLng latlng, float zoom){
        Log.d(TAG, "move Camera: moving the camera to: lat: " + latlng.latitude + " long: " + latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }

}
