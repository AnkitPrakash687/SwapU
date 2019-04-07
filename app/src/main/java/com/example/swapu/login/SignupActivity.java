package com.example.swapu.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swapu.R;
import com.example.swapu.home.HomeActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    EditText edName, edEmail, edPassword, edConfirmPassword, zipCode;
    Button signup;
    TextView city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        city = findViewById(R.id.city_signup_textview);
        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        edConfirmPassword = findViewById(R.id.edConfirmPassword);
        zipCode = findViewById(R.id.location_signup_edittext);
        signup = findViewById(R.id.signup_button);
        zipCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
             /*added validation to check if name is blank*/
                if( TextUtils.isEmpty(edName.getText())){
                    edName.setError( "Name is required!" );
             /*added validation to check if email is blank*/
                }else if( TextUtils.isEmpty(edEmail.getText())){
                    edEmail.setError( "Email is required!" );
             /*added validation to check if passowrd is blank*/
                }else if( TextUtils.isEmpty(edPassword.getText())){
                    edPassword.setError( "Password is required!" );
            /*added validation to check if confirm password is blank*/
                }else if( TextUtils.isEmpty(edConfirmPassword.getText())){
                    edConfirmPassword.setError( "Confirm password is required!" );
                }else if(!edPassword.getText().toString().equals(edConfirmPassword.getText().toString())){
                    Toast.makeText(SignupActivity.this, "Passwords are not the same!", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(zipCode.getText().toString())){
                    zipCode.setError("Empty zipcode");
                }else if(city.getText().toString().equals("Wrong Zip code")){
                    zipCode.setError("Wrong zipcode");
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
                    user.put("location", city.getText().toString());
                    user.put("zipCode", Integer.parseInt(zipCode.getText().toString()));
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("location", city.getText().toString());
                    editor.putString("zipCode", zipCode.getText().toString());
                    editor.commit();
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            progress.dismiss();
                            if (e == null) {
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                Bitmap profilePic = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                        R.drawable.no_profile);
                                currentUser.put("profilePic", conversionBitmapParseFile(profilePic));
                                currentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(SignupActivity.this, "Welcome!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            isFinishing();

                                        } else {

                                            // Toast.makeText(getActivity(), e.getMessage()+" Error in posting item", Toast.LENGTH_LONG).show();
                                        }
                                        // Here you can handle errors, if thrown. Otherwise, "e" should be null
                                    }
                                });

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

        String locality;
        try {
            List<Address> myList = myLocation.getFromLocationName(zipCode.getText().toString(), 5);
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

    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
    }
}
