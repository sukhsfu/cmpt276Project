package ca.cmpt276.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;


import static ca.cmpt276.UI.MainActivity.favourite;
import static ca.cmpt276.UI.MainActivity.restaurantList;


/**
 * The RestaurantActivity is launched from the MainActivity when a restaurant is clicked.
 * It displays details of the clicked restaurant and a list of the inspections from that restaurant.
 */
public class RestaurantActivity extends AppCompatActivity {

    private RestaurantManager manager = RestaurantManager.getInstance();
    private Restaurant restaurant;
    private Restaurant restaurantfav;
    private List<Inspection> inspections;
    private int resPosition;
    private int backIndex;
    private static final String SEARCH_TEXT = "SearchText";
    private static final String SPINNER_POS = "SpinnerPOS";
    private boolean searchPerformed = false;
    private String searchText;
    private int selectedSpinnerPOS;
    private List<Restaurant> favorites;
    List<String> favList;

    public static Intent makeLaunchIntent(Context context,int position, int index) {
        Intent intent = new Intent(context, RestaurantActivity.class);
        intent.putExtra("position",position);
        intent.putExtra("index", index);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Intent intent = getIntent();
        if(intent.hasExtra(SEARCH_TEXT) && intent.hasExtra(SPINNER_POS)){
            searchPerformed = true;
            searchText = intent.getStringExtra(SEARCH_TEXT);
            selectedSpinnerPOS = intent.getIntExtra(SPINNER_POS, 0);
        }

        extractDataFromIntent();
        setupRestaurantInformation();
        populateInspectionsListView();
        registerClickCallbackListView();
        setupGPSClickCallback();
        getFavorites();
        setupFavoriteIcon();//

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupFavoriteIcon() {
        ImageView imageView = (ImageView) findViewById(R.id.imgFavorite);

        if(isRestaurantAFavorite(restaurantfav)){
            imageView.setImageResource(R.drawable.ic_favorite_yellow_24dp);
        }else{
            imageView.setImageResource(R.drawable.ic_favorite_border_yellow_24dp);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences= v.getContext().getSharedPreferences("favourites", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.remove("favourite_"+resPosition);
                if(isRestaurantAFavorite(restaurantfav)){

                    editor.putBoolean("favourite_"+resPosition,false);
                    imageView.setImageResource(R.drawable.ic_favorite_border_yellow_24dp);
                }else{
                    editor.putBoolean("favourite_"+resPosition,true);
                    imageView.setImageResource(R.drawable.ic_favorite_yellow_24dp);
                }
                editor.apply();
                getFavorites();
            }
        });
    }

    private void getFavorites(){
        int count = 0;
        favorites = new ArrayList<>();
        for(Restaurant restaurant: manager){
            SharedPreferences sharedPreferences=getSharedPreferences("favourites",MODE_PRIVATE);
            boolean checklist=sharedPreferences.getBoolean("favourite_"+count,false);
            if(checklist){
                favorites.add(restaurant);
            }
            count++;
        }
    }

    private boolean isRestaurantAFavorite(Restaurant restaurant) {
        for (Restaurant restaurant1 : favorites) {
            if (restaurant.getTrackingNumber().equals(restaurant1.getTrackingNumber())) {
                return true;
            }
        }
        return false;
    }

    private void setupGPSClickCallback() {
        TextView tv = findViewById(R.id.txtGPS);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackingNumer = restaurant.getTrackingNumber();
                Intent intent = MapsActivity.makeLaunchIntent(RestaurantActivity.this, trackingNumer);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch (backIndex){
            case 0:
                Intent intent = new Intent(RestaurantActivity.this, MainActivity.class);
                if(searchPerformed){
                    intent.putExtra(SPINNER_POS, selectedSpinnerPOS);
                    intent.putExtra(SEARCH_TEXT, searchText);
                }
                startActivity(intent);
                break;
            case 1:
                Intent intent1 = new Intent(RestaurantActivity.this, MapsActivity.class);
                if(searchPerformed){
                    intent1.putExtra(SPINNER_POS, selectedSpinnerPOS);
                    intent1.putExtra(SEARCH_TEXT, searchText);
                }
                startActivity(intent1);
                // go back to maps activity
                break;
        }
    }

    public void extractDataFromIntent() {
        Intent intent = getIntent();
        resPosition = intent.getIntExtra("position", 0);
        backIndex = intent.getIntExtra("index", 0);
        restaurant = manager.retrieve(resPosition);
        restaurantfav=manager.retrieve(resPosition);
        inspections = restaurant.getInspections();
    }

    private void setupRestaurantInformation() {
        String name = restaurant.getName();
        String address = restaurant.getAddress();
        String city = restaurant.getCity();
        String fullAddress = address + ", " + city;
        double latitude = restaurant.getLatitude();
        double longitude = restaurant.getLongitude();
        String GPS = latitude + ", " + longitude;

        TextView resName = findViewById(R.id.item_inspectionDate);
        resName.setText(name);
        TextView resAddress = findViewById(R.id.inspectionType);
        resAddress.setText(fullAddress);
        TextView resGPS = findViewById(R.id.txtGPS);
        resGPS.setText(GPS);
    }

    private void populateInspectionsListView() {
        // Build adapter
        ArrayAdapter<Inspection> adapter = new myListAdapter();
        ListView list = findViewById(R.id.inspectionsListView);
        list.setAdapter(adapter);
    }

    private void registerClickCallbackListView() {
        ListView list = findViewById(R.id.inspectionsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Inspection clickedInspection = inspections.get(position);
                Intent intent = InspectionActivity.makeLaunchIntent(RestaurantActivity.this, resPosition, position);
                startActivity(intent);
            }
        });
    }



    private class myListAdapter extends ArrayAdapter<Inspection> {
        public myListAdapter() {
            super(RestaurantActivity.this, R.layout.inspections_item_view, inspections);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.inspections_item_view, parent, false);
            }

            Inspection currInspection = inspections.get(position);

            // get hazard level to set icon
            String hazardLevel = currInspection.getHazardLevel().replaceAll("[^a-zA-Z0-9 &]", "");
            ImageView iconView = itemView.findViewById(R.id.item_icon);
            Log.d("RestaurantActivity", "Hazard Level: " + hazardLevel);
            if (hazardLevel.equalsIgnoreCase("low") ) {
                iconView.setImageResource(R.drawable.smile);
                iconView.setColorFilter(Color.GREEN);
            }
            else if (hazardLevel.equalsIgnoreCase("moderate") ) {
                iconView.setImageResource(R.drawable.normal);
                iconView.setColorFilter(Color.YELLOW);

            } else if (hazardLevel.equalsIgnoreCase("high")){
                iconView.setImageResource(R.drawable.sad);
                iconView.setColorFilter(Color.RED);
            }

            itemView.setBackgroundColor(Color.rgb(231,231,231));
            itemView.setPadding(50,50,50,50);

            // set date
            String dateFormatted = MainActivity.dateDifference(currInspection.getDate());
            TextView dateTxt = itemView.findViewById(R.id.item_inspectionDate);
            dateTxt.setText(dateFormatted);

            // set critical & non-critical issues
            TextView critText = itemView.findViewById(R.id.item_numCritIssues);
            critText.setText(getString(R.string.inspection_setNumCrit, currInspection.getNumCriticalIssues()));

            TextView nonCritText = itemView.findViewById(R.id.item_numNonCritIssues);
            nonCritText.setText(getString(R.string.inspection_setNumNonCrit, currInspection.getNumNonCriticalIssues()));

            return itemView;
        }
    }


}

