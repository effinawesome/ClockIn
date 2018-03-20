package com.faithco.m.clockin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.content.Context;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class MainActivity extends AppCompatActivity {
    private TextView latitudeField;
    private TextView longitudeField;
    private TextView logField;
    private TextView jobNameField;
    private LocationManager locationManager;
    private String provider;
    private String jobName;
    private Location location;
    private String dirName = "clockin";
    private String fileName = "current.txt";
    private String clockedInFile = Environment.getExternalStorageDirectory()
            + File.separator
            + dirName
            + File.separator
            + fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File file = new File(clockedInFile);
        if(file.exists()){
            setContentView(R.layout.activity_clockout);
            onCreateClockOut();
        }
        else{
            setContentView(R.layout.activity_clockin);
            onCreateClockIn();
        }


    }
    private void onCreateClockOut(){
        jobNameField = (TextView) findViewById(R.id.clockedinjobname);
        //jobNameField.setText((String)getClockedInJobName());
        latitudeField = (TextView) findViewById(R.id.lat_text2);
        longitudeField = (TextView) findViewById(R.id.lon_text2);
        logField = (TextView) findViewById(R.id.editText);
        logField.append((String)getClockedInJobName());
    }
    private void onCreateClockIn(){
        latitudeField = (TextView) findViewById(R.id.lat_text);
        longitudeField = (TextView) findViewById(R.id.lon_text);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        logField = (TextView) findViewById(R.id.editText2);
        logField.append("made it to onCreateClockIn\n");
    }

    private boolean writeToFile(String data){
        String directoryPath =
                Environment.getExternalStorageDirectory()
                + File.separator
                + dirName
                + File.separator;
        logField.append("Writing to: " + fileName + " at: " + directoryPath + "\n");

        File fileDirectory = new File(directoryPath);
        if(!fileDirectory.exists()){
            if(fileDirectory.mkdirs()){
                logField.append("Directory created \n");
            }
            else{
                logField.append("Directory creation failed\n");
                return false;
            }
        } else{
          logField.append("Directory exists\n");
        }

        try{
            File fileToWrite = new File(directoryPath,fileName);

            if(fileToWrite.createNewFile()){
                FileOutputStream outputStream = new FileOutputStream(fileToWrite);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.append(data);
                //outputStreamWriter.close();
                outputStreamWriter.flush();
                return true;
            }
            else{
                logField.append("Failed to create file\n");
            }
        } catch (IOException e){
            logField.append("IO Exception: " + e.toString()+ "\n");
            return false;
        }
        return true;
    }

    // retrieve the job name
    private String getClockedInJobName(){
        File file = new File(clockedInFile);
        StringBuilder jobname = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null){
                jobname.append(line);
            }
        }
        catch (IOException e){

        }
        return jobname.toString();
    }

    public void clockIn(View view) {
        logField.append("made it to onClick\n");
        jobNameField = (TextView) findViewById(R.id.jobNameText);
        //System.out.println("Provider " + provider + " has been selected.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            logField.append("Permissions are incorrect.\n");
            return;
        }
        else{
            logField.append("permissions are correct\n");

            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            if(provider != null) {
                location = locationManager.getLastKnownLocation(provider);
                logField.append("provider isnt null\n");
                if (location != null) {
                    float lat = (float) (location.getLatitude());
                    float lon = (float) (location.getLongitude());
                    latitudeField.setText(String.valueOf(lat));
                    longitudeField.setText(String.valueOf(lon));
                } else {
                    longitudeField.setText("Cannot retrieve location.");
                    latitudeField.setText("Cannot retrieve location.");
                }
                if(writeToFile(jobNameField.getText().toString())){
                    logField.append("wrote to file\n");
                    setContentView(R.layout.activity_clockout);

                }

            }
            else
            {
                logField.append("provider is null.");
            }
        }

    }
}
