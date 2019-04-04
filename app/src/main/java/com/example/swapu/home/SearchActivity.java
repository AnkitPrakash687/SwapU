package com.example.swapu.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.swapu.R;

public class SearchActivity extends AppCompatActivity {
    EditText search;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search = findViewById(R.id.search_edittext);
    }

    public void onClickSearch(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("searchText",search.getText().toString());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
