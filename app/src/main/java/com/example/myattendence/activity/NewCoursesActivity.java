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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.Map;

public class NewCoursesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    String courseId = "", courseCredits= "", courseType= "";
    ListView listView;
    Button button;

    ProgressDialog progressDialog;

    EditText newCourseIdedtx,courseCeaditEdtx,courseTypeEdtx;

    ArrayList <String> courseNameArraylist;

    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_courses);

        Toolbar toolbar = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Courses");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //Progress Dialog
        progressDialog = new ProgressDialog(NewCoursesActivity.this);
        progressDialog.setMessage("Adding new course...");
        progressDialog.setTitle("Updating");
        //progressDialog.setCancelable(false);



        listView = findViewById(R.id.allCourseListViewId);

        getDataForBatch();


    }

    public void adding_new_courses(View view) {


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.adding_course_id_layout);


        Button modifyOldIdButton = dialog.findViewById(R.id.saveNewCourseIdButtonId);
        newCourseIdedtx = dialog.findViewById(R.id.newCourseIdTextEditorId);
        courseCeaditEdtx = dialog.findViewById(R.id.courseCreditsTextEditorId);
        courseTypeEdtx = dialog.findViewById(R.id.courseTypeTextEditorId);




        courseId = newCourseIdedtx.getText().toString();
        courseId = courseId.replaceAll("\\s+","");
        courseId = courseId.replaceAll("-","");
        courseId = courseId.toUpperCase();

        courseCredits = courseCeaditEdtx.getText().toString();
        courseCredits = courseCredits.replaceAll("\\s+","");

        courseType = courseTypeEdtx.getText().toString();
        courseType = courseType.replaceAll("\\s+","");




        modifyOldIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                courseId = newCourseIdedtx.getText().toString();
                courseId = courseId.replaceAll("\\s+","");
                courseId = courseId.replaceAll("-","");
                courseId = courseId.toUpperCase();

                courseCredits = courseCeaditEdtx.getText().toString();
                courseCredits = courseCredits.replaceAll("\\s+","");

                courseType = courseTypeEdtx.getText().toString();
                courseType = courseType.replaceAll("\\s+","");
                Log.d("onClick","Error : "+courseId+"\n"+courseCredits+"\n"+courseType);
                if (courseId.isEmpty()||courseCredits.isEmpty()||courseType.isEmpty()){
                    showData("Error..","Please fill all the option...");
                }else {
                    final String adding_course_query = "INSERT INTO `course_info`(`course_id`, `credit`, `type`) VALUES ('"+courseId+"','"+courseCredits+"','"+courseType+"');";
                    //showData("Success..",courseId+"\n"+courseCredits+"\n"+courseType);
                    RequestQueue requestQueue ;
                    {
                        String reg_url = "http://192.168.2.100/attendence/adding_new_course.php";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, reg_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            //progressDialog.dismiss();

                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            String message = jsonObject.getString("message");
                                            if (code.equals("Success..")){
                                                DatabaseHelper_2 databaseHelper_2 = new DatabaseHelper_2(NewCoursesActivity.this);
                                                databaseHelper_2.insertIntoAllCourse_TABLE(courseId);
                                            }
                                            showData(code,message);
                                        } catch (JSONException e) {
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
                                params.put("courseId",courseId);
                                params.put("adding_course_query",adding_course_query);
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

    public void getDataForBatch()
    {
        courseNameArraylist = new ArrayList<>();

        DatabaseHelper_2 databaseHelper_2 = new DatabaseHelper_2(this);
        Cursor c = databaseHelper_2.getAllCourseName();
        while (c.moveToNext()){
            String name = c.getString(0);
            courseNameArraylist.add(name);
        }

        arrayAdapter = new ArrayAdapter<String>(NewCoursesActivity.this, R.layout.samplelayoutfortext, R.id.listtextViewID, courseNameArraylist);
        listView.setAdapter(arrayAdapter);

    }

    public void showData(String title, String data) {
        progressDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(data);
        builder.setCancelable(true);
        if (title.equals("Success..")){
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    newCourseIdedtx.setText("");
                    courseCeaditEdtx.setText("");
                    courseTypeEdtx.setText("");
                    getDataForBatch();

                }
            });
        }else {
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        AlertDialog alertDialogdialog = builder.create();
        alertDialogdialog.show();
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            this.finish();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String input = newText.toLowerCase();
        ArrayList<String> newArrayListCourseId = new ArrayList<>();


        for(String name : courseNameArraylist){
            if (name.toLowerCase().contains(input)){
                int i = courseNameArraylist.indexOf(name);
                newArrayListCourseId.add(courseNameArraylist.get(i));
            }
        }

        arrayAdapter = new ArrayAdapter<String>(NewCoursesActivity.this, R.layout.samplelayoutfortext, R.id.listtextViewID, newArrayListCourseId);
        listView.setAdapter(arrayAdapter);
        return true;
    }
}
