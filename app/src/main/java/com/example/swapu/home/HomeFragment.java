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
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swapu.App;
import com.example.swapu.R;
import com.example.swapu.common.ComUtils;
import com.example.swapu.home.map.MapsActivity;
import com.example.swapu.home.product.ProductActivity;
import com.example.swapu.model.ItemModel;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements GetNearByLocation.GetNearByAsyncTaskCallback {
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
    final static String KEY = "HOME_CACHE";
    ItemAdapter itemAdapter;
    ListView itemGridview;
    Button location;
    List<Integer> nearByZipCodeList;
    ArrayList<ItemModel> itemModels;
    String city;
    SharedPreferences sharedPreferences;
    String searchText;
    Button searchButton;
    private Toolbar toolbar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_home, container, false);
        context = container.getContext();
        final App globalVariable = (App) context.getApplicationContext();

        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button location = mContentView.findViewById(R.id.location_button);

        // Setting toolbar as the ActionBar with setSupportActionBar() call
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String zipCode = sharedPreferences.getString("zipCode", "");
        city = sharedPreferences.getString("location", "");
        itemGridview = mContentView.findViewById(R.id.item_gridview);
        location = mContentView.findViewById(R.id.location_button);
        location.setText(city.split(",")[0]);

        itemAdapter = new ItemAdapter(context, R.layout.gridview_items, dataList);
        getNearByLocation();
      //  ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
       // Query Parameters
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getActivity(), MapsActivity.class);
                startActivityForResult(mapIntent, 1);
            }

        });

        searchButton = mContentView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getActivity(), SearchActivity.class);
                startActivityForResult(mapIntent, 6);
            }
        });

        // Set an item click listener for GridView widget
        itemGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView title = view.findViewById(R.id.title_textview);
                String objectId = title.getTag().toString();
                Intent productIntent = new Intent(getContext(), ProductActivity.class);
                productIntent.putExtra("objectId", objectId);
                productIntent.putExtra("title", title.getText().toString());
                productIntent.putExtra("zipCode", zipCode);
                startActivity(productIntent);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                locationDetails = data.getStringExtra("location");
                refresh();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == 6) {
            if (resultCode == Activity.RESULT_OK) {
                searchText = data.getStringExtra("searchText");
                searchButton.setText(searchText);
                refresh();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }


    public void refresh() {
        if (dataList.size() > 0) {
            dataList.clear();
            itemAdapter.notifyDataSetChanged();
        }

        city = sharedPreferences.getString("location", "");
        location = mContentView.findViewById(R.id.location_button);
            location.setText(city.split(",")[0]);
        getNearByLocation();


    }


    public ProgressDialog showProgressDialog(FragmentActivity activity, String message) {
        ProgressDialog m_Dialog = new ProgressDialog(activity);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(false);
        m_Dialog.show();
        return m_Dialog;

    }

    public void getNearByLocation() {

        //  ProgressDialog progressDialog = showProgressDialog(getActivity(),"loading Near By Locations");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String zipCode = sharedPreferences.getString("zipCode", "");
        nearByZipCodeList = new ArrayList<>();
        nearByZipCodeList.add(Integer.parseInt(zipCode.trim()));
        String distance = "30";
        StringBuilder url = new StringBuilder();
        url.append("https://www.zipcodeapi.com/rest/j8e1e5TnyhPXPweFQMWcKRExQOxtdAoxviq6JCJeYfiunhnKW965UBd4iAE8vRzj/radius.json/");
        url.append(zipCode.trim() + "/");
        url.append(distance + "/mile");
        // String urll = " https://www.zipcodeapi.com/rest/j8e1e5TnyhPXPweFQMWcKRExQOxtdAoxviq6JCJeYfiunhnKW965UBd4iAE8vRzj/radius.json/64468/100/mile";

        new GetNearByLocation(url.toString(), this, context).execute();

    }

    @Override
    public void onPostExecute(JSONObject jsonResponse) {

        if (jsonResponse != null) {
            JSONArray zipCodes = null;
            try {

                if (!jsonResponse.isNull("isNotOnline") && jsonResponse.getBoolean("isNotOnline")) {
                    Toast.makeText(context, "you are not online", Toast.LENGTH_LONG).show();
                } else {
                    zipCodes = jsonResponse.getJSONArray("zip_codes");

                    for (int i = 0; i < zipCodes.length(); i++) {
                        JSONObject zipCode = zipCodes.getJSONObject(i);
                        nearByZipCodeList.add(Integer.parseInt(zipCode.getString("zip_code").trim()));
                    }
                    findObjects();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void findObjects() {
        final GridView listView = mContentView.findViewById(R.id.item_gridview);
        final App globalVariable = (App) context.getApplicationContext();
        // Configure Query
        ParseQuery<ParseObject> query = new ParseQuery<>("items");

        String currentUser = ParseUser.getCurrentUser().getUsername();
        //String email = currentUser.getEmail();
        query.whereNotEqualTo("username", currentUser);
        // Query Parameters
        query.whereContainedIn("zipCode", nearByZipCodeList);
        if (searchText != null && !searchText.isEmpty()) {
            query.whereFullText("title", searchText);
        }
        // query.whereEqualTo("zipCode", Integer.parseInt(zipCode.trim()));

        // Sorts the results in ascending order by the itemName field
        //  query.orderByAscending("itemName");
        String c = query.getClassName();
        final ProgressDialog progressDialog = showProgressDialog(getActivity(), "loading data");
        query.findInBackground(new FindCallback<ParseObject>() {
            Bitmap bitmap;

            @Override
            public void done(List<ParseObject> objects, final ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    // Adding objects into the Array
                    for (int i = 0; i < objects.size(); i++) {
                        ParseFile file = objects.get(i).getParseFile("download");
                        final String title = objects.get(i).getString("title");
                        final Date postDate = objects.get(i).getCreatedAt();
                        final String objectId = objects.get(i).getObjectId();
                        final String price = Double.toString(objects.get(i).getDouble("price"));

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
                                        ItemModel itemModel = new ItemModel(title, postDate, ComUtils.getResizedBitmap(bitmap, 250,
                                                250), objectId, price);
                                        dataList.add(itemModel);
                                        itemGridview.setAdapter(itemAdapter);
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


}
