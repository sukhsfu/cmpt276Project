package ca.cmpt276.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;


public class RestaurantActivity extends AppCompatActivity {

    RestaurantManager manager = RestaurantManager.getInstance();
    Restaurant restaurant;
    List<Inspection> inspections;
    int position;

    public static Intent makeLaunchIntent(Context context,int position) {
        Intent intent = new Intent(context, RestaurantActivity.class);
        intent.putExtra("position",position);
        return intent;
    }

    public void extractDataFromIntent() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        restaurant = manager.retrieve(position);
        inspections = restaurant.getInspections();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        extractDataFromIntent();
        setupRestaurantInformation();
        populateInspectionsListView();


    }

    private void setupRestaurantInformation() {
        String name = restaurant.getName();
        String address = restaurant.getAddress();
        String city = restaurant.getCity();
        String fullAddress = address + ", " + city;
        double latitude = restaurant.getLatitude();
        double longitude = restaurant.getLongitude();
        String GPS = latitude + ", " + longitude;

        TextView resName = findViewById(R.id.restaurantName);
        resName.setText(name);
        TextView resAddress = findViewById(R.id.restaurantAddress);
        resAddress.setText(fullAddress);
        TextView resGPS = findViewById(R.id.restaurantGPS);
        resGPS.setText(GPS);
    }

    private void populateInspectionsListView() {

        // Build adapter
        ArrayAdapter<Inspection> adapter = new myListAdapter();
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


            return itemView;
        }
    }



}
