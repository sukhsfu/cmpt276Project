package ca.cmpt276.data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;


public class RestaurantActivity extends AppCompatActivity {

    RestaurantManager manager;
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
        restaurant=manager.retrieve(position);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);


        extractDataFromIntent();
        populateInspectionsList();

    }

    private void populateInspectionsList() {

    }


}
