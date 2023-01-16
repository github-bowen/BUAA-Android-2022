package com.example.success;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class KnowledgeAdapter extends ArrayAdapter<KnowledgeDetail> {
    private int resourceId;
    public KnowledgeAdapter(Context context, int textViewResourceId, List<KnowledgeDetail> Knowledges) {
        super(context, textViewResourceId, Knowledges);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        KnowledgeDetail knowledgeDetail = getItem(position);
        View view;
        KnowledgeViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new KnowledgeViewHolder();
            viewHolder.photo = (ImageView) view.findViewById(R.id.knowledgePhoto);
            viewHolder.knowledgeTitle = (TextView) view.findViewById(R.id.knowledgeTitile);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (KnowledgeViewHolder) view.getTag();
        }
        if(knowledgeDetail.getPhotoId() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(knowledgeDetail.getPhotoId(), 0,
                    knowledgeDetail.getPhotoId().length);
            viewHolder.photo.setImageBitmap(bitmap);
        } else {
            viewHolder.photo.setImageResource(R.drawable.ice);
        }


        //viewHolder.photo.setImageResource(R.drawable.ice);
        viewHolder.knowledgeTitle.setText(knowledgeDetail.getTitle());

        return view;
    }
}
