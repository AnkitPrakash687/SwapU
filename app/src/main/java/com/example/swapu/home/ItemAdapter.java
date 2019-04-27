package com.example.swapu.home;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swapu.R;
import com.example.swapu.model.ItemModel;

import java.util.List;

import static com.example.swapu.common.ComUtils.getFormattedDate;

public class ItemAdapter extends ArrayAdapter<ItemModel> {
    Context context;
    int layoutResourceId;
    List<ItemModel> item = null;

    public ItemAdapter(Context context, int resource, List<ItemModel> objects) {
        super(context, 0, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.item = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(layoutResourceId, parent, false);
        ImageView imageview = convertView.findViewById(R.id.image_imageview);
        TextView title = convertView.findViewById(R.id.title_textview);
        TextView date = convertView.findViewById(R.id.date_textview);
        TextView price = convertView.findViewById(R.id.price_textview);
        ImageView tradeIb = convertView.findViewById(R.id.trade_ib);
        ItemModel itemModel = item.get(position);
        imageview.setImageBitmap(itemModel.getImage());
        title.setText(itemModel.getTitle());
        title.setTag(itemModel.getObjectId());
        date.setText(getFormattedDate(itemModel.getPostDate()));
        price.setText("$" + itemModel.getPrice());
        if (itemModel.isTrade()) {
            tradeIb.setImageResource(R.drawable.applogoswapu);
        }
        return convertView;
    }
}
