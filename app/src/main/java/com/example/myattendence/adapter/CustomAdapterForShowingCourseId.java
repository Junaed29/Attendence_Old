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

public class CustomAdapterForShowingCourseId extends BaseAdapter {
    private Context mContext;

    private ArrayList<String> batchArrayList = new ArrayList<>();
    private ArrayList<String> courseIdArraylist = new ArrayList<>();

    TextView batchTextView;
    TextView courseTextView;


    LayoutInflater inflater;

    RadioGroup radioGroup;

    public CustomAdapterForShowingCourseId(Context mContext,  ArrayList<String> courseIdArraylist, ArrayList<String> batchArrayList) {

        this.mContext = mContext;
        this.courseIdArraylist = courseIdArraylist;
        this.batchArrayList = batchArrayList;
        inflater = (LayoutInflater.from(mContext));

    }

    @Override
    public int getCount() {
        return courseIdArraylist.size();
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
        convertView = inflater.inflate(R.layout.sample_layoutfor_showing_courseid, null);
        batchTextView = convertView.findViewById(R.id.listtextForBatchIdID);
        courseTextView= convertView.findViewById(R.id.listtextForCourseID);

        batchTextView.setText(batchArrayList.get(position));
        courseTextView.setText(courseIdArraylist.get(position));

        return convertView;
    }
}
