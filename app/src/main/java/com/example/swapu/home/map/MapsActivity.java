package com.example.swapu.home.map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swapu.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback { //, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_REQUEST_CODE = 101;
    GoogleMap gMap;
    LatLng latLng;
    String locationDetails;
    EditText zipCodeEt;
    String zipCode;
    TextView currentLocationTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        currentLocationTv = findViewById(R.id.current_location_tv);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        zipCodeEt = findViewById(R.id.zipcode_edittext);
        Button getlocation = findViewById(R.id.get_location);
        Button zipcodeSearch = findViewById(R.id.get_location_zipcode);
        Button apply = findViewById(R.id.apply_button);
        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLastLocation();
            }
        });
        zipcodeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText zipcode = findViewById(R.id.zipcode_edittext);
                getLocationFromZipcode(zipcode.getText().toString());

            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locationDetails != null) {
                    String[] place = locationDetails.split(",");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("location", place[0] + ", " + place[1]);
                    editor.putString("zipCode", place[2]);
                    editor.commit();

                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("location",locationDetails);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                    SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                    Toast.makeText(MapsActivity.this,currentLocation.getLatitude()+" "+currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
//                    try {
//                        getPlaceDetail(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                }else{
                    Toast.makeText(MapsActivity.this,"No Location recorded",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are Here");
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        googleMap.animateCamera(yourLocation);
        //Adding the created the marker on the map
        googleMap.addMarker(markerOptions);
        try {
            getPlaceDetail(latLng);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                } else {
                    Toast.makeText(MapsActivity.this,"Location permission missing",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void getLocationFromZipcode(String zipcode){
        Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> myList = myLocation.getFromLocationName(zipcode, 5);
            if(myList.size()>0) {
                latLng = new LatLng(myList.get(0).getLatitude(), myList.get(0).getLongitude());
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                supportMapFragment.getMapAsync(MapsActivity.this);

            }else{
                Toast.makeText(MapsActivity.this,"No zip found",Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void getPlaceDetail(LatLng latLng) throws IOException {
        Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> myList = myLocation.getFromLocation(latLng.latitude,latLng.longitude,5);
        Address address = myList.get(0);
        String addressline = address.getLocality() + ", " + address.getAdminArea();
        Button location = findViewById(R.id.get_location);
        currentLocationTv.setText(addressline);
        zipCode = address.getPostalCode();
        zipCodeEt.setText(zipCode);
        locationDetails = addressline + ", " + address.getPostalCode() + ", " + address.getCountryName();
    }

}

