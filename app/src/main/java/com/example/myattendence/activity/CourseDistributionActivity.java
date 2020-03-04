package com.example.myattendence.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myattendence.R;
import com.example.myattendence.adapter.RecyclerViewAdapterForCourseDistribution;
import com.example.myattendence.helper.DatabaseHelper_2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseDistributionActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    ArrayList <String> teacherNameArrayList, courseIdArrayList, batchArrayList, teacherIdArrayList;

    RecyclerView recyclerView;

    ProgressDialog progressDialog;

    RecyclerViewAdapterForCourseDistribution myadapter;

    String teacherNameString = "", teacherIdString = "", batchString = "", courseString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_distribution);

        Toolbar toolbar = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Courses For Teachers");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        teacherNameArrayList = new ArrayList<>();
        courseIdArrayList = new ArrayList<>();
        batchArrayList = new ArrayList<>();
        teacherIdArrayList = new ArrayList<>();

        //Progress Dialog
        progressDialog = new ProgressDialog(CourseDistributionActivity.this);
        progressDialog.setMessage("Distribution courses among teachers...");
        progressDialog.setTitle("Updating");
        //progressDialog.setCancelable(false);

        recyclerView = findViewById(R.id.recyclerviewForCourseDistributionId);

        getDataForTeacher();
    }


    public void getDataForTeacher()
    {
        teacherNameArrayList.clear();
        courseIdArrayList.clear();
        batchArrayList.clear();

        teacherNameArrayList = new ArrayList<>();
        courseIdArrayList = new ArrayList<>();
        batchArrayList = new ArrayList<>();
        DatabaseHelper_2 databaseHelper_2 = new DatabaseHelper_2(this);
        Cursor c = databaseHelper_2.getDistributedCoursesInfo();
        while (c.moveToNext()){
            String teacherId = c.getString(0);
            String courseId = c.getString(1);
            String batch = c.getString(2);


            Cursor cursor = databaseHelper_2.getTeacherNameFromTeacherInfo(teacherId);
            while (cursor.moveToNext())
                teacherNameArrayList.add(cursor.getString(0));
            courseIdArrayList.add(courseId);
            batchArrayList.add(batch);
            cursor.close();
        }

        c.close();
        setupRecyclerview();

    }


    public void setupRecyclerview() {
        myadapter = new RecyclerViewAdapterForCourseDistribution(this, teacherNameArrayList,courseIdArrayList,batchArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myadapter);
    }

    public void distribute_new_courses(View view) {



        teacherNameArrayList.clear();
        teacherNameArrayList.add("Select");
        teacherIdArrayList.clear();
        teacherIdArrayList.add("Select");
        courseIdArrayList.clear();
        courseIdArrayList.add("Select");
        batchArrayList.clear();
        batchArrayList.add("Select");

        DatabaseHelper_2 databaseHelper_2 = new DatabaseHelper_2(this);

        Cursor cursor = databaseHelper_2.getAllTeacherInfo();

        while (cursor.moveToNext()){
            String batch = cursor.getString(0);
            String courseId = cursor.getString(1);
            teacherNameArrayList.add(batch);
            teacherIdArrayList.add(courseId);
        }
        cursor.close();

        cursor = databaseHelper_2.getAllCourseName();

        while (cursor.moveToNext()){
            String batch = cursor.getString(0);
            courseIdArrayList.add(batch);
        }
        cursor.close();


        cursor = databaseHelper_2.getAllBatchInfo();

        while (cursor.moveToNext()){
            String batch = cursor.getString(0);
            batchArrayList.add(batch);
        }
        cursor.close();



        ArrayAdapter <String> batchArrayAdapter ,teacherNameArrayAdapter,courseIdArrayAdapter;

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.distributing_courses);


        Spinner batchSpinner = dialog.findViewById(R.id.spinnerBatch);
        Spinner teacherNameSpinner = dialog.findViewById(R.id.spinnerTeacherNameId);
        Spinner courseSpinner = dialog.findViewById(R.id.spinnerCourseID);




        final LinearLayout layout = dialog.findViewById(R.id.allTextViewLayoutId);

        final TextView batchTextView = dialog.findViewById(R.id.textViewBatchId);
        final TextView teacherNameTextView = dialog.findViewById(R.id.textViewTeacherId);
        final TextView courseTextView = dialog.findViewById(R.id.textViewCourseId);

        batchArrayAdapter = new ArrayAdapter<String>(CourseDistributionActivity.this,R.layout.samplelayoutfortext_for_spinner,R.id.spinnerID,batchArrayList);
        batchSpinner.setAdapter(batchArrayAdapter);
        teacherNameArrayAdapter = new ArrayAdapter<String>(CourseDistributionActivity.this,R.layout.samplelayoutfortext_for_spinner,R.id.spinnerID,teacherNameArrayList);
        teacherNameSpinner.setAdapter(teacherNameArrayAdapter);
        courseIdArrayAdapter = new ArrayAdapter<String>(CourseDistributionActivity.this,R.layout.samplelayoutfortext_for_spinner,R.id.spinnerID,courseIdArrayList);
        courseSpinner.setAdapter(courseIdArrayAdapter);


        final Button saveButton = dialog.findViewById(R.id.buttonSaveDistributionId);

        //teacherNameString = "Select", batchString = "Select", courseString = "Select";

        batchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                batchString = batchArrayList.get(position);
                if (teacherNameString.equals("Select")||batchString.equals("Select")||courseString.equals("Select")) {
                    batchTextView.setText(batchString);
                }else {
                    batchTextView.setText(batchString);
                    layout.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        teacherNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                teacherNameString = teacherNameArrayList.get(position);
                teacherIdString = teacherIdArrayList.get(position);
                if (teacherNameString.equals("Select")||batchString.equals("Select")||courseString.equals("Select")) {
                    teacherNameTextView.setText(teacherNameString);
                }else {
                    teacherNameTextView.setText(teacherNameString);
                    layout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //layout.setVisibility(View.GONE);
            }
        });

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseString = courseIdArrayList.get(position);
                if (teacherNameString.equals("Select")||batchString.equals("Select")||courseString.equals("Select")) {
                    courseTextView.setText(courseString);
                }else {
                    courseTextView.setText(courseString);
                    layout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //layout.setVisibility(View.GONE);
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (teacherNameString.equals("Select")||batchString.equals("Select")||courseString.equals("Select")) {
                    showData("Error..","Please fill up all the option...");
                }else {
                    //showData("Success..",teacherIdString+"\n"+teacherNameString+"\n"+batchString+"\n"+courseString);

                    final String checkQuery = "SELECT * FROM `course_distribution` WHERE `teacher_id` = '"+teacherIdString+"' and `course_id` = '"+courseString+"' and `batch` = '"+batchString+"' ;";
                    final String FinalQuery = "INSERT INTO `course_distribution`(`teacher_id`, `course_id`, `batch`) VALUES ('"+teacherIdString+"','"+courseString+"','"+batchString+"');";

                    RequestQueue requestQueue ;
                    {
                        String reg_url = "http://192.168.2.100/attendence/distribution_courses.php";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, reg_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            //progressDialog.dismiss();

                                            Log.d("Teacher",response);
                                            Log.d("Teacher",checkQuery);
                                            Log.d("Teacher",FinalQuery);


                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            String message = jsonObject.getString("message");
                                            if (code.equals("Success..")){
                                                DatabaseHelper_2 databaseHelper_2 = new DatabaseHelper_2(CourseDistributionActivity.this);
                                                databaseHelper_2.insertIntoCourseDistribution_TABLE(teacherIdString,courseString,batchString);
                                            }
                                            showData(code,message);
                                        } catch (JSONException e) {
                                            Log.d("Teacher"," "+e);
                                            String code = ("Connection lost....");
                                            String message = ("Please check your internet connection...");
                                            showData(code,message);
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        String code = ("Connection lost....");
                                        String message = ("Please check your internet connection...");
                                        showData(code,message);
                                    }
                                }
                        ){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map <String,String> params = new HashMap<String, String>();
                                params.put("checkQuery",checkQuery);
                                params.put("FinalQuery",FinalQuery);
                                return params;
                            }
                        };
                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);
                    }

                }
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void showData(String title, String data) {

        progressDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(data);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDataForTeacher();
                dialog.cancel();
            }
        });
        AlertDialog alertDialogdialog = builder.create();
        alertDialogdialog.show();
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
        ArrayList<String> newArrayListForTeacherName = new ArrayList<>();
        ArrayList<String> newArrayListForCourseId = new ArrayList<>();
        ArrayList<String> newArrayListForBatch = new ArrayList<>();

        for(String name : teacherNameArrayList){
            if (name.toLowerCase().contains(input)){
                int i = teacherNameArrayList.indexOf(name);
                newArrayListForTeacherName.add(teacherNameArrayList.get(i));
                newArrayListForCourseId.add(courseIdArrayList.get(i));
                newArrayListForBatch.add(batchArrayList.get(i));
            }
        }

        myadapter.upDateArrayList(newArrayListForTeacherName,newArrayListForCourseId,newArrayListForBatch);
        return true;
    }
}
