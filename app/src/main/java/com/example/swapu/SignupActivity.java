package com.example.swapu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    EditText edName, edEmail, edPassword, edConfirmPassword, location;
    Button signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        edConfirmPassword = findViewById(R.id.edConfirmPassword);
        location = findViewById(R.id.location_signup_edittext);
        signup = findViewById(R.id.signup_button);
        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    checkLocation();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( TextUtils.isEmpty(edName.getText())){
                    edName.setError( "Name is required!" );
                }else if( TextUtils.isEmpty(edEmail.getText())){
                    edEmail.setError( "Email is required!" );
                }else if( TextUtils.isEmpty(edPassword.getText())){
                    edPassword.setError( "Password is required!" );
                }else if( TextUtils.isEmpty(edConfirmPassword.getText())){
                    edConfirmPassword.setError( "Confirm password is required!" );
                }else if(!edPassword.getText().toString().equals(edConfirmPassword.getText().toString())){
                    Toast.makeText(SignupActivity.this, "Passwords are not the same!", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(location.getText().toString())){
                    location.setError("Empty zipcode");
                }
                else{
                    TextView city = findViewById(R.id.city_signup_textview);
                    if(city.getText().toString().isEmpty()) {
                        checkLocation();
                    }
                    final ProgressDialog progress = new ProgressDialog(SignupActivity.this);
                    progress.setMessage("Loading ...");
                    progress.show();
                    ParseUser user = new ParseUser();
                    user.setUsername(edEmail.getText().toString().trim());
                    user.setEmail(edEmail.getText().toString().trim());
                    user.setPassword(edPassword.getText().toString());
                    user.put("name", edName.getText().toString().trim());
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            progress.dismiss();
                            if (e == null) {
                                Toast.makeText(SignupActivity.this, "Welcome!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                                startActivity(intent);
                                isFinishing();
                            } else {
                                ParseUser.logOut();
                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void checkLocation(){
        Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
        EditText location = findViewById(R.id.location_signup_edittext);
        TextView city = findViewById(R.id.city_signup_textview);
        String locality;
        try {
            List<Address> myList = myLocation.getFromLocationName(location.getText().toString(), 5);
            if(myList.size()>0){
                locality = myList.get(0).getLocality() + ", "+myList.get(0).getAdminArea();
                city.setText(locality);
            }else{
                showErrorMessage("Wrong Zip code");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showErrorMessage(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(SignupActivity.this, R.style.Theme_AppCompat_DayNight).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Error Message");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
