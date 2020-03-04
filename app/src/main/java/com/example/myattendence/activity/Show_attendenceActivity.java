package com.example.myattendence.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.myattendence.R;
import com.example.myattendence.adapter.RecyclerViewAdapter;
import com.example.myattendence.helper.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;

public class Show_attendenceActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {


    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    //private List<Anime> lstAnime;
    private RecyclerView recyclerView;

    String course_id,teacher_id,batch;

    RecyclerViewAdapter myadapter;

    ArrayList <String> idarrayList, parcentageArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_attendence);



        SharedPreferences sharedPreferences = getSharedPreferences("SaveData", Context.MODE_PRIVATE);
        teacher_id = sharedPreferences.getString("teacher_id", "Not Found");
        course_id = sharedPreferences.getString("course_id", "Not Found");
        batch = sharedPreferences.getString("batch", "Not Found");

        Toolbar toolbar = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(batch+" ("+course_id+")");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //lstAnime = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerviewid);
        Log.d("onResponse", "Before");

        idarrayList = new ArrayList<String>();
        parcentageArrayList = new ArrayList<String>();

        loadDataFromLocalStorage();
    }


    public void loadDataFromLocalStorage()
    {
        DatabaseHelper dbHelper = new DatabaseHelper(Show_attendenceActivity.this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor c = dbHelper.showCallomName(batch);
        String[] columnNames = c.getColumnNames(); /// For all columns
        for (int i = 0; i < (columnNames.length - 0); i++) {
            idarrayList.add(columnNames[i]);
        }

        Collections.sort(idarrayList);

        idarrayList.remove("sync_status");
        idarrayList.remove("date");
        idarrayList.remove("teacher_id");
        idarrayList.remove("course_id");

        for(int i = 0; i < idarrayList.size(); i++)
        {
            Cursor cursor = dbHelper.getTotalClass(batch,course_id,teacher_id,idarrayList.get(i));
            Cursor cursor1 = dbHelper.getTotalPresent(idarrayList.get(i),batch);

            String total_class = "0";
            String total_present = "0";

            if (cursor.getCount()>0){
                while (cursor.moveToNext()&&cursor1.moveToNext()){
                    total_class = cursor.getString(0);
                    total_present = cursor1.getString(0);
                }
            }
            Double parcentage = (Double.parseDouble(total_present)/Double.parseDouble(total_class)) * 100;
            parcentageArrayList.add(String.valueOf(parcentage));

            Log.d("parcentage",String.valueOf(parcentage));



        }

        setupRecyclerview();
    }



    public void setupRecyclerview() {

        myadapter = new RecyclerViewAdapter(this, idarrayList,parcentageArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myadapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar,menu);

        MenuItem menuItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(this);


        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String input = newText.toLowerCase();
        ArrayList<String> newArrayListForStudentId = new ArrayList<>();
        ArrayList<String> newArrayListForPercentage = new ArrayList<>();


        for(String name : idarrayList){
            if (name.toLowerCase().contains(input)){
                int i = idarrayList.indexOf(name);
                newArrayListForStudentId.add(idarrayList.get(i));
                newArrayListForPercentage.add(parcentageArrayList.get(i));
            }
        }

        myadapter.upDateArrayList(newArrayListForStudentId,newArrayListForPercentage);
        return true;
    }
}
