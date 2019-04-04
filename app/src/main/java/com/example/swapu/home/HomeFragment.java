package com.example.swapu.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.swapu.App;
import com.example.swapu.model.ItemModel;
import com.example.swapu.home.map.MapsActivity;
import com.example.swapu.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    private View mContentView = null;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    String lat;
    String provider;
    String locationDetails = null;
    Bitmap imageBmp;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;
    public ArrayList<ItemModel> dataList = new ArrayList();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_home, container, false);
        context = container.getContext();

        final App globalVariable = (App) context.getApplicationContext();
        GridView itemGridview = mContentView.findViewById(R.id.item_gridview);
        Button location = mContentView.findViewById(R.id.location_button);
       // getNearByLocation();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String zipCode = sharedPreferences.getString("zipCode","");
        String city = sharedPreferences.getString("location","");
        location.setText(city.split(",")[0]);
        findObjects();
        ItemAdapter itemAdapter = new ItemAdapter(context, R.layout.gridview_items, dataList);
        itemGridview.setAdapter(itemAdapter);



        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button location = mContentView.findViewById(R.id.location_button);
        GridView itemGridview = mContentView.findViewById(R.id.item_gridview);
      //  ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
       // Query Parameters


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getActivity(), MapsActivity.class);
                startActivityForResult(mapIntent, 1);
            }

        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                locationDetails = data.getStringExtra("location");
                if(dataList.size()>0){
                    dataList.clear();
                }
                findObjects();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }
        Button location = mContentView.findViewById(R.id.location_button);
        if (locationDetails != null) {
            String place[] = locationDetails.split(",");
//            final App globalVariable = (App) context.getApplicationContext();
//            globalVariable.setLocation(place[0] + ", " + place[1]);
//            globalVariable.setPincode(place[2]);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("location", place[0] + ", " + place[1]);
            editor.putString("zipCode", place[2]);
            editor.commit();
            String city = sharedPreferences.getString("location","");
            location.setText(city.split(",")[0]);


        }

    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            onStart();
        }
    }

    private void findObjects() {


        final GridView listView = mContentView.findViewById(R.id.item_gridview);
        final App globalVariable = (App) context.getApplicationContext();
        // Configure Query
        ParseQuery<ParseObject> query = new ParseQuery<>("items");

        ParseUser currentUser = ParseUser.getCurrentUser();
        String email = currentUser.getEmail();
        // Query Parameters
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String zipCode = sharedPreferences.getString("zipCode","");

        query.whereEqualTo("zipCode", Integer.parseInt(zipCode.trim()));

        // Sorts the results in ascending order by the itemName field
        //  query.orderByAscending("itemName");
        String c = query.getClassName();
       // final ProgressDialog progressDialog = showProgressDialog(getActivity(),"loading");
        query.findInBackground(new FindCallback<ParseObject>() {
            Bitmap bitmap;

            @Override
            public void done(List<ParseObject> objects, final ParseException e) {
           //     progressDialog.dismiss();
                if (e == null) {

                    // Adding objects into the Array
                    for (int i = 0; i < objects.size(); i++) {
                        ParseFile file = objects.get(i).getParseFile("download");
                        final String title = objects.get(i).getString("title");
                        //   ParseFile file = (ParseFile) objects.get(i).get("download");

                        final Date postDate = objects.get(i).getCreatedAt();
                        if (file != null) {
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        // Decode the Byte[] into
                                        // Bitmap
                                        bitmap = BitmapFactory
                                                .decodeByteArray(
                                                        data, 0,
                                                        data.length);

                                        dataList.add(new ItemModel(title, postDate, bitmap));
                                        ItemAdapter itemAdapter = new ItemAdapter(context, R.layout.gridview_items, dataList);
                                        itemAdapter.notifyDataSetChanged();

                                    }

                                }
                            });
                        }

                    }
                } else {

                }

            }
        });
    }

    public static ProgressDialog showProgressDialog(Activity activity, String message) {
        ProgressDialog m_Dialog = new ProgressDialog(activity);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(false);
        m_Dialog.show();
        return m_Dialog;

    }

    public void getNearByLocation(){
        String googleAPIKey = getResources().getString(R.string.google_maps_key);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
      //  String requesturl="https://maps.googleapis.com/maps/api/place/search/json?radius=500&sensor=false&key="+googleAPIKey+"&location=13.01,74.79";
        String requesturl = "http://pastebin.com/raw/2bW31yqa";
        // final ProgressDialog progressDialog = showProgressDialog(getActivity(),"nearby");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, requesturl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                       String res = response.toString();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error


                    }
                });
        requestQueue.add(jsonObjectRequest);


}
}
