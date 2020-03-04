package com.example.myattendence.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myattendence.R;

import java.util.ArrayList;


public class RecyclerViewAdapterForCourseDistribution extends RecyclerView.Adapter<RecyclerViewAdapterForCourseDistribution.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> nameArrayList = new ArrayList<>();
    private ArrayList<String> batchArrayList = new ArrayList<>();
    private ArrayList<String> courseIdArraylist = new ArrayList<>();
    


    public RecyclerViewAdapterForCourseDistribution(Context mContext, ArrayList<String> nameArrayList,ArrayList<String> courseIdArraylist, ArrayList<String> batchArrayList) {
        this.mContext = mContext;
        this.nameArrayList = nameArrayList;
        this.batchArrayList = batchArrayList;
        this.courseIdArraylist = courseIdArraylist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.sample_layoutfor_course_distribution, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.nameTextView.setText(nameArrayList.get(position));
        holder.batchTextView.setText(batchArrayList.get(position));
        holder.courseTextView.setText(courseIdArraylist.get(position));
    }

    @Override
    public int getItemCount() {
        return nameArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView batchTextView;
        TextView nameTextView;
        TextView courseTextView;


        public MyViewHolder(View itemView) {
            super(itemView);
            batchTextView = itemView.findViewById(R.id.listtextForBatchIdID);
            nameTextView= itemView.findViewById(R.id.listtextForTeacherNameID);
            courseTextView= itemView.findViewById(R.id.listtextForCourseID);

        }
    }

    public void upDateArrayList(ArrayList<String> name,ArrayList<String> courseId, ArrayList<String> batches ){
        nameArrayList = new ArrayList<>();
        batchArrayList  = new ArrayList<>();
        courseIdArraylist  = new ArrayList<>();

        nameArrayList.addAll(name);
        batchArrayList.addAll(courseId);
        courseIdArraylist.addAll(batches);

        notifyDataSetChanged();
    }


}
