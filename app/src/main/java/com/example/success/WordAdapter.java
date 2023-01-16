package com.example.success;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WordAdapter extends ArrayAdapter<Word> {
    private int resourceId;
    public WordAdapter(Context context, int textViewResourceId, List<Word> words) {
        super(context,textViewResourceId,words);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Word word = getItem(position);
        View view;
       WordViewHolder viewHolder;
        if(convertView==null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new WordViewHolder();
            viewHolder.english = (TextView) view.findViewById(R.id.wordEnglish);
            viewHolder.chinese = (TextView) view.findViewById(R.id.wordChinese);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (WordViewHolder) view.getTag();
        }
        viewHolder.english.setText(word.getEnglish());
        Log.d("listView","set English"+word.getEnglish());
        viewHolder.chinese.setText(word.getChinese());
        return view;
    }
}
