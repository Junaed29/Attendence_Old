package com.example.myattendence.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.myattendence.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList <String> arrayList ;

    public static HashMap hashMap ;
    LayoutInflater inflater;

    RadioGroup radioGroup;

    public CustomAdapter(Context context, ArrayList <String> arrayList) {

        this.context = context;
        this.arrayList = arrayList;
        inflater = (LayoutInflater.from(context));
        hashMap = new HashMap();
        for(int i = 0; i < arrayList.size(); i++){
            hashMap.put(i, "a");
        }
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.sampelayout, null);
        TextView textView  = convertView.findViewById(R.id.textViewId);
        RadioButton presentradioButton = convertView.findViewById(R.id.presentId);
        RadioButton absentradioButton = convertView.findViewById(R.id.absentId);
        RadioButton lateradioButton = convertView.findViewById(R.id.lateId);
// perform setOnCheckedChangeListener event on yes button
        presentradioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    hashMap.put(position, "p");
                }
            }
        });
// perform setOnCheckedChangeListener event on no button
        absentradioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    hashMap.put(position, "a");
                }
            }
        });

        lateradioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    hashMap.put(position, "l");
                }
            }
        });


        textView.setText(arrayList.get(position));
        return convertView;
    }
}
