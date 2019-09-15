package com.example.calcula_percurso_e_tempo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GPS = 1001;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public double latitude;
    public double longitude;
    public double latitudeAtual;
    public double longitudeAtual;
    public boolean isActivated = false;
    public EditText searchEditText;
    public TextView distanceshowView;
    public Location localizacaoAnterior;
    public double metros = 0;
    public double tempo = 0;
    public TextView timeshowView;

    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchEditText = findViewById(R.id.searchEditText);
        distanceshowView = findViewById(R.id.distanceshowView);
        timeshowView = findViewById(R.id.timeShowView);

        ImageButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener( (v) -> {
            Uri uri = Uri.parse(String.format("geo: %f, %f?q=%s", latitude, longitude, searchEditText.getEditableText().toString()));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                if (localizacaoAnterior == null){
                    localizacaoAnterior = location;
                }else{
                  metros = metros + location.distanceTo(localizacaoAnterior);
                  String s = String.valueOf(metros);
                  distanceshowView.setText(s);
                  long secs = (new Date().getTime())/1000;
                  tempo = tempo + secs;
                  String st = String.valueOf(tempo);
                  timeshowView.setText(st);
                }
                latitudeAtual = lat;
                longitudeAtual = lon;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2000,
                    10,
                    locationListener
            );
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_GPS);
        }
    }

    public void requestPermissionGPS(View view){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_GPS);
    }

    public void activateGPS(View view){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2000,
                    10,
                    locationListener
            );
            isActivated = true;
        }else{
            Toast.makeText(this, getString(R.string.no_permition), Toast.LENGTH_SHORT).show();
        }
    }

    public void deactivateGPS(View view){
        if (isActivated == true)
            locationManager.removeUpdates(locationListener);
        else
            Toast.makeText(this, getString(R.string.gps_is_off), Toast.LENGTH_SHORT).show();
            isActivated = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_GPS){
            if(grantResults.length > 0 && grantResults [0] == PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            2000,
                            10,
                            locationListener
                    );
                }
            }
        }else{
            Toast.makeText(this, getString(R.string.gps_is_off), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }



}
