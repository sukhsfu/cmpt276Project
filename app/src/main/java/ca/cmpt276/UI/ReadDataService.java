package ca.cmpt276.UI;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;
import ca.cmpt276.model.Violation;

/**
 * The ReadDataService reads in and organizes data from CSV file into model
 */
class sortinspection implements Comparator<Inspection>
{
    public int compare(Inspection a, Inspection b){
        return (b.getDate().compareTo(a.getDate()));
    }
}
public class ReadDataService extends Service {

    private List<Inspection> inspections = new ArrayList<>();
    private RestaurantManager manager = RestaurantManager.getInstance();
    String url = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    String url2 = " http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    JSONObject obj;
    String tmp;
    JSONObject obj2;
    String tmp2;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "data.csv");

        if(!file.exists()){
            readRestaurantDataInitial();
            readInspectionDataInitial();
        }

        try{
            readRestaurantData();}
        catch (IOException e){

        }
        try{
            readInspectionData();}
        catch (IOException e){

        }
        organizeData();
        debugData();

        return START_STICKY;
    }

    public ReadDataService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void readRestaurantDataInitial() {
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

                Restaurant sample = new Restaurant(trackingNum, name, address, city,"", latitude, longitude);
                manager.addRestaurant(sample);
            }
        }catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();

        }
        manager.sortRestaurantList();
    }

    private void readInspectionDataInitial() {
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
        Collections.sort(inspections,new sortinspection());
    }

    private void readRestaurantData()  throws IOException{

        manager.clearRestaurants();

        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


        File file = new File(path, "data.csv");
        InputStream is= new FileInputStream(file);;



        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                String trackingNum = tokens[0];
                String name = tokens[1].replaceAll("[^a-zA-Z0-9 &]", "");
                String address = tokens[2].replaceAll("[^a-zA-Z0-9 &]", "");
                String city = tokens[3].replaceAll("[^a-zA-Z0-9 &]", "");
                String facetype = tokens[4].replaceAll("[^a-zA-Z0-9 &]","");
                double latitude = Double.parseDouble(tokens[5]);
                double longitude = Double.parseDouble(tokens[6]);

                Restaurant sample = new Restaurant(trackingNum, name, address, city, facetype, latitude, longitude);
                manager.addRestaurant(sample);
            }
        }catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();

        }
        manager.sortRestaurantList();
    }

    private void readInspectionData()throws IOException {
        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File file = new File(path, "inspection.csv");
        InputStream is = new FileInputStream(file);;

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );



        String line = "";
        try {
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                System.out.println(tokens);
                if(tokens.length!=0) {
                    String trackingNum = tokens[0];
                    String date = tokens[1];
                    String type = tokens[2].replaceAll("[^a-zA-Z0-9 &]","");;
                    int numCritical = Integer.parseInt(tokens[3]);
                    int numNonCritical = Integer.parseInt(tokens[4]);
                    String hazardRating = "";
                    if (tokens.length == 5) {
                        Inspection sample = new Inspection(trackingNum, date, type, numCritical, numNonCritical, hazardRating);
                        inspections.add(sample);
                    } else {
                        if (tokens.length == 7) {
                            hazardRating = tokens[6].replaceAll("[^a-zA-Z0-9 &]","");;
                        }


                        Inspection sample = new Inspection(trackingNum, date, type, numCritical, numNonCritical, hazardRating);

                        if (tokens[5].length() > 0) {
                            if (!tokens[5].contains("|")) {
                                // there is only one violation
                                String[] indivViol = tokens[5].split(",");

                                Violation violation = extractViolationFromCSV(indivViol);
                                sample.addViolation(violation);
                            } else {
                                String[] violations = tokens[5].split("[|]");
                                for (int i = 0; i < violations.length; i++) {
                                    String[] indivViol = violations[i].split(",");
                                    Violation violation = extractViolationFromCSV(indivViol);
                                    sample.addViolation(violation);
                                }
                            }
                        }
                        inspections.add(sample);
                    }
                }
            }
        }catch (IOException e) {
            Log.wtf("MyActivityIns", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
        Collections.sort(inspections,new sortinspection());
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
            //Log.d("LogAllData", "Restaurant: " + restaurant.toString());
        }
        for (Inspection inspection : inspections) {
            //Log.d("LogAllData", "Inspection: " + inspection.toString());
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
}
