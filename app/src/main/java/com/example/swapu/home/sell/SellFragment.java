package com.example.swapu.home.sell;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swapu.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class SellFragment extends Fragment {
    private View mContentView = null;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 4;
    private Uri uriFilePath;
    private List<Bitmap> bitmapList;
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
        final Button capture = view.findViewById(R.id.capture_button);
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
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3);

            }
        });

        capture.setOnClickListener(new View.OnClickListener()
        {
            Intent intent;
            @Override
            public void onClick(View v)
            {
                if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });


        post.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ImageButton imageButton = mContentView.findViewById(R.id.imageButton);
                BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
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
                String locality="";
                boolean trade = (tradeSwitch.isChecked() ? true : false);
                /*added input validation to check if product is empty*/
                if (TextUtils.isEmpty(title)) {
                    titleTextView.setError("Empty title");
                    /*added input validation to check if product description is empty*/
                } else if (TextUtils.isEmpty(desc)) {
                    descTextView.setError("Empty description");
                    /*added input validation to check if product location is empty*/
                } else if (TextUtils.isEmpty(location.getText().toString())) {
                    location.setError("Empty Zip Code");
                    /*added input validation to check if product price is empty*/
                } else if (TextUtils.isEmpty(productPrice)) {
                    price.setError("Empty price");
                } else if (city.equals("Wrong Zip Code")) {
                    showErrorMessage("Enter correct Zip Code");
                } else if (!imageButton.getTag().toString().equals("2")) {
                    showErrorMessage("Seems you have not uploaded any pictures");
                }else {

                  if (city.getText().toString().isEmpty()) {
                        locality = getLocation(location.getText().toString());
                    }
                    if(locality.equals("Wrong Zip Code")) {
                            showErrorMessage("Enter Correct Zip Code");
                    }else{
                        final ParseObject product = new ParseObject("items");
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        // added validation to check if the user selected an image
                        product.put("download", conversionBitmapParseFile(bitmap));
                        product.put("title", title);
                        product.put("description", desc);
                        product.put("trade", trade);
                        product.put("productType", productType);
                        product.put("price", Integer.parseInt(productPrice));
                        product.put("location", cityState);
                        product.put("zipCode", Integer.parseInt(zipcode));
                        product.put("username", currentUser.getUsername());

                        product.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    Toast.makeText(getActivity(), "Item Posted", Toast.LENGTH_LONG).show();
                                } else {

                                    Toast.makeText(getActivity(), e.getMessage()+" Error in posting item", Toast.LENGTH_LONG).show();
                                }
                                // Here you can handle errors, if thrown. Otherwise, "e" should be null
                            }
                        });
                    }
                }
            }


        });

        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (!hasFocus) {
                    TextView city = mContentView.findViewById(R.id.city_textview);
                    String loc = getLocation(location.getText().toString());
                    city.setText(loc);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (uriFilePath != null)
            outState.putString("uri_file_path", uriFilePath.toString());
        super.onSaveInstanceState(outState);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    //    ImageButton imageButton = getView().findViewByID(R.id.imageButton);
        Bitmap bitmap = null;
        if(data != null) {
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
                    case CAMERA_REQUEST:
                        if (resultCode == Activity.RESULT_OK) {
                            //data gives you the image uri. Try to convert that to bitmap
                            bitmap = (Bitmap) data.getExtras().get("data");
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

    private String getLocation(String zipCode){
        Geocoder myLocation = new Geocoder(getContext(), Locale.getDefault());
        String locality="";
        try {
            List<Address> myList = myLocation.getFromLocationName(zipCode, 5);
            if(myList.size()>0){
                locality = myList.get(0).getLocality() + ", "+myList.get(0).getAdminArea();
            }else{
                locality = "Wrong Zip Code";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locality;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}
