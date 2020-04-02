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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Button;
import android.widget.Toast;

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

/**
 * The MainActivity displays the list of restaurants to the user.
 */

public class MainActivity extends AppCompatActivity implements jadapter.OnNoteListener, AdapterView.OnItemSelectedListener {
    private List<String> restaurantText = new ArrayList<>();
    protected static List<Integer> Hazards=new ArrayList<>();

    private RestaurantManager manager = RestaurantManager.getInstance();
    private SearchView searchView;
    private int selectedSpinnerPOS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setOutputData();
        setupRestaurantInList();
        setupButtonSwitchToMap();

        Spinner spinner = (Spinner) findViewById(R.id.mainSpinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.menu_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        searchView = findViewById(R.id.mainSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String searchText = searchView.getQuery().toString();
                if(searchText != null || !searchText.equals("")){
                    switch(selectedSpinnerPOS){
                        case 0:
                            //TODO filter restaurants by name
                            break;
                        case 1:
                            //TODO filter restaurants by hazard level
                            break;
                        case 2:
                            //TODO filter restaurants by violations
                            break;
                        case 3:
                            //TODO filter restaurants by favorites
                            break;
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("") || newText == null){
                   // TODO populate all restaurants
                }
                return false;
            }
        });
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "Position is: " + position, Toast.LENGTH_SHORT).show();
        selectedSpinnerPOS = position;
        switch (selectedSpinnerPOS){
            case 0:
                searchView.setQueryHint("Pizza");
                break;
            case 1:
                searchView.setQueryHint("Low");
                break;
            case 2:
                searchView.setQueryHint("Less than 10");
                break;
            case 3:
                searchView.setQueryHint("All favorites");
                // TODO: populate restaurant list to be favorites only
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

