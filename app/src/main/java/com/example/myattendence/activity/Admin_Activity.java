package com.example.myattendence.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myattendence.R;
import com.example.myattendence.helper.DatabaseHelper_2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class Admin_Activity extends AppCompatActivity implements View.OnClickListener {
    Button show_codeButton,entryNewBatchButton,entryNewCourseButton,distributionButton;

    DatabaseHelper_2 databaseHelper_2 ;

    ProgressDialog progressDialog;

    ArrayList <String> allTeacherIdArrayList, allTeacherNameArrayList, allCourseIdArrayList, allBatchesArrayList, totalStudentArrayList;
    ArrayList <String> teacherIdForDistributionArrayList, courseIdForDistributionArrayList, batchForDistributionArrayList;


    final String getAllTeacherUrl = "http://192.168.2.100/attendence/getAllTeacher.php";
    final String getAllCoursesUrl = "http://192.168.2.100/attendence/getAllCourses.php";
    final String getAllBatchesUrl = "http://192.168.2.100/attendence/getAllbatches.php";
    final String getCourseDistributionDetailsUrl = "http://192.168.2.100/attendence/getAllCourseDistributionDetails.php";

    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adnin_);

        databaseHelper_2 = new DatabaseHelper_2(this);


        Toolbar toolbar = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);

        // Name of activity
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("ADMIN");
        }

        show_codeButton = findViewById(R.id.showCodesButtonId);
        entryNewBatchButton = findViewById(R.id.entryNewBatchButtonId);
        entryNewCourseButton = findViewById(R.id.entryNewCourseButtonId);
        distributionButton = findViewById(R.id.distributeClassButtonId);


        //Progress Dialog
        progressDialog = new ProgressDialog(Admin_Activity.this);
        progressDialog.setMessage("Updating Data...");
        progressDialog.setTitle("Syncing");
        progressDialog.show();
        //progressDialog.setCancelable(false);


        allBatchesArrayList = new ArrayList<>();
        allCourseIdArrayList = new ArrayList<>();
        allTeacherIdArrayList = new ArrayList<>();
        allTeacherNameArrayList = new ArrayList<>();
        totalStudentArrayList = new ArrayList<>();
        teacherIdForDistributionArrayList = new ArrayList<>();
        courseIdForDistributionArrayList = new ArrayList<>();
        batchForDistributionArrayList = new ArrayList<>();

        getAllCourses();


        show_codeButton.setOnClickListener(this);
        entryNewBatchButton.setOnClickListener(this);
        entryNewCourseButton.setOnClickListener(this);
        distributionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.showCodesButtonId){
            startActivity(new Intent(Admin_Activity.this,ShowCodesActivity.class));
        }else if (v.getId()==R.id.entryNewBatchButtonId){
            startActivity(new Intent(Admin_Activity.this,Create_TableActivity.class));
        }else if (v.getId()==R.id.entryNewCourseButtonId){
            startActivity(new Intent(Admin_Activity.this,NewCoursesActivity.class));
        }else if (v.getId()==R.id.distributeClassButtonId){
            startActivity(new Intent(Admin_Activity.this,CourseDistributionActivity.class));
        }
    }


    public void getAllBatches(){
        {
            Log.d("getAllBatches", "Inside getAllBatches");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getAllBatchesUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("getAllBatches", response);
                            try {

                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = null;

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    allBatchesArrayList.add(jsonObject.getString("batch"));
                                    totalStudentArrayList.add(jsonObject.getString("total"));
                                    Log.d("onResponse", allBatchesArrayList.get(i));
                                }

                            } catch (JSONException e) {
                                Log.d("getAllBatches ", response+e);
                                Toast.makeText(getApplicationContext(), "Error getAllBatches "+e, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }

                            //Collections.sort(allBatchesArrayList);
                            databaseHelper_2.insertIntoAllBatch_TABLE(allBatchesArrayList,totalStudentArrayList);
                            getAllTeacherDetails();
                            //setListView();
                        }


                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    }
            );
            requestQueue = Volley.newRequestQueue(Admin_Activity.this);
            requestQueue.add(stringRequest);


        }
    }

    public void getAllCourses(){
        {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, getAllCoursesUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("getAllCourses", response);
                            try {
                                //progressDialog.dismiss();
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = null;

                                for (int i = 0; i < response.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    allCourseIdArrayList.add(jsonObject.getString("course_id"));
                                    Log.d("onResponse", allCourseIdArrayList.get(i));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Collections.sort(allCourseIdArrayList);
                            databaseHelper_2.insertIntoAllCourse_TABLE(allCourseIdArrayList);
                            getAllBatches();
                            //setListView();
                        }


                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error getAllCourses", Toast.LENGTH_LONG).show();
                        }
                    }
            );
            requestQueue = Volley.newRequestQueue(Admin_Activity.this);
            requestQueue.add(stringRequest);


        }
    }

    public void getAllTeacherDetails(){
        {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, getAllTeacherUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("getAllTeacherDetails", response);
                            try {
                                //progressDialog.dismiss();
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = null;

                                for (int i = 0; i < response.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    allTeacherIdArrayList.add(jsonObject.getString("teacher_id"));
                                    allTeacherNameArrayList.add(jsonObject.getString("teacher_name"));
                                    //Log.d("onResponse", arrayList2.get(i));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            databaseHelper_2.insertIntoAllTeacher_TABLE(allTeacherNameArrayList,allTeacherIdArrayList);

                            getCourseDistributionDetails();
                            //setListView();
                        }


                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error getAllTeacherDetails", Toast.LENGTH_LONG).show();
                        }
                    }
            );
            requestQueue = Volley.newRequestQueue(Admin_Activity.this);
            requestQueue.add(stringRequest);


        }
    }

    public void getCourseDistributionDetails(){
        {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, getCourseDistributionDetailsUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("DistributionDetails", response);
                            try {
                                progressDialog.dismiss();
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = null;

                                for (int i = 0; i < response.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    teacherIdForDistributionArrayList.add(jsonObject.getString("teacher_id"));
                                    courseIdForDistributionArrayList.add(jsonObject.getString("course_id"));
                                    batchForDistributionArrayList.add(jsonObject.getString("batch"));
                                    //Log.d("onResponse", arrayList2.get(i));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            databaseHelper_2.insertIntoCourseDistribution_TABLE(teacherIdForDistributionArrayList,courseIdForDistributionArrayList,batchForDistributionArrayList);

                            databaseHelper_2.close();
                            //setListView();
                        }


                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error DistributionDetails", Toast.LENGTH_LONG).show();
                        }
                    }
            );
            requestQueue = Volley.newRequestQueue(Admin_Activity.this);
            requestQueue.add(stringRequest);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout){
            SharedPreferences sharedPreferences = getSharedPreferences("SaveData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("teacher_id","");
            //editor.putString("status","");
            editor.apply();
            startActivity(new Intent(Admin_Activity.this,LogInActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
