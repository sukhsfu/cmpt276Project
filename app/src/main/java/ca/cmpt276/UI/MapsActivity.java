package ca.cmpt276.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;
import ca.cmpt276.model.Violation;
import ca.cmpt276.model.ViolationManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * MapsActivity displays Google map centered to user's location and has markers for every restaurant
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_REQUEST_CODE = 1000;
    private static final float DEFAULT_ZOOM = 15;
    public static final String TAG = "mapsActivity";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lng";
    private static final String SEARCH_TEXT = "SearchText";
    private static final String SPINNER_POS = "SpinnerPOS";
    private GoogleMap mMap;
    RestaurantManager manager = RestaurantManager.getInstance();
    private SearchView searchView;
    private int selectedSpinnerPOS = 0;
    private boolean searchPerformed = false;
    private String searchText;
    private Spinner spinner;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean locationPermissionGranted = false;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    String last_modified="";
    String last_modified2;

    String url = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";

    JSONObject obj;

    public static Intent makeLaunchIntent(Context context, double lat, double lng) {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra(LATITUDE, lat);
        intent.putExtra(LONGITUDE, lng);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = new Intent(this, ReadDataService.class);
        startService(intent);
        setupBriefDescriptions();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            int request_code = 0;

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_code);
        } else {
            final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, "time.txt");

            if (!file.exists() || file.length() == 0) {

                try {
                    file.createNewFile();
                    openUpdate();
                } catch (IOException e) {

                }

            } else {
                comparetime();
            }
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
                            moveCamera(curLocation);
                        }
                    }
                }
            };
        }

        spinner = (Spinner) findViewById(R.id.mapSpinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.menu_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        searchView = findViewById(R.id.mapSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String text = searchView.getQuery().toString();
                if(text != null || !text.equals("")){
                    searchText = text;
                    searchPerformed = true;
                    updateMarkers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("") || newText == null){
                    mMap.clear();
                    populateAllMarkers();
                    searchPerformed = false;
                }
                return false;
            }
        });
    }

    private void setupBriefDescriptions() {
        for (Restaurant restaurant : manager) {
            for (Inspection inspection : restaurant.getInspections()) {
                for (Violation violation : inspection.getViolations() ) {
                    ViolationManager violationManager = violation.getManager();
                    violationManager.populateBriefDescriptions();
                    violation.setBriefDescription( violationManager.retrieve(violation.getType()) );
                }
            }
        }
    }


    private void initMap(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();

        if(intent.hasExtra(SPINNER_POS) && intent.hasExtra(SEARCH_TEXT)){
            searchText = intent.getStringExtra(SEARCH_TEXT);
            searchPerformed = true;
            selectedSpinnerPOS = intent.getIntExtra(SPINNER_POS, 0);
            updateMarkers();
            //spinner.setSelection(selectedSpinnerPOS);
            //searchView.setQuery(searchText, false);
        }else{
            for (Restaurant restaurant : manager) {
                addMarker(restaurant);
            }
        }

        if (intent.hasExtra(LATITUDE)) {
            if(intent.hasExtra(LONGITUDE)){
                double lat = intent.getDoubleExtra(LATITUDE, 0.0);
                double lng = intent.getDoubleExtra(LONGITUDE, 0.0);
                LatLng latlng = new LatLng(lat, lng);
                Restaurant restaurant = findRestaurantInListFromLatLng(latlng);
                moveCamera(latlng);
                Log.d("Moving camera to ","restaurant");
                launchInfoWindow(restaurant);
            }
        } else {
            if (locationPermissionGranted) {
                getUserLocation();
            }
        }

        // show blue dot for user's current location
        mMap.setMyLocationEnabled(true);

        setupInfoWindows();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng latlng = marker.getPosition();
                Restaurant restaurant = findRestaurantInListFromLatLng(latlng);
                int position = manager.getIndex(restaurant);
                Intent intent=RestaurantActivity.makeLaunchIntent(MapsActivity.this,position, 1);
                if(searchPerformed){
                    intent.putExtra(SEARCH_TEXT, searchText);
                    intent.putExtra(SPINNER_POS, selectedSpinnerPOS);
                }
                startActivity(intent);
            }
        });
    }

    private void populateAllMarkers(){
        for (Restaurant restaurant : manager) {
            addMarker(restaurant);
        }
    }

    private void updateMarkers(){
        switch(selectedSpinnerPOS){
            case 0:
                updateMarkersByName(searchText);
                break;
            case 1:
                updateMarkersByHazard(searchText);
                break;
            case 2:
                updateMarkersByViolation(searchText);
                break;
            case 3:
                updateMarkersByFavorite(searchText);
                break;
            case 4:
                updateMarkersByCombined(searchText);
                break;
        }
    }

    private void updateMarkersByName(String name){
        mMap.clear();
        for(Restaurant restaurant: manager){
            if(restaurant.getName().toLowerCase().contains(name.toLowerCase())){
                addMarker(restaurant);
            }
        }
        setupInfoWindows();
    }

    private void updateMarkersByHazard(String searchText){
        mMap.clear();
        for(Restaurant restaurant: manager){
            Inspection inspection = getMostRecentInspection(restaurant);
            if(inspection != null){
                if(inspection.getHazardLevel().toLowerCase().equals(searchText.toLowerCase())){
                    addMarker(restaurant);
                }
            }
        }
        setupInfoWindows();
    }

    private void updateMarkersByViolation(String searchText){
        mMap.clear();
        String search = searchText.toLowerCase();
        String searchParam="";
        if(search.contains("less") || search.contains("<")){
            searchParam = "less";
        }else if(search.toLowerCase().contains("more") || search.contains("greater") || search.contains(">")){
            searchParam = "more";
        }

        if(searchParam != ""){
            final StringBuilder sb = new StringBuilder(searchText.length());
            for(int i = 0; i < searchText.length(); i++){
                final char c = searchText.charAt(i);
                if(c > 47 && c < 58){
                    sb.append(c);
                }
            }
            int numViolations = Integer.parseInt(sb.toString());
            for(Restaurant restaurant: manager){
                int num = getRecentNumCriticalViolations(restaurant);
                switch(searchParam){
                    case "less":
                        if(num <= numViolations){
                            addMarker(restaurant);
                        }
                        break;
                    case "more":
                        if(num >= numViolations){
                            addMarker(restaurant);
                        }
                        break;
                    default:
                        break;
                }
            }
            setupInfoWindows();
        }
    }

    private void updateMarkersByFavorite(String searchText){
        //TODO
    }

    private void updateMarkersByCombined(String searchText){
        //TODO
    }

    private Marker addMarker(Restaurant restaurant){
        LatLng displayRestaurant = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
        MarkerOptions options = new MarkerOptions();
        options.position(displayRestaurant);
        options.title(restaurant.getName());
        String snippet;
        if (restaurant.getInspections().isEmpty()) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            snippet = getString(R.string.maps_markerSnippet_noInsp, restaurant.getAddress());
            options.snippet(snippet);
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
            snippet = getString(R.string.maps_markerSnippet, restaurant.getAddress(), hazardLev);
            options.snippet(snippet);
        }

        //setupInfoWindows();
        return mMap.addMarker(options);
    }

    public void openUpdate() {
        Fragment Update;
        FragmentManager FM=getSupportFragmentManager();
        Update =FM.findFragmentByTag("example dialog");
        if(Update==null){
            FragmentTransaction FT=FM.beginTransaction();
            Update = new update();
            FT.add(Update,"example dialog");
            FT.commit();
        }

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
                    if ( res.getInspections().isEmpty() ) {
                        hazardLev = getString(R.string.maps_infoWin_noInsp);
                    } else {
                        Inspection inspection = getMostRecentInspection(res);
                        hazardLev = getString(R.string.maps_infoWin_hazLev ,inspection.getHazardLevel().replaceAll("[^a-zA-Z0-9 &]", ""));
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
        if(!restaurant.getInspections().isEmpty()){
            Inspection inspectionReturn = restaurant.getInspections().get(0);
            for (Inspection inspection : restaurant.getInspections()) {
                if (inspection.getDate().compareTo(inspectionReturn.getDate()) > 0) {
                    inspectionReturn = inspection;
                }
            }
            return inspectionReturn;
        }else{
            return null;
        }

    }

    private int getRecentNumCriticalViolations(Restaurant restaurant){
        int numCriticalViolations = 0;
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, -1);
        Date lastYear = cal.getTime();

        List<Inspection> inspections = restaurant.getInspections();
        for(Inspection inspection: inspections){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            try{
                Date date = formatter.parse(inspection.getDate());
                if(date.after(lastYear)){
                    numCriticalViolations = numCriticalViolations + inspection.getNumCriticalIssues();
                }
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return numCriticalViolations;
    }

    public String readtime(){  //read local_time in file and last_modified in file
        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "time.txt");
        String line="";
        try{
        InputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );

            line=reader.readLine();
            last_modified2=reader.readLine();
            return line;}
        catch (IOException e){
            System.out.println("error occured in reading data in readtime");
            e.printStackTrace();
        }

      return line;

    }

    public void comparetime(){
        String filetime=readtime();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();

                    try {
                        obj = new JSONObject(myResponse);

                    } catch (Throwable t) {

                    }
                    try {
                        last_modified= obj.getJSONObject("result").getJSONArray("resources").getJSONObject(0).getString("last_modified");
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy'-'MM'-'dd'T'hh:mm");
                        LocalDateTime today = LocalDateTime.now();
                        String todaystring = formatter.format(today);
                        String a1=todaystring.substring(8,10)+todaystring.substring(11,13)+todaystring.substring(14,16);
                        String  b1=filetime.substring(8,10)+filetime.substring(11,13)+filetime.substring(14,16);

                        int a=Integer.parseInt(a1);//local time
                        int b=Integer.parseInt(b1);//lastmodified from server.
                        int c=a%100+((a%10000)/100)*60+(a/10000)*24*60;//local_time
                        int d=b%100+((b%10000)/100)*60+(b/10000)*24*60;//lastmodified from server.

                        if((c-d)>=(20*60))
                        {


                            if(last_modified.equals(last_modified2)) {

                            }
                            else{

                                openUpdate();
                            }

                        }
                        else{

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void setupSwitchButton() {
        Button btnSwitch = findViewById(R.id.btnSwitch);

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                if(searchPerformed){
                    intent.putExtra(SPINNER_POS, selectedSpinnerPOS);
                    intent.putExtra(SEARCH_TEXT, searchText);
                }
                startActivity(intent);
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
                } else {
                    Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
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
                        moveCamera(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()));
                    }
                }
            });
        }
    }

    private void moveCamera(LatLng latlng){
        Log.d(TAG, "move Camera: moving the camera to: lat: " + latlng.latitude + " long: " + latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, MapsActivity.DEFAULT_ZOOM));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //String item = parent.getItemAtPosition(position).toString();
        //Toast.makeText(this, "Position is: " + position, Toast.LENGTH_SHORT).show();
        selectedSpinnerPOS = position;
        switch (selectedSpinnerPOS){
            case 0:
                searchView.setQueryHint("Pizza");
                //searchView.clearFocus();
                break;
            case 1:
                searchView.setQueryHint("Low");
                break;
            case 2:
                searchView.setQueryHint("Less than 10");
                break;
            case 3:
                searchView.setQueryHint("All favorites");
                // TODO: populate markers to be favorites only
                break;
            case 4:
                searchView.setQueryHint("Favorite, pizza, low, 5 or less");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
