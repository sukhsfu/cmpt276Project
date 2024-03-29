package ca.cmpt276.UI;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ca.cmpt276.model.Restaurant;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static ca.cmpt276.UI.MainActivity.restaurantList;

public class update extends AppCompatDialogFragment {
    Context context;
    Activity activity;
    @NonNull
    String url = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    String url2 = " http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    JSONObject obj;
    JSONObject obj9;
    String tmp;
    JSONObject obj2;
    String tmp2;
    AlertDialog.Builder builder;
    AlertDialog builderdownload;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)  {
         builder = new AlertDialog.Builder(activity);
         builderdownload =  builder.create();
         builder.setTitle("Update Data").setMessage("New data detected on server, would you like to update?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builderdownload.setIcon(R.mipmap.loading);
                builderdownload.setTitle("Download complete.");
                builderdownload.setMessage("Synchronized Data with City.");
                builderdownload.create();
                builderdownload.show();
                SharedPreferences sharedPreferences= context.getSharedPreferences("restaurants", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                SharedPreferences sharedPreferences2= context.getSharedPreferences("Inspections", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2=sharedPreferences2.edit();
                Gson gson;
                int cnt=0;
                editor.remove("restaurant_size");
                editor.putInt("restaurant_size",restaurantList.size());
                editor.apply();
                for(Restaurant restaurant:restaurantList){
                    gson = new Gson();
                    String json = gson.toJson(restaurant);
                    editor.remove("restaurant_"+cnt);
                    editor2.remove("Inspection_"+cnt);
                    editor.putString("restaurant_"+cnt,json);
                    if(restaurant.getInspections().size()!=0)
                    editor2.putString("Inspection_"+cnt,restaurant.getInspections().get(0).getTrackingNumber());
                    cnt++;
                    editor.apply();
                    editor2.apply();
                }

                jsonParse();
                jsonParse2();
                savetime();
                builderdownload.cancel();
                //add here.


            }
        });


           return builder.create();





    }

    private void jsonParse(){
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
                            try {
                                tmp = obj.getJSONObject("result").getJSONArray("resources").getJSONObject(0).getString("url");
                                //String last_modified= obj.getJSONObject("result").getJSONArray("resources").getJSONObject(0).getString("last_modified");
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
                                    if(response.isSuccessful()) {
                                        final String myCSV = response.body().string();
                                        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                                            int request_code = 0;

                                            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_code);
                                        } else {

                                            final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


                                            File file = new File(path, "data.csv");

                                            try {
                                                file.createNewFile();
                                            } catch (IOException e) {
                                                System.out.println("An error occurred.");
                                                e.printStackTrace();
                                            }
                                            try {

                                                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                                                writer.write(myCSV);
                                                writer.close();
                                            } catch (IOException e) {
                                                throw new RuntimeException("Unable to write to File " + e);

                                            }

                                        }
                                    }
                                }
                            });
                            //textView.setText(tmp);


                }

            }
        });
    }

    private void jsonParse2(){


        OkHttpClient client3 = new OkHttpClient();
        Request request3 = new Request.Builder()
                .url(url2)
                .build();
        client3.newCall(request3).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call3, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call3, @NotNull Response response3) throws IOException {
                if (response3.isSuccessful()){
                    final String myResponse3 = response3.body().string();

                    try {
                        obj2 = new JSONObject(myResponse3);

                    } catch (Throwable t) {

                    }


                            try {
                                tmp2 = obj2.getJSONObject("result").getJSONArray("resources").getJSONObject(0).getString("url");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            OkHttpClient client4 = new OkHttpClient();
                            Request request4 = new Request.Builder().url(tmp2).build();

                            client4.newCall(request4).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call3, @NotNull IOException e) {
                                    e.printStackTrace();;
                                }

                                @Override
                                public void onResponse(@NotNull Call call3, @NotNull Response response3) throws IOException {
                                    if(response3.isSuccessful()){
                                        final String myCSV2 = response3.body().string();


                                                //System.out.println(myCSV2);



                                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                                                    int request_code = 0;

                                                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_code);
                                                } else {
                                                    final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


                                                    File file2 = new File(path, "inspection.csv");



                                                    try {
                                                        file2.createNewFile();
                                                    } catch (IOException e) {
                                                        System.out.println("An error occurred.");
                                                        e.printStackTrace();
                                                    }
                                                    try {

                                                        BufferedWriter writer2 = new BufferedWriter(new FileWriter(file2));
                                                        writer2.write(myCSV2);
                                                        writer2.close();
                                                    } catch (IOException e) {
                                                        throw new RuntimeException("Unable to write to File " + e);

                                                    }
                                                }


                                    }
                                }



                    });
                }

            }
        });

    }
    public void savetime(){   //saving local_time and last_modified stime.
        final String[] A = new String[1];
        A[0]="";
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
                        obj9 = new JSONObject(myResponse);

                    } catch (Throwable t) {

                    }
                    try {
                        A[0] = obj9.getJSONObject("result").getJSONArray("resources").getJSONObject(0).getString("last_modified");
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy'-'MM'-'dd'T'hh:mm");
                        LocalDateTime today = LocalDateTime.now();
                        String todaystring = formatter.format(today);
                        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                            int request_code = 0;

                            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_code);
                        } else {

                            final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


                            File file = new File(path, "time.txt");
                            if(!file.exists()) {
                                try {
                                    file.createNewFile();
                                } catch (IOException e) {
                                    System.out.println("An error occurred.");
                                    e.printStackTrace();
                                }
                            }

                            try{
                                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                                writer.write(todaystring);
                                writer.newLine();
                                writer.write(A[0]);
                                writer.close();
                            } catch (IOException e) {
                                throw new RuntimeException("Unable to write to File " + e);

                            }

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });



    }

    @Override
    public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            this.context = context;
            this.activity=(Activity)context;
    }




}
