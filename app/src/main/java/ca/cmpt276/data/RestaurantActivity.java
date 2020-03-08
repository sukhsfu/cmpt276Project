package ca.cmpt276.data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;


public class RestaurantActivity extends AppCompatActivity {

    RestaurantManager manager = RestaurantManager.getInstance();
    Restaurant restaurant;
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
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        extractDataFromIntent();
        setupRestaurantInformation();
        populateInspectionsList();

    }

    private void setupRestaurantInformation() {
        String name = restaurant.getName().replaceAll("[^a-zA-Z0-9 &]", "");
        String address = restaurant.getAddress().replaceAll("[^a-zA-Z0-9 &]", "");
        String city = restaurant.getCity().replaceAll("[^a-zA-Z0-9 &]", "");
        String fullAddress = address + " " + city;
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

    private void populateInspectionsList() {

    }


}
