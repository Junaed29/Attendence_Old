package com.example.myattendence.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myattendence.R;
import com.example.myattendence.helper.DatabaseConnector;
import com.example.myattendence.helper.DatabaseHelper_3;

import java.util.ArrayList;

public class StoreAllDataActivity extends AppCompatActivity {

    DatabaseConnector databaseConnector;
    String teacher_id;

    DatabaseHelper_3 databaseHelper_3;

    ArrayList <String> batches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_all_data);

        databaseConnector = new DatabaseConnector(this);
        batches = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("SaveData", Context.MODE_PRIVATE);
        teacher_id = sharedPreferences.getString("teacher_id", "Not Found");

        Toast.makeText(StoreAllDataActivity.this,teacher_id,Toast.LENGTH_SHORT).show();

        databaseHelper_3 = new DatabaseHelper_3(this);
        SQLiteDatabase sqLiteDatabase = databaseHelper_3.getReadableDatabase();
        sqLiteDatabase = databaseHelper_3.getWritableDatabase();



        Cursor cursor = databaseHelper_3.getBatchNames(teacher_id);

        while (cursor.moveToNext()) {
            Toast.makeText(StoreAllDataActivity.this, cursor.getString(0),Toast.LENGTH_SHORT).show();
            batches.add(cursor.getString(0));
        }

    }
}
