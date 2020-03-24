package ca.cmpt276.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;

/**
 * The RestaurantActivity is launched from the MainActivity when a restaurant is clicked.
 * It displays details of the clicked restaurant and a list of the inspections from that restaurant.
 */
public class RestaurantActivity extends AppCompatActivity {

    RestaurantManager manager = RestaurantManager.getInstance();
    Restaurant restaurant;
    List<Inspection> inspections;
    int resPosition;
    int backIndex;

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

        extractDataFromIntent();
        setupRestaurantInformation();
        populateInspectionsListView();
        registerClickCallbackListView();
        setupGPSClickCallback();
    }

    private void setupGPSClickCallback() {
        TextView tv = findViewById(R.id.txtGPS);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MapsActivity.makeLaunchIntent(RestaurantActivity.this, resPosition);
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
                startActivity(new Intent(RestaurantActivity.this, MainActivity.class));
                break;
            case 1:
                startActivity(new Intent(RestaurantActivity.this, MapsActivity.class));
                // go back to maps activity
                break;
        }
    }

    public void extractDataFromIntent() {
        Intent intent = getIntent();
        resPosition = intent.getIntExtra("position", 0);
        backIndex = intent.getIntExtra("index", 0);
        restaurant = manager.retrieve(resPosition);
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
            critText.setText(getString(R.string.inspection_setNumCrit, currInspection.getNumCriticalIssues()));
            nonCritText.setText(getString(R.string.inspection_setNumNonCrit, currInspection.getNumNonCriticalIssues()));

            return itemView;
        }
    }
}
