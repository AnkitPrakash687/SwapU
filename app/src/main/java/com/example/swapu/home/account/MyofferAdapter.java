package com.example.swapu.home.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swapu.R;
import com.example.swapu.model.ItemModel;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import static com.example.swapu.common.ComUtils.getFormattedDate;

public class MyofferAdapter extends ArrayAdapter<ItemModel> {
    Context context;
    int layoutResourceId;
    List<ItemModel> item = null;
    String objectId;
    public MyofferAdapter( Context context, int resource, List<ItemModel> objects) {
        super(context, 0, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.item = objects;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(layoutResourceId, parent, false);
        ImageView imageview = convertView.findViewById(R.id.image_imageview_offer);
        TextView title = convertView.findViewById(R.id.title_textview_offer);
        TextView date = convertView.findViewById(R.id.date_textview_offer);
        TextView price = convertView.findViewById(R.id.price_textview_offer);
        ImageView tradeIb = convertView.findViewById(R.id.trade_ib_offer);
        ImageButton deleteBtn = convertView.findViewById(R.id.deleteBtn_offer);
        ItemModel itemModel = item.get(position);
        imageview.setImageBitmap(itemModel.getImage());
        title.setText(itemModel.getTitle());
        title.setTag(itemModel.getObjectId());
        date.setText(getFormattedDate(itemModel.getPostDate()));
        price.setText("$" + itemModel.getPrice());
        if (itemModel.isTrade()) {
            tradeIb.setImageResource(R.drawable.applogoswapu);
        }
        objectId = itemModel.getObjectId();
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete Product")
                        .setMessage("Are you sure you want to delete this product?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation

                                ParseQuery<ParseObject> query = ParseQuery.getQuery("items");
                                query.getInBackground(objectId, new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {

                                        try {
                                            object.delete();
                                            object.saveInBackground();
                                            item.remove(position);
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "Deleted Successfully.", Toast.LENGTH_SHORT).show();


                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }


                                    }
                                });

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

        return convertView;
    }
}
