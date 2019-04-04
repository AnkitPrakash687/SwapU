package com.example.swapu.home.account;

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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.swapu.App;
import com.example.swapu.common.AppStatus;
import com.example.swapu.common.ComUtils;
import com.example.swapu.home.ItemAdapter;
import com.example.swapu.login.LoginActivity;
import com.example.swapu.R;
import com.example.swapu.model.ItemModel;
import com.parse.FindCallback;
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

public class AccountFragment extends Fragment {
    @Nullable
    private View mContentView = null;
    protected Context context;
    ItemAdapter myofferAdapter;
    ListView myofferListview;
    String accountName;
    String doj;
    TextView accountNameTv, dojTv;
    public ArrayList<ItemModel> dataList = new ArrayList();
    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_account , null);
        // return inflater.inflate(R.layout.fragment_sell, container, false);
        context = container.getContext();
        return mContentView;
    }

    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button logout = mContentView.findViewById(R.id.logout_button);
        accountNameTv = mContentView.findViewById(R.id.account_name);
        dojTv = mContentView.findViewById(R.id.account_doj);
        myofferListview = mContentView.findViewById(R.id.myoffer_listview);
        myofferAdapter = new ItemAdapter(context, R.layout.item_myoffer, dataList);

        accountName = ParseUser.getCurrentUser().getString("name");
        doj = getFormattedDate(ParseUser.getCurrentUser().getCreatedAt());
        accountNameTv.setText(accountName);
        dojTv.setText("Joined: "+doj);
        findObjects();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progress = new ProgressDialog(getContext());
                progress.setMessage("Loading ...");
                progress.show();
                ParseUser.logOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                progress.dismiss();
            }
        });
    }
    private void findObjects() {
        if (AppStatus.getInstance(context).isOnline()) {
            // Configure Query
            ParseQuery<ParseObject> query = new ParseQuery<>("items");

            String currentUser = ParseUser.getCurrentUser().getUsername();
            //String email = currentUser.getEmail();
            query.whereEqualTo("username", currentUser);

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
                                            myofferListview.setAdapter(myofferAdapter);
                                            myofferAdapter.notifyDataSetChanged();
                                        }

                                    }
                                });
                            }
                        }


                    } else {

                    }

                }
            });
        }else{
            Toast.makeText(context,"You're not online", Toast.LENGTH_LONG).show();
        }
    }


    public static ProgressDialog showProgressDialog(FragmentActivity activity, String message) {
        ProgressDialog m_Dialog = new ProgressDialog(activity);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(false);
        m_Dialog.show();
        return m_Dialog;

    }
    }


