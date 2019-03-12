package com.example.swapu;

import android.os.AsyncTask;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FetchData extends AsyncTask<Void,Void,Void> {
    String data = "";
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("http://pastebin.com/raw/2bW31yqa");
            HttpURLConnection httpURLConnection =(HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            while (line != null){
                line = bufferedReader.readLine();
                data = line + data;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
       HomeActivity.data = data;
    }
}
