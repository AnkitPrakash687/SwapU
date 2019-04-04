package com.example.swapu.home;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.swapu.common.AppStatus;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

class GetNearByLocation extends AsyncTask<Void, Void, JSONObject> {

    private GetNearByAsyncTaskCallback mCallback = null;
    private String url;
    private Context context;

    public GetNearByLocation(String url, GetNearByAsyncTaskCallback callback, Context context) {
        mCallback = callback;
        this.url = url;
        this.context = context;

    }

    protected JSONObject doInBackground(Void... v) {
        if (!AppStatus.getInstance(context).isOnline()) {
            String str = "{\"isNotOnline\":true}";
            try {
                JSONObject isNotOnline = new JSONObject(str);
                return isNotOnline;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        } else {
            InputSource is;
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(this.url);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }

                return new JSONObject(stringBuffer.toString());
            } catch (Exception ex) {
                Log.e("App", "yourDataTask", ex);
                return null;
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    @Override
    protected void onPostExecute(JSONObject result) {
        // Finally, the result of doInBackground is handled on UI thread here.

        if (mCallback != null) {
            mCallback.onPostExecute(result);
        }
    }

    /**
     * Interface for Activity callback
     */
    public interface GetNearByAsyncTaskCallback {

        void onPostExecute(JSONObject result);
        /* Add other callback method if necessary */
        // void onProgressUpdate();
    }
}
