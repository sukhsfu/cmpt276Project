package ca.cmpt276.UI;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;
import ca.cmpt276.model.Violation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The MainActivity reads in and organizes data from CSV file into model, then displays the
 * list of restaurants to the user.
 */

public class MainActivity extends AppCompatActivity implements jadapter.OnNoteListener {
    private List<String> restaurantText = new ArrayList<>();
    private List<Inspection> inspections = new ArrayList<>();
    protected static List<Integer> Hazards=new ArrayList<>();

    private RestaurantManager manager = RestaurantManager.getInstance();private TextView mTextViewResult;
    String url = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    JSONObject obj;
    String tmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //startDownload();  This function can be used if we want to download data to external storage
        readRestaurantData();
        readInspectionData();
        organizeData();
        debugData();
        setOutputData();
        setupRestaurantInList();
        setupButtonSwitchToMap();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void setupButtonSwitchToMap() {
        Button switchMap = (Button) findViewById(R.id.btnSwitchMap);
        switchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
    }

    private void jsonParse(){

//        mTextViewResult = findViewById(R.id.text_view_result);
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

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tmp = obj.getJSONObject("result").getJSONArray("resources").getJSONObject(0).getString("url");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            OkHttpClient client2 = new OkHttpClient();
                            Request request2 = new Request.Builder().url(tmp).build();

                            client2.newCall(request2).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    e.printStackTrace();;
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    if(response.isSuccessful()){
                                        final String myCSV = response.body().string();
                                        MainActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mTextViewResult.setText(myCSV);
                                            }
                                        });
                                    }
                                }
                            });
//                            mTextViewResult.setText(tmp);
                        }
                    });
                }

            }
        });
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
                String[] tokens = line.split(",");
                String trackingNum = tokens[0];
                String name = tokens[1].replaceAll("[^a-zA-Z0-9 &]", "");
                String address = tokens[2].replaceAll("[^a-zA-Z0-9 &]", "");
                String city = tokens[3].replaceAll("[^a-zA-Z0-9 &]", "");
                double latitude = Double.parseDouble(tokens[5]);
                double longitude = Double.parseDouble(tokens[6]);

                Restaurant sample = new Restaurant(trackingNum, name, address, city, latitude, longitude);
                manager.addRestaurant(sample);
            }
        }catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();

        }
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
                String[] tokens = line.split(",",7);

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

                        Violation violation = extractViolationFromCSV(indivViol);
                        sample.addViolation(violation);
                    }
                    else {
                        String[] violations = tokens[6].split("[|]");
                        for (int i = 0; i < violations.length; i++) {
                            String[] indivViol = violations[i].split(",");
                            Violation violation = extractViolationFromCSV(indivViol);
                            sample.addViolation(violation);
                        }
                    }
                }
                inspections.add(sample);
            }
        }catch (IOException e) {
            Log.wtf("MyActivityIns", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
    }

    private void organizeData() {
        for (Restaurant restaurant : manager) {
            for (Inspection inspection : inspections) {
                if (inspection.getTrackingNumber().equalsIgnoreCase(restaurant.getTrackingNumber())) {
                    restaurant.addInspection(inspection);
                }
            }
        }
    }

    private void debugData() {
        for (Restaurant restaurant : manager) {
            Log.d("LogAllData", "Restaurant: " + restaurant.toString());
        }
        for (Inspection inspection : inspections) {
            Log.d("LogAllData", "Inspection: " + inspection.toString());
        }

    }

    private  void setOutputData(){
        for (Restaurant restaurant : manager) {
            if (restaurant.getInspections().size() != 0) {
                Inspection inspectionRet = restaurant.getInspections().get(0);
                for (Inspection inspection : restaurant.getInspections()) {
                    if (inspection.getDate().compareTo(inspectionRet.getDate()) > 0) {
                        inspectionRet = inspection;
                    }

                }
                restaurantText.add(restaurant.getName() + "\n\n" + dateDifference(inspectionRet.getDate())+ (inspectionRet.getNumCriticalIssues() + inspectionRet
                        .getNumNonCriticalIssues()) + " issues found\n" + inspectionRet.getNumCriticalIssues() + "  critical, " + inspectionRet.getNumNonCriticalIssues() + "  non-critical");
                   if(inspectionRet.getHazardLevel().equalsIgnoreCase("\"Low\"") ){
                       Hazards.add(Color.GREEN);
                   }
               else if(inspectionRet.getHazardLevel().equalsIgnoreCase("\"Moderate\"")){
                    Hazards.add(Color.YELLOW);
                }
                else if(inspectionRet.getHazardLevel().equalsIgnoreCase("\"High\"")) {
                    Hazards.add(Color.RED);
                }
                else{
                    Hazards.add(Color.WHITE);
                }
            }
            else{
                Hazards.add(Color.WHITE);
                restaurantText.add(restaurant.getName() + "\n");
            }
        }

    }

    protected static String dateDifference(String A){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate today= LocalDate.now();
        String todaystring=formatter.format(today);
        int givendate=Integer.parseInt(A);
        int todaydate=Integer.parseInt(todaystring);
        int[] givenA={(givendate/10000),((givendate%10000)/100),(givendate%100)}; //0-yy,1-mm,2-dd
        int[] todayA={(todaydate/10000),((todaydate%10000)/100),(todaydate%100)}; //0-yy,1-mm,2-dd
        String[] mon={"Jan","Feb","March","April","May","June","July","August","Sept","Oct","Nov","Dec"};
        int[]days={31,28,31,30, 31,30,31,31, 30,31,30,31};

        if(givenA[0]%4==0){
            if(givenA[0]%100==0){
                if(givenA[0]%400==0){
                    days[1]=29;
                }
            }
            days[1]=29;
        }

       if (((todayA[0]*365+todayA[1]*days[todayA[1]-1]+todayA[2])-(givenA[0]*365+givenA[1]*days[givenA[1]-1]+givenA[2]))<=30){

           return (((todayA[0]*365+todayA[1]*days[todayA[1]-1]+todayA[2])-(givenA[0]*365+givenA[1]*days[givenA[1]-1]+givenA[2]))+" days ago\n");
       }
       else if((todayA[0]*365+todayA[1]*30+todayA[2]-givenA[0]*365-givenA[1]*30-givenA[2])<=365){
           return (mon[givenA[1]-1]+"  "+givenA[2]+"\n");
       }
       else{
           return(mon[givenA[1]-1]+"  "+givenA[0]+"\n");
       }

    }

    private Violation extractViolationFromCSV(String[] indivViol) {
        String violNum = indivViol[0].replaceAll("[^0-9]", "");
        int violType = Integer.parseInt(violNum);
        String severity = indivViol[1];
        String detailedDescrip = indivViol[2];
        boolean isRepeat;

        if (indivViol.length >= 4) {
            // not repeat
            isRepeat = false;
        } else {
            isRepeat = true;
        }

        return new Violation(violType, severity, detailedDescrip, isRepeat);
    }

    private void setupRestaurantInList() {

        RecyclerView list= findViewById(R.id.mainrecyleview);
        list.setHasFixedSize(true);
        list.setItemViewCacheSize(20);
        list.setDrawingCacheEnabled(true);
        list.setNestedScrollingEnabled(false);
        list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new jadapter(restaurantText,  this));
    }
    public void startDownload(View view){
//        Uri uri = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/2/2d/Snake_River_%285mb%29.jpg");
        Uri uri = Uri.parse("http://data.surrey.ca/dataset/3c8cb648-0e80-4659-9078-ef4917b90ffb/resource/0e5d04a2-be9b-40fe-8de2-e88362ea916b/download/restaurants.csv");
        DownloadManager.Request request1 = new DownloadManager.Request(uri);
        request1.setTitle("CSVDownload");
        request1.setDescription("Updatting CSV");
        request1.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS,"myCSV");

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request1);
    }


    @Override
    public void onNoteClick(int position) {
        Intent intent=RestaurantActivity.makeLaunchIntent(MainActivity.this,position);
        startActivity(intent);
    }

}

