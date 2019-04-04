package com.example.swapu.home.message;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swapu.R;
import com.example.swapu.model.ItemModel;
import com.example.swapu.model.MessageModel;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<MessageModel> {
    Context context;
    int layoutResourceId;
    List<MessageModel> item = null;
    public MessageAdapter(Context context, int resource, List<MessageModel> objects) {
        super(context, 0, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.item = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(layoutResourceId, parent, false);
        ImageView imageview = convertView.findViewById(R.id.message_pro_pic);
        TextView name = convertView.findViewById(R.id.messages_name);
        TextView date = convertView.findViewById(R.id.message_date);
        TextView lastMessage = convertView.findViewById(R.id.messages_lastMessage);
        MessageModel messageModel = item.get(position);
        imageview.setImageBitmap(messageModel.getImage());
        name.setText(messageModel.getReceiverName());
        date.setText(messageModel.getPostDate());
        lastMessage.setText(messageModel.getLastMessage());
        return convertView;
    }
}
