package com.example.myattendence.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.myattendence.R;
import com.example.myattendence.adapter.CustomAdapterForShowingCourseId;

import com.example.myattendence.helper.DatabaseHelper;

import java.util.ArrayList;

public class AllTablesActivity extends AppCompatActivity {
    ListView listView;
    DatabaseHelper dataBaseHelper;
    String from;
    String id;
    ArrayList<String> arrayList,arrayList2;
    Cursor c;

    private JsonArrayRequest request;
    private RequestQueue requestQueue;

    String courses_url = "http://192.168.2.100/attendence/courses.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tables);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Course & Batches");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        final Bundle b = intent.getExtras();
        if (b != null) {
            from = (String) b.get("tableName");
        }

        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();

        dataBaseHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.tablelistViewId);

        loadFromLocalStroge();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            this.finish();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }


    public void showData(String title, String massage) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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




    public void setListView() {
        final CustomAdapterForShowingCourseId arrayAdapter = new CustomAdapterForShowingCourseId(AllTablesActivity.this, arrayList,arrayList2);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AllTablesActivity.this, "Selected : " + arrayList.get(position), Toast.LENGTH_SHORT).show();
                String s = arrayList2.get(position);
                if (from.equals("froma_take")) {

                    SharedPreferences sharedPreferences = getSharedPreferences("SaveData",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("course_id",arrayList.get(position));
                    editor.putString("batch",s);
                    editor.apply();

                    Intent intent = new Intent(AllTablesActivity.this, AttendenceTable.class);
                    intent.putExtra("tableName", s);
                    startActivity(intent);
                } else if (from.equals("froma_modify")) {
                    SharedPreferences sharedPreferences = getSharedPreferences("SaveData",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("course_id",arrayList.get(position));
                    editor.putString("batch",s);
                    editor.apply();
                    Intent intent = new Intent(AllTablesActivity.this, Modify_TableActivity.class);
                    intent.putExtra("tableName", s);
                    startActivity(intent);
                } else if (from.equals("froma_show")) {
                    SharedPreferences sharedPreferences = getSharedPreferences("SaveData",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("course_id",arrayList.get(position));
                    editor.putString("batch",s);
                    editor.apply();
                    Toast.makeText(AllTablesActivity.this, "From show", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AllTablesActivity.this, Show_attendenceActivity.class);
                    intent.putExtra("tableName", s);
                    startActivity(intent);
                }
            }
        });
    }


    private void loadFromLocalStroge()
    {
        DatabaseHelper dbHelper = new DatabaseHelper(AllTablesActivity.this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor c = dbHelper.displayCourseId();
        if (c.getCount() == 0) {
            showData("Error", "No data found");
        } else {
            while (c.moveToNext()) {
                //Toast.makeText(MainActivity.this, "Display ",Toast.LENGTH_SHORT).show();
                arrayList.add(c.getString(0));
                arrayList2.add(c.getString(1));
            }
        }
        setListView();
    }
}
