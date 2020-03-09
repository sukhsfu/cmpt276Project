package ca.cmpt276.data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;

public class InspectionActivity extends AppCompatActivity {
    RestaurantManager manager = RestaurantManager.getInstance();
    Restaurant restaurant;
    Inspection inspection;

    public static Intent makeLaunchIntent(Context context, int resPosition, int insPosition) {
        Intent intent = new Intent(context, InspectionActivity.class);
        intent.putExtra("InspectionActivity_resPos", resPosition);
        intent.putExtra("InspectionActivity_insPos", insPosition);

        return intent;
    }

    public void extractDataFromIntent() {
        Intent intent = getIntent();
        int resPosition = intent.getIntExtra("InspectionActivity_resPos", 0);
        int insPosition = intent.getIntExtra("InspectionActivity_insPos", 0);

        restaurant = manager.retrieve(resPosition);
        inspection = restaurant.getInspections().get(insPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        extractDataFromIntent();
    }
}
