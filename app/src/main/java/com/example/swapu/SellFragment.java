package com.example.swapu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class SellFragment extends Fragment {
    private View mContentView = null;
    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_sell , null);
       // return inflater.inflate(R.layout.fragment_sell, container, false);
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button gallery = view.findViewById(R.id.gallery_button);
        final Button post = view.findViewById(R.id.post_button);
        final EditText location = mContentView.findViewById(R.id.location_edittext);
        String locationDetails=null;
        Spinner staticSpinner = view.findViewById(R.id.productType_spinner);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.product_type_array,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        gallery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),3);
            }
        });


        post.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ImageButton imageButton = mContentView.findViewById(R.id.imageButton);
                BitmapDrawable drawable = (BitmapDrawable)imageButton.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                TextView titleTextView = mContentView.findViewById(R.id.title_editText);
                TextView descTextView = mContentView.findViewById(R.id.desc_editText);
                Spinner productTypeSpinner = mContentView.findViewById(R.id.productType_spinner);
                Switch tradeSwitch = mContentView.findViewById(R.id.trade_switch);
                TextView city = mContentView.findViewById(R.id.city_textview);
                EditText location = mContentView.findViewById(R.id.location_edittext);
                EditText price = mContentView.findViewById(R.id.price_edittext);
                String title = titleTextView.getText().toString().trim();
                String desc = descTextView.getText().toString().trim();
                String productType = productTypeSpinner.getSelectedItem().toString();
                String productPrice = price.getText().toString();
                String zipcode = location.getText().toString();
                String cityState = city.getText().toString();
                boolean trade = (tradeSwitch.isChecked()?true:false);
                /*added input validation to check if product is empty*/
                if(TextUtils.isEmpty(title)) {
                    titleTextView.setError("Empty title");
                }else if(TextUtils.isEmpty(desc)){
                    descTextView.setError("Empty description");
                }else if(TextUtils.isEmpty(location.getText().toString())) {
                    location.setError("Empty Zip Code");
                }else if(TextUtils.isEmpty(productPrice)) {
                    price.setError("Empty price");
                }else {
                        if(city.getText().toString().isEmpty()){
                            checkLocation();
                        }
                    ParseQuery<ParseObject> query = new ParseQuery<>("Product");
                    query.findInBackground(new FindCallback<ParseObject>() {
                                               Bitmap bitmap;
                                               @Override
                                               public void done(List<ParseObject> objects, final ParseException e) {
                                                   if (e == null) {
                                                       // Adding objects into the Array
                                                       for (int i = 0; i < objects.size(); i++) {
                                                           String title = objects.get(i).getString("title");
                                                           ParseFile imagePng = objects.get(i).getParseFile("download");
                                                           Date postDate = objects.get(i).getCreatedAt();
                                                           imagePng.getDataInBackground(new GetDataCallback() {
                                                               @Override
                                                               public void done(byte[] data, ParseException e) {
                                                                   if (e == null) {
                                                                       // Decode the Byte[] into
                                                                       // Bitmap
                                                                       bitmap = BitmapFactory
                                                                               .decodeByteArray(
                                                                                       data, 0,
                                                                                       data.length);

                                                                   }

                                                               }
                                                           });
                                                        //   dataList.add(new ItemModel(title,postDate,bitmap));
                                                       }
                                                   } else {

                                                   }

                                               }
                                           });
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    //  ParseObject product = new ParseObject("Product");
                    final ParseObject product = new ParseObject("Product");
                    if(imageButton.getTag().toString().equals("2")) {
                        product.put("download", conversionBitmapParseFile(bitmap));
                    }else{
                        showErrorMessage("Seems you have not uploaded any pictures");
                    }
                    product.put("title", title);
                    product.put("description", desc);
                    product.put("trade", trade);
                    product.put("productType", productType);
                   product.put("price", productPrice);
                    product.put("location", cityState);
                    product.put("zipCode", zipcode);
                    product.put("username", currentUser.getUsername());

                    product.saveInBackground();
                }
            }


        });

        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (!hasFocus) {
                    checkLocation();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    //    ImageButton imageButton = getView().findViewByID(R.id.imageButton);
        Bitmap bitmap = null;
        Uri selectedImage = data.getData();

        try {
            switch (requestCode) {

                case 3:
                    if (resultCode == Activity.RESULT_OK) {
                        //data gives you the image uri. Try to convert that to bitmap
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                        ImageButton imageButton = mContentView.findViewById(R.id.imageButton);
                      //  Bitmap newBitmap = Bitmap.createScaledBitmap (bitmap,50,50,false);
                        imageButton.setImageBitmap(bitmap);
                        imageButton.setTag(2);
                        break;
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Log.e(TAG, "Selecting picture cancelled");
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in onActivityResult : " + e.getMessage());
        }
    }
    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
    }
    private void showErrorMessage(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight).create();
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
    private int countWord(String s){
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length;
    }

    private void checkLocation(){
        Geocoder myLocation = new Geocoder(getContext(), Locale.getDefault());
        EditText location = mContentView.findViewById(R.id.location_edittext);
        TextView city = mContentView.findViewById(R.id.city_textview);
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
}
