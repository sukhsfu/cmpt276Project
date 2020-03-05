package ca.cmpt276.data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class RestaurantActivity extends AppCompatActivity {

    public static Intent makeLaunchIntent(Context context,int position) {
        Intent intent = new Intent(context, RestaurantActivity.class);
        intent.putExtra("position",position);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

    }


}
