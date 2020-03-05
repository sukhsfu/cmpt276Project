package ca.cmpt276.data;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements jadapter.OnNoteListener {
    private List<String> restaurant = new ArrayList<>();
    private List<InspectionSample> inspectionSamples = new ArrayList<>();
    private List<RestaurantSample> restaurantSamples = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readRestaurantData();
        readInspectionData();
        setupRestaurantInList();
//
//        TextView textview = (TextView) findViewById(R.id.test);
//        textview.setText("TrackingNumber" + inspectionSamples);
    }

    private void readInspectionData() {
        InputStream is = getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                Log.d("MyActivityIns", "Line:" + line);

                String[] tokens = line.split(",");
                InspectionSample sample = new InspectionSample();
                sample.setTrackingNumber(tokens[0]);
                sample.setInspectionDate(Integer.parseInt(tokens[1]));
                sample.setInspType(tokens[2]);
                sample.setNumCritical(Integer.parseInt(tokens[3]));
                sample.setNumNonCritical(Integer.parseInt(tokens[4]));
                sample.setHazardRating(tokens[5]);
                if(tokens.length >= 7 && tokens[6].length() > 0) {
                    sample.setViolLump(tokens[6]);
                }
                else{
                    sample.setViolLump("None");
                }

                inspectionSamples.add(sample);

                Log.d("MyActivityIns", "Just created" + sample);

            }
        }catch (IOException e) {
            Log.wtf("MyActivityIns", "Error reading data file on line" + line, e);
            e.printStackTrace();

        }


    }

    private void readRestaurantData() {
        InputStream is = getResources().openRawResource(R.raw.restaurants_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                Log.d("MyActivity", "Line:" + line);

                String[] tokens = line.split(",");
                RestaurantSample sample = new RestaurantSample();
                sample.setTrcackingNumber(tokens[0]);
                sample.setName(tokens[1]);
                sample.setAddress(tokens[2]);
                sample.setPhysicalcity(tokens[3]);
                sample.setFaceType(tokens[4]);

                sample.setLat(Double.parseDouble(tokens[5]));
                sample.setLon(Double.parseDouble(tokens[6]));
                restaurantSamples.add(sample);

                Log.d("MyActivity", "Just created" + sample);

            }
        }catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();

        }


        for(int i=0;i<restaurantSamples.size();i++){
            restaurant.add(restaurantSamples.get(i).toString());

        }
    }

    private void setupRestaurantInList() {

        RecyclerView list=(RecyclerView) findViewById(R.id.mainrecyleview);
        list.setHasFixedSize(true);
        list.setItemViewCacheSize(20);
        list.setDrawingCacheEnabled(true);
        list.setNestedScrollingEnabled(false);
        list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new jadapter(restaurant,  this));


    }

    @Override
    public void onNoteClick(int position) {

        Intent intent=RestaurantActivity.makeLaunchIntent(MainActivity.this,position);
        startActivity(intent);

    }
}

