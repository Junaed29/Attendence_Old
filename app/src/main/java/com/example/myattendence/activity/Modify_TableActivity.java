package com.example.myattendence.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myattendence.R;
import com.example.myattendence.adapter.CustomAdapterForModifyClass;
import com.example.myattendence.helper.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Modify_TableActivity extends AppCompatActivity
{
    EditText newIdEditText;
    Button saveButton, modifyOldButton;



    AlertDialog.Builder builder;


    Button fromButton, toButton;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String teacher_id = "",course_id = "",student_id = "", date = "", batch = "";

    TextView detailsTextView;
    Spinner idSpinner;

    CustomAdapterForModifyClass customAdapterForModifyClass;

    ListView listView;

    ArrayAdapter <String> idArrayAdapter;

    ArrayList <String> ids, dates, status;

    DatabaseHelper databaseHelper;

    String TABLE_NAME, fromDateString = "", toDateString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify__table);

        databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        Intent intent = getIntent();
        final Bundle b = intent.getExtras();
        if(b != null){
            TABLE_NAME = (String) b.get("tableName");
        }

        listView = findViewById(R.id.listViewDateAndStatusId);
        idSpinner = findViewById(R.id.idSpinnerId);

        builder = new AlertDialog.Builder(Modify_TableActivity.this);


        SharedPreferences sharedPreferences = getSharedPreferences("SaveData", Context.MODE_PRIVATE);
        teacher_id = sharedPreferences.getString("teacher_id", "Not Found");
        course_id = sharedPreferences.getString("course_id", "Not Found");
        batch = sharedPreferences.getString("batch", "Not Found");




        ids = new ArrayList<>();




        DatabaseHelper dbHelper = new DatabaseHelper(Modify_TableActivity.this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor c = dbHelper.showCallomName(TABLE_NAME);
        String[] columnNames = c.getColumnNames(); /// For all columns
        for (int i = 1; i < (columnNames.length - 3); i++) {
            ids.add(columnNames[i]);
        }


        idArrayAdapter = new ArrayAdapter<String>(Modify_TableActivity.this, R.layout.samplelayoutfortext, R.id.listtextViewID, ids);



        idSpinner.setAdapter(idArrayAdapter);

        //student_id = ids.get(0);


        //getData();

        idSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                student_id = ids.get(position);
                Log.d("student_id",student_id);


                getData();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        /*
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                student_status = (String) radioButton.getText();
            }
        });

        */

        //saveButton.setOnClickListener(this);
        //modifyOldButton.setOnClickListener(this);
        //fromButton.setOnClickListener(this);
        //toButton.setOnClickListener(this);

    }


    public void getData()
    {

        dates = new ArrayList<>();
        status = new ArrayList<>();


        final DatabaseHelper dbHelper = new DatabaseHelper(Modify_TableActivity.this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.getAllDetailsForStudent(student_id,batch,teacher_id,course_id);

        if (cursor.getCount()>0){
            while (cursor.moveToNext()){
                Log.d("student_id",cursor.getString(0)+" "+cursor.getString(1));
                dates.add(cursor.getString(0)) ;
                status.add(cursor.getString(1));
            }
        }

        customAdapterForModifyClass = new CustomAdapterForModifyClass(Modify_TableActivity.this,dates,status);



        listView.setAdapter(customAdapterForModifyClass);

        customAdapterForModifyClass.notifyDataSetChanged();

        cursor.close();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(Modify_TableActivity.this);
                dialog.setContentView(R.layout.sample_layout_for_student_status);

                RadioButton presentRadioButton = dialog.findViewById(R.id.statuspresentId);
                RadioButton lateRadioButton = dialog.findViewById(R.id.statuslateId);
                RadioButton absentRadioButton = dialog.findViewById(R.id.statusabsentId);

                RadioGroup radioGroup = dialog.findViewById(R.id.statusradioGroupId);

                Log.d("position","position  "+status.get(position));

                date = dates.get(position);

                switch (status.get(position)) {
                    case "PRESENT":
                        Log.d("position", "PRESENT");
                        presentRadioButton.setChecked(true);
                        break;
                    case "ABSENT":
                        Log.d("position", "ABSENT");
                        absentRadioButton.setChecked(true);
                        break;
                    case "LATE":
                        Log.d("position", "LATE");
                        lateRadioButton.setChecked(true);
                        break;
                }


                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        RadioButton checked = dialog.findViewById(checkedId);
                        String StudentStatus = "";
                        StudentStatus = checked.getText().toString();
                        dbHelper.insertIntoTABLE_UPDATE_TABLE(student_id,batch,course_id,date,StudentStatus,teacher_id);
                        dialog.cancel();
                        getData();
                    }
                });


                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });




    }





    public void display(String message){
        builder.setTitle("Error...");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
