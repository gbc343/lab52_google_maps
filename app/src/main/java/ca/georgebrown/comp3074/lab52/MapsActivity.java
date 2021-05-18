package ca.georgebrown.comp3074.lab52;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    private EditText locationNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        locationNameText = findViewById(R.id.location_text);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = locationNameText.getText().toString();
                if(!name.isEmpty()){
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try{
                   List<Address> addresses =
                    geocoder.getFromLocationName(name,1);
                   if(addresses !=null && !addresses.isEmpty()){
                       Address a = addresses.get(0);
                       LatLng pos = new LatLng(a.getLatitude(), a.getLongitude());
                       if(mMap!=null) {
                           mMap.addMarker(new MarkerOptions().position(pos).title(name));
                           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 9));
                       }
                   }
                }
                    catch (IOException e){
                        e.printStackTrace();
                    }
            }

            }
        });


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED){
        //we have permission
            getLocation();


        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode == 1
                && permissions.length == 1
                && permissions[0]==Manifest.permission.ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getLocation();
        }
        else{
            Log.d("PERMISSION", "Permission denied");
        }
    }

    void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //we have permission
            LocationManager locationManager =
                    (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            String provider = LocationManager.GPS_PROVIDER;
            if (!locationManager.isProviderEnabled(provider)) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }else {
                Location location = locationManager.getLastKnownLocation(provider);
                if (mMap != null && location != null) {
                    LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18));
                }

                locationManager.requestLocationUpdates(provider, 100, 1, this);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng clc = new LatLng(43.675987,-79.4129857);
        mMap.addMarker(new MarkerOptions().position(clc).title("Casa Loma Campus"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clc,18));
        getLocation();

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location!=null){
            LatLng pos = new LatLng(location.getLatitude(),location.getLongitude());
            if(mMap!=null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18));
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }
}