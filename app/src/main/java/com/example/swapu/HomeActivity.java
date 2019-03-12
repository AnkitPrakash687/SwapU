package com.example.swapu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.swapu.R;
import com.parse.Parse;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
public static String data;
   // TextView tvName, tvEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNav = findViewById(R.id.navigation);

        FetchData process = new FetchData();
        process.execute();
        String data1 = data;
//        TextView sample = findViewById(R.id.sample_textview);
//        sample.setText(data);
        ParseUser currentUser = ParseUser.getCurrentUser();
        Fragment selectedFragment = new HomeFragment();

        GPSTracker gps = new GPSTracker(this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        if(latitude != 0 && longitude != 0) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String zip = addresses.get(0).getPostalCode();
            String country = addresses.get(0).getCountryName();
        }
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();}
//        tvName = findViewById(R.id.tvName);
//        tvEmail = findViewById(R.id.tvEmail);

        bottomNav.setOnNavigationItemSelectedListener(navListener);

//        if(currentUser!=null){
//            tvName.setText(currentUser.getString("name"));
//            tvEmail.setText(currentUser.getEmail());
//        }
    }

//    public void logout(View view) {
//        ProgressDialog progress = new ProgressDialog(this);
//        progress.setMessage("Loading ...");
//        progress.show();
//        ParseUser.logOut();
//        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//        progress.dismiss();
//    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.navigation_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.navigation_sell:
                            selectedFragment = new SellFragment();
                            break;
                        case R.id.navigation_messages:
                            selectedFragment = new MessageFragment();
                            break;
                        case R.id.navigation_account:
                            selectedFragment = new AccountFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };


}
