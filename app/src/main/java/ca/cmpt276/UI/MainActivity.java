package ca.cmpt276.UI;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    protected static List<Integer> numcritical=new ArrayList<>();
    protected static List<Boolean> favourite =new ArrayList<>();
    protected static List<Restaurant> restaurantList =new ArrayList<>();
    private RestaurantManager manager = RestaurantManager.getInstance();
    private static RestaurantManager manager1 = RestaurantManager.getInstance();
    private static final String SEARCH_TEXT = "SearchText";
    private static final String SPINNER_POS = "SpinnerPOS";
    private SearchView searchView;
    private int selectedSpinnerPOS = 0;
    private String searchText;
    private Spinner spinner;
    private boolean searchPerformed = false;
    private jadapter Jadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if(intent.hasExtra(SPINNER_POS) && intent.hasExtra(SEARCH_TEXT)){
            searchText = intent.getStringExtra(SEARCH_TEXT);
            searchPerformed = true;
            selectedSpinnerPOS = intent.getIntExtra(SPINNER_POS, 0);
            //Toast.makeText(this, "spinner " + selectedSpinnerPOS + " " + searchText, Toast.LENGTH_SHORT).show();
            //updateRestaurantList(); //cases 0,1,2,3,4
        }else{
            // TODO populate entire list normally
        }

        setOutputData();//set data to restaurantText and Hazards list
        booltorestaurant();
        setupRestaurantInList();//pass restaurant and  Hazards to jadapter.
        setupButtonSwitchToMap();

        spinner = (Spinner) findViewById(R.id.mainSpinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.menu_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        searchView = findViewById(R.id.mainSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String text = searchView.getQuery().toString();
                if(text != null || !text.equals("")){
                    searchText = text;
                    searchPerformed = true;

                }
                updateRestaurantList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("") || newText == null){
                    searchPerformed = false;
                }
                updateRestaurantList(newText);
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
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                if(searchPerformed){
                    intent.putExtra(SPINNER_POS, selectedSpinnerPOS);
                    intent.putExtra(SEARCH_TEXT, searchText);
                }
                startActivity(intent);
            }
        });
    }

    private void updateRestaurantList(String text){
        switch(selectedSpinnerPOS){
            case 0:
                //TODO filter restaurants by name
                // use searchText field to get the search value entered for each case
                Jadapter.getFilter().filter(text);
                break;
            case 1:
                //TODO filter restaurants by hazard level
                 if(!text.isEmpty())
                  text=text.concat("clr");
                 Jadapter.getFilter().filter(text);
                break;
            case 2:
                //TODO filter restaurants by violations
                if(!text.isEmpty())
                    text=text.concat("xxxaaaxxx");
                Jadapter.getFilter().filter(text);
                break;
            case 3:
                //TODO filter restaurants by favorites

                text=text.concat("favs");
                Jadapter.getFilter().filter(text);
                break;
            case 4:
                //TODO filter restaurants by combined criteria
                if(!text.isEmpty())
                    text=text.concat("combined");
                Jadapter.getFilter().filter(text);
                break;
        }

    }

    private void setOutputData(){
        numcritical.clear();
        Hazards.clear();
        favourite.clear();
        int  cnt=0;
        for (Restaurant restaurant : manager) {
            numcritical.add(getRecentNumCriticalViolations(restaurant));
            SharedPreferences sharedPreferences=getSharedPreferences("favourites",MODE_PRIVATE);
             boolean checklist=sharedPreferences.getBoolean("favourite_"+cnt,false);
             favourite.add(checklist);

             cnt++;


            if (restaurant.getInspections().size() != 0) {
                Inspection inspectionRet = restaurant.getInspections().get(0);
                for (Inspection inspection : restaurant.getInspections()) {
                    if (inspection.getDate().compareTo(inspectionRet.getDate()) > 0) {
                        inspectionRet = inspection;
                    }

                }



                restaurantText.add(restaurant.getName()
                        + "\n\n" + dateDifference(inspectionRet.getDate())
                        + getString(R.string.issues_restaurant_tab, (inspectionRet.getNumCriticalIssues() + inspectionRet.getNumNonCriticalIssues()) )
                        + getString(R.string.critical_restaurant_tab,inspectionRet.getNumCriticalIssues())
                        + getString(R.string.non_critical_restaurant_tab , inspectionRet.getNumNonCriticalIssues()) );
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

    private static String[] getMonthArray(Context context) {
        String[] mon={context.getResources().getString(R.string.jan), context.getResources().getString(R.string.feb),
                context.getResources().getString(R.string.march), context.getResources().getString(R.string.april),
                context.getResources().getString(R.string.may), context.getResources().getString(R.string.june),
                context.getResources().getString(R.string.july), context.getResources().getString(R.string.aug),
                context.getResources().getString(R.string.sept), context.getResources().getString(R.string.oct),
                context.getResources().getString(R.string.nov), context.getResources().getString(R.string.dec)};
        return mon;
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
        Jadapter=new jadapter(restaurantText,  this);
        list.setAdapter(Jadapter );
        Jadapter.getFilter().filter("");
    }
    private int getRecentNumCriticalViolations(Restaurant restaurant){
        int numCriticalViolations = 0;
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, -1);
        Date lastYear = cal.getTime();

        List<Inspection> inspections = restaurant.getInspections();
        for(Inspection inspection: inspections){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            try{
                Date date = formatter.parse(inspection.getDate());
                if(date.after(lastYear)){
                    numCriticalViolations = numCriticalViolations + inspection.getNumCriticalIssues();
                }
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return numCriticalViolations;
    }

    @Override
    public void onNoteClick(int position) {
        Intent intent=RestaurantActivity.makeLaunchIntent(MainActivity.this,position, 0);
        if(searchPerformed){
            intent.putExtra(SEARCH_TEXT, searchText);
            intent.putExtra(SPINNER_POS, selectedSpinnerPOS);
        }
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(this, "Position is: " + position, Toast.LENGTH_SHORT).show();
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
            case 4:
                searchView.setQueryHint("Favorite, pizza, low, 5 or less");
                break;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public static void  booltorestaurant(){
        restaurantList.clear();
        int cnt=0;
        for(Restaurant restaurant:manager1){
              if(favourite.get(cnt++)) {
                  restaurantList.add(restaurant);
              }
        }

    }
    public static void restauranttobool(){
        favourite.clear();

        for(Restaurant restaurant:manager1){

            favourite.add(false);

            for(Restaurant restaurant1:restaurantList){
                if(restaurant1.getTrackingNumber().equals(restaurant.getTrackingNumber())){
                    favourite.remove(favourite.size()-1);
                    favourite.add(true);
                }
            }
        }
    }

}

