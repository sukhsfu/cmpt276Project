package ca.cmpt276.data;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;
import ca.cmpt276.model.Violation;

public class MainActivity extends AppCompatActivity implements jadapter.OnNoteListener {
    private List<String> restaurants = new ArrayList<>();
    private List<InspectionSample> inspectionSamples = new ArrayList<>();
    private List<RestaurantSample> restaurantSamples = new ArrayList<>();

    private RestaurantManager manager = RestaurantManager.getInstance();

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
                String name = tokens[1];
                String address = tokens[2];
                String city = tokens[3];
                double latitude = Double.parseDouble(tokens[5]);
                double longitude = Double.parseDouble(tokens[6]);

                Restaurant sample = new Restaurant(name, address, city, latitude, longitude);
                manager.addRestaurant(sample);

                //restaurantSamples.add(sample);

                Log.d("MyActivity", "Just created" + sample);

            }
        }catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();

        }


//        for(int i=0;i<restaurantSamples.size();i++){
//            restaurants.add(restaurantSamples.get(i).toString());
//
//        }
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

                String trackingNum = tokens[0];
                String date = tokens[1];
                String type = tokens[2];
                int numCritical = Integer.parseInt(tokens[3]);
                int numNonCritical = Integer.parseInt(tokens[4]);
                String hazardRating = tokens[5];

                Inspection sample = new Inspection(trackingNum, date, type, numCritical, numNonCritical, hazardRating);

                if(tokens.length >= 7 && tokens[6].length() > 0) {
                    if (!tokens[6].contains("|")) {
                        // there is only one violation
                        String[] indivViol = tokens[6].split(",");
                        // PROBLEM HERE ^,  03/05 ~2:00
                        int violType = Integer.parseInt(indivViol[1]);
                        String severity = indivViol[2];
                        String detailedDescrip = indivViol[3];
                        boolean isRepeat;
                        if (indivViol.length >= 4) {
                            // not repeat
                            isRepeat = false;
                        }
                        else {
                            isRepeat = true;
                        }

                        Violation violation = new Violation(violType, severity, detailedDescrip, isRepeat);
                        sample.addViolation(violation);

                    }

                    String[] violations = tokens[6].split("|");
                    for (int i = 0; i < violations.length; i++ ) {
                        String[] indivViol = violations[i].split(",");

                        int violType = Integer.parseInt(indivViol[1]);
                        String severity = indivViol[2];
                        String detailedDescrip = indivViol[3];
                        boolean isRepeat;
                        if (indivViol.length >= 4) {
                            // not repeat
                            isRepeat = false;
                        }
                        else {
                            isRepeat = true;
                        }

                        Violation violation = new Violation(violType, severity, detailedDescrip, isRepeat);
                        sample.addViolation(violation);

                    }
                    //sample.setViolLump(tokens[6]);
                }
                else{
                    //sample.setViolLump("None");
                }

                //inspections.add(sample);

                Log.d("MyActivityIns", "Just created" + sample);

            }
        }catch (IOException e) {
            Log.wtf("MyActivityIns", "Error reading data file on line" + line, e);
            e.printStackTrace();

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
        list.setAdapter(new jadapter(restaurants,  this));


    }

    @Override
    public void onNoteClick(int position) {

        Intent intent=RestaurantActivity.makeLaunchIntent(MainActivity.this,position);
        startActivity(intent);

    }

//    public static List<Inspection> getInspectionsList(int position) {
//        return restaurant[position].inspections;
//    }
}

