package com.example.myattendence.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myattendence.R;

import java.util.ArrayList;

public class CustomAdapterForModifyClass extends BaseAdapter {
    ArrayList<String> dates, status;
    Context context;

    LayoutInflater mInflater=null;

    public CustomAdapterForModifyClass( Context context,ArrayList<String> dates, ArrayList<String> status) {
        this.dates = dates;
        this.status = status;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dates.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView==null){
            mInflater = (LayoutInflater)    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.sample_layoutfor_teacherinfo, null);
        }

        TextView dateTextView = convertView.findViewById(R.id.listtextForTeacherNameID);
        TextView statusTextView = convertView.findViewById(R.id.listtextForTeacherIdID);

        dateTextView.setText(dates.get(position));
        statusTextView.setText(status.get(position));

        return convertView;
    }
}
