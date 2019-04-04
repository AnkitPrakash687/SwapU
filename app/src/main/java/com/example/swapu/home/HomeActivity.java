package com.example.swapu.home;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.swapu.home.message.MessageFragment;
import com.example.swapu.R;
import com.example.swapu.home.sell.SellFragment;
import com.example.swapu.home.account.AccountFragment;
import com.parse.ParseUser;

public class HomeActivity extends AppCompatActivity {
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 4;
    public static String data;
    // TextView tvName, tvEmail;
    Fragment homeFragment;
    Fragment messageFragment;
    Fragment sellFragment;
    Fragment accountFragment;
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //   Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.navigation_home:
                            //  selectedFragment = new HomeFragment();
                            displayHomeFragment();
                            break;
                        case R.id.navigation_sell:
                            //   selectedFragment = new SellFragment();
                            displaySellFragment();
                            break;
                        case R.id.navigation_messages:
                            //   selectedFragment = new MessageFragment();
                            displayMessageFragment();
                            break;
                        case R.id.navigation_account:
                            //     selectedFragment = new AccountFragment();
                            displayAccountFragment();
                            break;
                    }

                    //  getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNav = findViewById(R.id.navigation);
        String data1 = data;
//        TextView sample = findViewById(R.id.sample_textview);
//        sample.setText(data);
        ParseUser currentUser = ParseUser.getCurrentUser();

        //  Fragment selectedFragment = new HomeFragment();
        if (savedInstanceState == null){
            //  getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            homeFragment = new HomeFragment();
            messageFragment = new MessageFragment();
            sellFragment = new SellFragment();
            accountFragment = new AccountFragment();
            displayHomeFragment();

        }
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    private void displayHomeFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (homeFragment.isAdded()) { // if the fragment is already in container
            ft.show(homeFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.fragment_container, homeFragment);
        }
        // Hide fragment B
        if (messageFragment.isAdded()) {
            ft.hide(messageFragment);
        }
        // Hide fragment C
        if (sellFragment.isAdded()) {
            ft.hide(sellFragment);
        }
        if (accountFragment.isAdded()) {
            ft.hide(accountFragment);
        }
        // Commit changes
        ft.commit();
    }

    private void displayMessageFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (messageFragment.isAdded()) { // if the fragment is already in container
            ft.show(messageFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.fragment_container, messageFragment);
        }
        // Hide fragment B
        if (homeFragment.isAdded()) {
            ft.hide(homeFragment);
        }
        // Hide fragment C
        if (sellFragment.isAdded()) {
            ft.hide(sellFragment);
        }
        if (accountFragment.isAdded()) {
            ft.hide(accountFragment);
        }
        // Commit changes
        ft.commit();
    }

    private void displaySellFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (sellFragment.isAdded()) { // if the fragment is already in container
            ft.show(sellFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.fragment_container, sellFragment);
        }
        // Hide fragment B
        if (messageFragment.isAdded()) {
            ft.hide(messageFragment);
        }
        // Hide fragment C
        if (accountFragment.isAdded()) {
            ft.hide(accountFragment);
        }
        if (homeFragment.isAdded()) {
            ft.hide(homeFragment);
        }
        // Commit changes
        ft.commit();
    }

    private void displayAccountFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (accountFragment.isAdded()) { // if the fragment is already in container
            ft.show(accountFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.fragment_container, accountFragment);
        }
        // Hide fragment B
        if (messageFragment.isAdded()) {
            ft.hide(messageFragment);
        }
        // Hide fragment C
        if (sellFragment.isAdded()) {
            ft.hide(sellFragment);
        }
        if (homeFragment.isAdded()) {
            ft.hide(homeFragment);
        }
        // Commit changes
        ft.commit();
    }


}
