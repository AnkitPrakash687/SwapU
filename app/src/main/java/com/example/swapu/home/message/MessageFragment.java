package com.example.swapu.home.message;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.swapu.R;
import com.example.swapu.chat.ChatActivity;
import com.example.swapu.common.AppStatus;
import com.example.swapu.common.ComUtils;
import com.example.swapu.model.MessageModel;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.swapu.common.ComUtils.getFormattedDate;

public class MessageFragment extends Fragment{
    private View mContentView = null;
    protected Context context;
    Bitmap imageBmp;
    public ArrayList<MessageModel> dataList = new ArrayList();
    MessageAdapter messageAdapter;
    ListView messageGridview;
    ProgressDialog progressDialog;
    Date timestamp;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_messages, container, false);
        context = container.getContext();
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messageGridview = mContentView.findViewById(R.id.messages_gridview);
        messageAdapter = new MessageAdapter(context, R.layout.item_messages, dataList);
        findObjects();

        // Set an item click listener for GridView widget
        messageGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                chatIntent.putExtra("chatId", dataList.get(position).getObjectId());
                chatIntent.putExtra("receiverName", dataList.get(position).getReceiverName());
                chatIntent.putExtra("receiver", dataList.get(position).getReceiver());
                startActivity(chatIntent);
            }
        });


    }


    public static ProgressDialog showProgressDialog(FragmentActivity activity, String message) {
        ProgressDialog m_Dialog = new ProgressDialog(activity);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(false);
        m_Dialog.show();
        return m_Dialog;

    }
    private void findObjects() {
        if (AppStatus.getInstance(context).isOnline()) {
            String currentUser = ParseUser.getCurrentUser().getUsername();
            ParseQuery<ParseObject> query = new ParseQuery<>("ChatThread");
            query.whereEqualTo("recipientUsername", currentUser);
            progressDialog = showProgressDialog(getActivity(), "loading data");
            query.findInBackground(new FindCallback<ParseObject>() {
                Bitmap bitmap;

                @Override
                public void done(List<ParseObject> objects, final ParseException e) {
                    //  progressDialog.dismiss();
                    if (e == null) {

                        // Adding objects into the Array
                        for (int i = 0; i < objects.size(); i++) {
                            String receiver = objects.get(i).getString("senderUsername");
                            String objectId = objects.get(i).getObjectId();
                            getReceiverDetail(receiver, objectId);

                        }
                    } else {

                    }

                }

            });

            ParseQuery<ParseObject> query1 = new ParseQuery<>("ChatThread");
            query1.whereEqualTo("senderUsername", currentUser);
            //  progressDialog = showProgressDialog(getActivity(),"loading data");
            query1.findInBackground(new FindCallback<ParseObject>() {
                Bitmap bitmap;

                @Override
                public void done(List<ParseObject> objects, final ParseException e) {
                    progressDialog.dismiss();
                    if (e == null) {

                        // Adding objects into the Array
                        for (int i = 0; i < objects.size(); i++) {
                            String receiver = objects.get(i).getString("recipientUsername");
                            String objectId = objects.get(i).getObjectId();
                            getReceiverDetail(receiver, objectId);

                        }
                    } else {

                    }

                }
            });
        }else{
            Toast.makeText(context,"You're not online", Toast.LENGTH_LONG).show();
        }
    }

    void getReceiverDetail(String receiver, String objectId){

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", receiver);
        query.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> objects, final ParseException e) {
                String receiverName = objects.get(0).getString("name");
                ParseFile file = objects.get(0).getParseFile("profilePic");
                if (file != null) {
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
                                setMessageAdapter(profilePic, receiverName, receiver, objectId);

                            }

                        }
                    });
                }
            }
        });
    }

    void setMessageAdapter(Bitmap profilePic, String receiverName, String receiver, String objectId){
        ParseQuery<ParseObject> query = new ParseQuery<>("Message");
        query.whereEqualTo("threadId", objectId);
        query.whereEqualTo("senderId", receiver);
        query.addDescendingOrder("createdAt");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    String lastMessage = object.getString("body");
                   String postDate = getFormattedDate(object.getCreatedAt());
                    dataList.add(new MessageModel(receiver,receiverName,lastMessage, postDate, profilePic, objectId));
                    messageGridview.setAdapter(messageAdapter);
                    messageAdapter.notifyDataSetChanged();
                }else{
                    String lastMessage = "";
                    String postDate = "";
                    dataList.add(new MessageModel(receiver,receiverName,lastMessage, postDate, profilePic, objectId));
                    messageGridview.setAdapter(messageAdapter);
                    messageAdapter.notifyDataSetChanged();

                }
            }
        });
    }



    }

