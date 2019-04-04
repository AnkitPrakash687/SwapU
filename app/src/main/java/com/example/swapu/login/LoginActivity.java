package com.example.swapu.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.swapu.App;
import com.example.swapu.R;
import com.example.swapu.common.AppStatus;
import com.example.swapu.home.HomeActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    EditText edEmail, edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);

        if(ParseUser.getCurrentUser()!=null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void login(View view) {
        /*added validation to check if email is blank*/

        if (!AppStatus.getInstance(this).isOnline()) {

            Toast.makeText(this, "You are not online!!!!", Toast.LENGTH_SHORT).show();

        } else {
            if (TextUtils.isEmpty(edEmail.getText())) {
                edEmail.setError("Email is required!");
                /*added validation to check if password is blank*/
            } else if (TextUtils.isEmpty(edPassword.getText())) {
                edPassword.setError("Password is required!");
            } else {
                final ProgressDialog progress = new ProgressDialog(this);
                progress.setMessage("Loading ...");
                progress.show();
                ParseUser.logInInBackground(edEmail.getText().toString(), edPassword.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        progress.dismiss();
                        if (parseUser != null) {
                            String location = ParseUser.getCurrentUser().get("location").toString();
                            String zipcode = ParseUser.getCurrentUser().get("zipCode").toString();
                            final App globalVariable = (App) getApplicationContext();
//                        globalVariable.setLocation(location);
//                        globalVariable.setPincode(zipcode);
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("location", location);
                            editor.putString("zipCode", zipcode);
                            editor.commit();
                            Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            ParseUser.logOut();
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        }
    }

    public void signup(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }


}
