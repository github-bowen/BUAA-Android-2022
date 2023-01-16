package com.example.success;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class FriendAdapter extends ArrayAdapter<Friend> {
    private int resourceId;
    public FriendAdapter(Context context, int textViewResourceId, List<Friend> friends) {
        super(context,textViewResourceId,friends);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Friend friend = getItem(position);
        View view;
        FriendViewHolder viewHolder;
        if(convertView==null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new FriendViewHolder();
            viewHolder.portrait = (ImageView) view.findViewById(R.id.friendPortrait);
            viewHolder.friendName = (TextView) view.findViewById(R.id.friendName);
            viewHolder.memoryNum = (TextView) view.findViewById(R.id.memoryNum);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (FriendViewHolder) view.getTag();
        }
        byte[] picture = friend.getPortraitId();
        Bitmap bitmap = null;
        if (picture != null) {
            bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        } else {
            // FIXME: 没有图片，随便搞个
            String path="./pxt.jpg";
            FileInputStream is= null;
            try {
                is = new FileInputStream(path);
                try {
                    bitmap=BitmapFactory.decodeFileDescriptor(is.getFD());
                }catch (IOException e){
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

//            byte[] arr = {1, 2, 3};
//            bitmap = BitmapFactory.decodeByteArray(arr, 0, 3);
        }
        viewHolder.portrait.setImageBitmap(bitmap);
        viewHolder.friendName.setText(friend.getFriendName());
        viewHolder.memoryNum.setText(String.valueOf(friend.getMemoryNum()));
        return view;
    }
}

