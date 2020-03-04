package com.example.myattendence.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class DatabaseConnector {
    private DatabaseHelper_3 databaseHelper_3 ;

    private Context context;

    private ProgressDialog progressDialog;

    private ArrayList<String> allTeacherIdArrayList, allTeacherNameArrayList,allTeacherUserNameArrayList,allTeacherPasswordArrayList, allCourseIdArrayList, allBatchesArrayList, totalStudentArrayList;
    private ArrayList <String> teacherIdForDistributionArrayList, courseIdForDistributionArrayList, batchForDistributionArrayList;


    private RequestQueue requestQueue;

    public DatabaseConnector (Context context)
    {
        this.context = context;
        databaseHelper_3 = new DatabaseHelper_3(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper_3.getReadableDatabase();
        sqLiteDatabase = databaseHelper_3.getWritableDatabase();

        //Progress Dialog
        progressDialog = new ProgressDialog(context);
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
        allTeacherUserNameArrayList = new ArrayList<>();
        allTeacherPasswordArrayList = new ArrayList<>();

        getAllCourses();
    }


    private void getAllBatches(){
        {
            Log.d("getAllBatches", "Inside getAllBatches");
            String getAllBatchesUrl = "http://192.168.1.107/attendence/getAllbatches.php";
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
                                Toast.makeText(context, "Error getAllBatches "+e, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }

                            //Collections.sort(allBatchesArrayList);
                            databaseHelper_3.insertIntoAllBatch_TABLE(allBatchesArrayList,totalStudentArrayList);
                            getAllTeacherDetails();
                            //setListView();
                        }


                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
            );
            requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);


        }
    }

    private void getAllCourses(){
        {

            String getAllCoursesUrl = "http://192.168.1.107/attendence/getAllCourses.php";
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
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }

                            Collections.sort(allCourseIdArrayList);
                            databaseHelper_3.insertIntoAllCourse_TABLE(allCourseIdArrayList);
                            getAllBatches();
                            //setListView();
                        }


                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Error getAllCourses", Toast.LENGTH_LONG).show();
                        }
                    }
            );
            requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);


        }
    }

    private void getAllTeacherDetails(){
        {

            String getAllTeacherUrl = "http://192.168.1.107/attendence/getAllTeacher.php";
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
                                    allTeacherUserNameArrayList.add(jsonObject.getString("username"));
                                    allTeacherPasswordArrayList.add(jsonObject.getString("password"));
                                    //Log.d("onResponse", arrayList2.get(i));
                                }

                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }

                            databaseHelper_3.insertIntoAllTeacher_TABLE(allTeacherNameArrayList,allTeacherIdArrayList,allTeacherUserNameArrayList,allTeacherPasswordArrayList);

                            getCourseDistributionDetails();
                            //setListView();
                        }


                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Error getAllTeacherDetails", Toast.LENGTH_LONG).show();
                        }
                    }
            );
            requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }
    }

    private void getCourseDistributionDetails(){
        {

            String getCourseDistributionDetailsUrl = "http://192.168.1.107/attendence/getAllCourseDistributionDetails.php";
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
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }

                            databaseHelper_3.insertIntoCourseDistribution_TABLE(teacherIdForDistributionArrayList,courseIdForDistributionArrayList,batchForDistributionArrayList);

                            databaseHelper_3.close();
                            //setListView();
                        }


                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Error DistributionDetails", Toast.LENGTH_LONG).show();
                        }
                    }
            );
            requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);


        }
    }
}
