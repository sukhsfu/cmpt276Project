package ca.cmpt276.UI;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;



import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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
 * The MainActivity displays the list of restaurants to the user.
 */

public class MainActivity extends AppCompatActivity implements jadapter.OnNoteListener {
    private List<String> restaurantText = new ArrayList<>();
    private List<Inspection> inspections = new ArrayList<>();
    protected static List<Integer> Hazards=new ArrayList<>();



    private RestaurantManager manager = RestaurantManager.getInstance();
    private TextView mTextViewResult;
    String url = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    String url2 = " http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    JSONObject obj;
    String tmp;
    JSONObject obj2;
    String tmp2;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Button switchMap = findViewById(R.id.btnSwitchMap);
        switchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
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
                   if(inspectionRet.getHazardLevel().equalsIgnoreCase("Low") ){
                       Hazards.add(Color.GREEN);
                   }
               else if(inspectionRet.getHazardLevel().equalsIgnoreCase("Moderate")){
                    Hazards.add(Color.YELLOW);
                }
                else if(inspectionRet.getHazardLevel().equalsIgnoreCase("High")) {
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

    @Override
    public void onNoteClick(int position) {
        Intent intent=RestaurantActivity.makeLaunchIntent(MainActivity.this,position, 0);
        startActivity(intent);
    }

}

