package com.example.swapu.home.product;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swapu.R;
import com.example.swapu.chat.ChatActivity;
import com.example.swapu.home.HomeActivity;
import com.example.swapu.login.LoginActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.swapu.common.ComUtils.getResizedBitmap;

public class ProductActivity extends AppCompatActivity {
    String title, objectId, zipCode;
    ImageView image;
    TextView priceTv, locationTv, descTv, sellerNameTv, sellerLoactionTv;
    Button sendBtn;
    String receiver;
    String chatId;
    String sender;
    ImageButton sellerProPicIb;
    String receiverName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        zipCode = intent.getStringExtra("zipCode");
        objectId = intent.getStringExtra("objectId");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("items");
        priceTv = findViewById(R.id.product_price_textView);
        locationTv = findViewById(R.id.product_location_textView);
        descTv = findViewById(R.id.product_description_textView);
        image = findViewById(R.id.product_imageView);
        sendBtn = findViewById(R.id.send_message_button);
        sender = ParseUser.getCurrentUser().getUsername();
        sellerNameTv = findViewById(R.id.sellerNameTv);
        sellerLoactionTv = findViewById(R.id.sellerLocationTv);
        sellerProPicIb = findViewById(R.id.sellerProPicIb);
        // The query will search for a ParseObject, given its objectId.
        // When the query finishes running, it will invoke the GetCallback
        // with either the object, or the exception thrown
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            Bitmap bitmap;
            ProgressDialog progressDialog = showProgressDialog(ProductActivity.this, "loading");

            public void done(ParseObject result, ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    ParseFile file = result.getParseFile("download");
                    priceTv.setText("$" + result.getInt("price"));
                    locationTv.setText(result.getString("location"));
                    descTv.setText(result.getString("description"));
                    receiver = result.getString("username");
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
                                    //image.setImageBitmap(ComUtils.getResizedBitmap(bitmap, 400, 300));
                                    image.setImageBitmap(bitmap);
                                }

                            }
                        });
                        getSellerInformation();
                    } else {
                        // something went wrong
                    }
                }
            }


        });


        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkChatThread();

            }

        });
    }

    private void getSellerInformation() {
        ParseQuery<ParseUser> sellerQuery = ParseUser.getQuery();
        sellerQuery.whereEqualTo("username", receiver);
        sellerQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    sellerNameTv.setText(object.getString("name"));
                    sellerLoactionTv.setText(object.getString("location"));
                    ParseFile file = object.getParseFile("profilePic");
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                // Decode the Byte[] into
                                // Bitmap
                                Bitmap profilePic = BitmapFactory
                                        .decodeByteArray(
                                                data, 0,
                                                data.length);
                                sellerProPicIb.setImageBitmap(getResizedBitmap(profilePic, 200, 200));
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkChatThread() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ChatThread");
        query.whereContainedIn("recipientUsername", Arrays.asList(receiver, sender));
        query.whereContainedIn("senderUsername", Arrays.asList(receiver, sender));
        final ProgressDialog progressDialog = showProgressDialog(this, "loading data");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                progressDialog.dismiss();
                if (e == null) {

                    if (objects.size() > 0) {
                        chatId = objects.get(0).getObjectId();
                        Intent intent = new Intent(ProductActivity.this, ChatActivity.class);
                        intent.putExtra("receiver", receiver);
                        intent.putExtra("chatId", chatId);
                        intent.putExtra("sender", sender);
                        if (ParseUser.getCurrentUser().getUsername().equals(receiver)) {
                            getUserName(sender, intent);
                        } else {
                            getUserName(receiver, intent);
                        }


                    } else {

                        final ParseObject chatThread = new ParseObject("ChatThread");
                        chatThread.put("senderUsername", sender);
                        chatThread.put("recipientUsername", receiver);
                        chatThread.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    chatId = chatThread.getObjectId();
                                    Intent intent = new Intent(ProductActivity.this, ChatActivity.class);
                                    intent.putExtra("receiver", receiver);
                                    intent.putExtra("chatId", chatId);
                                    intent.putExtra("sender", sender);
                                    if (ParseUser.getCurrentUser().getUsername().equals(receiver)) {
                                        getUserName(sender, intent);
                                    } else {
                                        getUserName(receiver, intent);
                                    }

                                }
                            }
                        });
                    }


                }
            }
        });
    }

    private void getUserName(String username, final Intent intent) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                receiverName = objects.get(0).getString("name");
                intent.putExtra("receiverName", receiverName);
                startActivity(intent);
            }
        });
    }

    public ProgressDialog showProgressDialog(Activity activity, String message) {
        ProgressDialog m_Dialog = new ProgressDialog(activity);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(false);
        m_Dialog.show();
        return m_Dialog;

    }

}
