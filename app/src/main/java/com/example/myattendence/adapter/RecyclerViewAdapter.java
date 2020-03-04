package com.example.myattendence.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.RequestQueue;
import com.example.myattendence.R;
import com.example.myattendence.activity.Each_student_attendence;
import com.example.myattendence.activity.MainActivity;
import com.example.myattendence.helper.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    String[] strings = {"Date", "Attempt"};
    String[][] data;

    private Context mContext;
    private List<String> idArraylist;
    private List<String> parcentageArraylist;
    private RequestQueue requestQueue;
    int total = 0, present = 0;
    private String teacher_id, course_id, batch, student_id;

    String load_student_report_url = "http://192.168.2.100/attendence/student_info.php";


    public RecyclerViewAdapter(Context mContext, List<String> idArraylist, List<String> parcentageArraylist) {
        this.mContext = mContext;
        this.idArraylist = idArraylist;
        this.parcentageArraylist = parcentageArraylist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.show_parcentage, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StringBuilder stringBuilder = new StringBuilder();

                student_id = idArraylist.get(viewHolder.getAdapterPosition());
                stringBuilder.append("Id : " + student_id + "\n\n");

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("SaveData", Context.MODE_PRIVATE);
                teacher_id = sharedPreferences.getString("teacher_id", "Not Found");
                course_id = sharedPreferences.getString("course_id", "Not Found");
                batch = sharedPreferences.getString("batch", "Not Found");

                {
                    DatabaseHelper dbHelper = new DatabaseHelper(mContext);
                    SQLiteDatabase database = dbHelper.getReadableDatabase();

                    Cursor cursor = dbHelper.getAllDetailsForStudent(student_id,batch,teacher_id,course_id);
                    total = 0;
                    present = 0;

                    ArrayList<Each_student_attendence> each_student_attendenceArrayList = new ArrayList<>();
                    if (cursor.getCount()>0){
                        while (cursor.moveToNext()){
                            Each_student_attendence attendence = new Each_student_attendence(cursor.getString(0), cursor.getString(1));
                            each_student_attendenceArrayList.add(attendence);
                            //stringBuilder.append(cursor.getString(0) + " -> " + cursor.getString(1) + "\n");
                            total++;
                            if (cursor.getString(1).equals("PRESENT"))
                                present++;
                        }
                    }


                    data = new String[each_student_attendenceArrayList.size()][2];

                    for(int i = 0; i < each_student_attendenceArrayList.size(); i++){
                        Each_student_attendence each_student_attendence = each_student_attendenceArrayList.get(i);
                        data[i][0] = each_student_attendence.getId();
                        data[i][1] = each_student_attendence.getAttempt();
                    }




                    showAttendance(student_id,Integer.toString(total),Integer.toString(present));


                    //stringBuilder.append("\n" + "Total Class : " + total + "\n");
                    //stringBuilder.append("\n" + "Total Present : " + present + "\n");
                    //showData("Report", stringBuilder.toString());

                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.idTextView.setText(idArraylist.get(position));
        holder.parcentageTextView.setText(parcentageArraylist.get(position));
    }

    @Override
    public int getItemCount() {
        return idArraylist.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView idTextView;
        TextView parcentageTextView;
        LinearLayout view_container;

        public MyViewHolder(View itemView) {
            super(itemView);

            view_container = itemView.findViewById(R.id.container);
            idTextView = itemView.findViewById(R.id.usernameId);
            parcentageTextView = itemView.findViewById(R.id.parcentageId);

        }
    }

    public void showData(String title, String massage) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(massage);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                // Toast.makeText(AttendenceTable.this, "Display is called in set positive"+bl,Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        //Toast.makeText(AttendenceTable.this, "Display is called in end in alertdialog"+bl,Toast.LENGTH_SHORT).show();
    }


    public void showAttendance(String id, String totalClass,String totalPresent){


        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.custom_dialog_for_table);


        final TableView<String[]> tableView = dialog.findViewById(R.id.tableId);
        final Button exitButton = dialog.findViewById(R.id.exitButton);
        TextView idTextView = dialog.findViewById(R.id.idTextViewInTable);
        TextView totalClassTextView = dialog.findViewById(R.id.totalClassTextViewInTable);
        TextView totalPresentTextView = dialog.findViewById(R.id.totalPresentTextViewInTable);

        idTextView.setText(id);
        totalClassTextView.setText("Total Class : " +totalClass);
        totalPresentTextView.setText("Total Present : "+totalPresent);

        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(mContext, strings));
        tableView.setDataAdapter(new SimpleTableDataAdapter(mContext, data));

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        // builder.setMessage("Junaed");
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }


    public void upDateArrayList(ArrayList<String> studentId,ArrayList<String> percentage ){
        idArraylist  = new ArrayList<>();
        parcentageArraylist  = new ArrayList<>();

        idArraylist.addAll(studentId);
        parcentageArraylist.addAll(percentage);

        notifyDataSetChanged();
    }

}
