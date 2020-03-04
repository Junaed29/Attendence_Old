package com.example.myattendence.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.myattendence.R;

import java.util.ArrayList;


public class RecyclerViewAdapterForTeacherInfo extends RecyclerView.Adapter<RecyclerViewAdapterForTeacherInfo.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> nameArrayList = new ArrayList<>();
    private ArrayList<String> IdArrayList = new ArrayList<>();
    


    public RecyclerViewAdapterForTeacherInfo(Context mContext, ArrayList<String> nameArrayList, ArrayList<String> IdArrayList) {
        this.mContext = mContext;
        this.nameArrayList = nameArrayList;
        this.IdArrayList = IdArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.sample_layoutfor_teacherinfo, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.nameTextView.setText(nameArrayList.get(position));
        holder.idTextView.setText(IdArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return nameArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView idTextView;
        TextView nameTextView;


        public MyViewHolder(View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.listtextForTeacherIdID);
            nameTextView= itemView.findViewById(R.id.listtextForTeacherNameID);

        }
    }

    public void upDateArrayList(ArrayList<String> name,ArrayList<String> id){
        nameArrayList = new ArrayList<>();
        IdArrayList  = new ArrayList<>();

        nameArrayList.addAll(name);
        IdArrayList.addAll(id);

        notifyDataSetChanged();
    }


}
