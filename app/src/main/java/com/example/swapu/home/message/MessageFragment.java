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
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.HashMap;
import java.util.List;

import static com.example.swapu.common.ComUtils.getFormattedDate;

public class MessageFragment extends Fragment {
    private View mContentView = null;
    protected Context context;
    SwipeRefreshLayout pullToRefresh;
    Bitmap imageBmp;
    public ArrayList<MessageModel> dataList = new ArrayList();
    MessageAdapter messageAdapter;
    ListView messageGridview;
    ProgressDialog progressDialog;
    ArrayList<String> chatHeadId = new ArrayList<>();
    Date timestamp;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_messages, container, false);
        context = container.getContext();
        return mContentView;
    }

    public static ProgressDialog showProgressDialog(FragmentActivity activity, String message) {
        ProgressDialog m_Dialog = new ProgressDialog(activity);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(false);
        m_Dialog.show();
        return m_Dialog;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pullToRefresh = (SwipeRefreshLayout) mContentView.findViewById(R.id.pullToRefresh);
        messageGridview = mContentView.findViewById(R.id.messages_gridview);
        messageAdapter = new MessageAdapter(context, R.layout.item_messages, dataList);
        runQuery();

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

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (dataList.size() > 0) {
                    dataList.clear();
                }
                runQuery();
                pullToRefresh.setRefreshing(false);
            }
        });


    }

    public void runQuery() {
        String currentUser = ParseUser.getCurrentUser().getUsername();
        final HashMap<String, String> chatHeadId = new HashMap<>();
        ParseQuery<ParseObject> query1 = new ParseQuery<>("ChatThread");
        query1.whereEqualTo("recipientUsername", currentUser);
        ParseQuery<ParseObject> query2 = new ParseQuery<>("ChatThread");
        query2.whereEqualTo("senderUsername", currentUser);
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(query1);
        queries.add(query2);
        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);

        try {
            // Fetches the posts with more than 1000 likes OR posts with the title "My great post"
            List<ParseObject> postList = mainQuery.find();

            for (ParseObject post : postList) {
                if (post.getString("senderUsername").equals(currentUser)) {
                    chatHeadId.put(post.getString("recipientUsername"), post.getObjectId());
                } else {
                    chatHeadId.put(post.getString("senderUsername"), post.getObjectId());
                }
            }

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereContainedIn("username", chatHeadId.keySet());
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
//
//                            String lastMessage = object.getString("body");
//                            String postDate = getFormattedDate(object.getCreatedAt());
                        for (ParseUser u : objects) {
                            final String receiver = u.getUsername();
                            final String receiverName = u.getString("name");
                            final String postDate = "";
                            final String lastMessage = "";
                            final String objectId = chatHeadId.get(receiver);
                            ParseFile file = u.getParseFile("profilePic");
                            if (file != null) {
                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            // Decode the Byte[] into
                                            // Bitmap
                                            final Bitmap profilePic = BitmapFactory
                                                    .decodeByteArray(
                                                            data, 0,
                                                            data.length);
                                            ParseQuery<ParseObject> query = new ParseQuery<>("Message");
                                            query.whereEqualTo("threadId", objectId);
                                            query.whereEqualTo("senderId", receiver);
                                            query.addDescendingOrder("createdAt");
                                            try {
                                                if (query.count() != 0) {

                                                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                                                        @Override
                                                        public void done(ParseObject object, ParseException e) {
                                                            if (e == null) {
                                                                String lastMessage = object.getString("body");
                                                                String postDate = getFormattedDate(object.getCreatedAt());
                                                                dataList.add(new MessageModel(receiver, receiverName, lastMessage, postDate, profilePic, objectId));
                                                                messageGridview.setAdapter(messageAdapter);
                                                                messageAdapter.notifyDataSetChanged();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    dataList.add(new MessageModel(receiver, receiverName, lastMessage, postDate, profilePic, objectId));
                                                    messageGridview.setAdapter(messageAdapter);
                                                    messageAdapter.notifyDataSetChanged();
                                                }
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }
                                        }

                                    }
                                });
                            }
                        }
                    }

                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

