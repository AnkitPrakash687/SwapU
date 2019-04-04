package com.example.swapu.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.swapu.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.example.swapu.common.ComUtils.getResizedBitmap;

public class ChatActivity extends AppCompatActivity {
    static final String TAG = ChatActivity.class.getSimpleName();
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";
    RecyclerView rvChat;
    String userId;
    String chatId;
    String receiverName;
    String receiverId;
    ArrayList<Message> mMessages;
    ChatListAdapter mAdapter;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    EditText etMessage;
    ImageButton btSend;
    ImageButton chatProPicIb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        chatId = intent.getStringExtra("chatId");
        receiverName = intent.getStringExtra("receiverName");
        receiverId = intent.getStringExtra("receiver");
        chatProPicIb = (ImageButton) findViewById(R.id.chat_pro_pic);
        setRecipientProPic();

        refreshMessages();
        // User login
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        }
        //else { // If not logged in, login as a new anonymous user
//                login();
//            }
        ParseLiveQueryClient parseLiveQueryClient = null;
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://swapu.back4app.io/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
        parseQuery.whereEqualTo("threadId", chatId);
        // This query can even be more granular (i.e. only refresh if the entry was added by some other user)
        // parseQuery.whereNotEqualTo(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());

        // Connect to Parse server
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        // Listen for CREATE events
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new
                SubscriptionHandling.HandleEventCallback<Message>() {
                    @Override
                    public void onEvent(ParseQuery<Message> query, Message object) {

                        mMessages.add(0, object);

                        // RecyclerView updates need to be run on the UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                rvChat.scrollToPosition(0);
                            }
                        });
                    }
                });
    }

    private void setRecipientProPic() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", receiverId);
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

                                chatProPicIb.setImageBitmap(getResizedBitmap(profilePic, 200, 200));
                            }

                        }
                    });
                }
            }
        });
    }

    //Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
//        void login() {
//            ParseAnonymousUtils.logIn(new LogInCallback() {
//                @Override
//                public void done(ParseUser user, ParseException e) {
//                    if (e != null) {
//                        Log.e(TAG, "Anonymous login failed: ", e);
//                    } else {
//                        startWithCurrentUser();
//                    }
//                }
//            });


    // Setup message field and posting
    void setupMessagePosting() {
        // Find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (ImageButton) findViewById(R.id.btSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        // final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatListAdapter(ChatActivity.this, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        rvChat.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                //ParseObject message = ParseObject.create("Message");
                //message.put(Message.USER_ID_KEY, userId);
                //message.put(Message.BODY_KEY, data);
                // Using new `Message` Parse-backed model now

                Message message = new Message();
                message.setChatId(chatId);
                message.setBody(data);
                message.setUserId(ParseUser.getCurrentUser().getUsername());
                message.setUserName(ParseUser.getCurrentUser().getString("name"));

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(ChatActivity.this, "Successfully created message on Parse",
                                Toast.LENGTH_SHORT).show();
                        refreshMessages();
                    }
                });
                etMessage.setText(null);
            }
        });
    }

    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery("Message");
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.whereEqualTo("threadId", chatId);
        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvChat.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }
}





