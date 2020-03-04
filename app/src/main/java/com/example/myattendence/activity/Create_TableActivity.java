package com.example.myattendence.activity;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.EditText;
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
import com.example.myattendence.adapter.RecyclerViewAdapterForTeacherInfo;
import com.example.myattendence.helper.DatabaseHelper_2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.* ;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Create_TableActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    String newTeacherName, newTeacherId;
    RequestQueue requestQueue ;
    ArrayList<String> batchNameArrayList,totalStudentArrayList;
    private RecyclerView recyclerView;

    EditText totalStudentEdtx,studentIdCodeEdtx,newIdEditText;

    RecyclerViewAdapterForTeacherInfo myadapter;
    String department = "", batch = "", totalStudent = "", studentIdCode = "", tableName = "", newId = "", modifyTableName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__table);

        Toolbar toolbar = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Student Batches");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerViewForBatch);

        getDataForBatch();
    }



    public void getDataForBatch()
    {
        batchNameArrayList = new ArrayList<>();
        totalStudentArrayList = new ArrayList<>();

        DatabaseHelper_2 databaseHelper_2 = new DatabaseHelper_2(this);
        Cursor c = databaseHelper_2.getAllBatchInfo();
        while (c.moveToNext()){
            String name = c.getString(0);
            String total = c.getString(1);
            batchNameArrayList.add(name);
            totalStudentArrayList.add("Total : "+total);
            Log.d("getDataForTeacher", name+" "+total);
        }
        setupRecyclerView();
    }


    public void setupRecyclerView() {
        myadapter = new RecyclerViewAdapterForTeacherInfo(this, batchNameArrayList,totalStudentArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myadapter);
    }

    public void Adding_New_Batch(View view) {
        ArrayAdapter<String> allBatchArrayAdapter;
        ArrayAdapter<String> allDeptArrayAdapter;

        final String [] allDept = getResources().getStringArray(R.array.allDepartment);
        final String [] allBatch = getResources().getStringArray(R.array.allBatches);
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.adding_table_layout);

        final LinearLayout linearLayout = dialog.findViewById(R.id.tableNameLayout);
        Spinner deptSpinner = dialog.findViewById(R.id.deptSpinnerId);
        Spinner batchSpinner = dialog.findViewById(R.id.batchSpinnerId);
        final TextView showTableName = dialog.findViewById(R.id.tableNameId);
        Button saveButton = dialog.findViewById(R.id.SaveTableButtonId);
        totalStudentEdtx = dialog.findViewById(R.id.totalStudentEditTextId);
        studentIdCodeEdtx = dialog.findViewById(R.id.idCodeTextId);





        allBatchArrayAdapter = new ArrayAdapter<String>(Create_TableActivity.this,R.layout.samplelayoutfortext_for_spinner,R.id.spinnerID,allBatch);
        allDeptArrayAdapter = new ArrayAdapter<String>(Create_TableActivity.this,R.layout.samplelayoutfortext_for_spinner,R.id.spinnerID,allDept);

        deptSpinner.setAdapter(allDeptArrayAdapter);
        batchSpinner.setAdapter(allBatchArrayAdapter);

        deptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                department = allDept[position];
                if (department.equals("Select")||batch.equals("Select")){
                    tableName = department+"_"+batch+"_Batch";
                    showTableName.setText(tableName);
                    linearLayout.setVisibility(View.GONE);
                }else {
                    tableName = department+"_"+batch+"_Batch";
                    showTableName.setText(tableName);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        batchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                batch = allBatch[position];
                if (department.equals("Select")||batch.equals("Select")){
                    tableName = department+"_"+batch+"_Batch";
                    showTableName.setText(tableName);
                    linearLayout.setVisibility(View.GONE);
                }else {
                    tableName = department+"_"+batch+"_Batch";
                    showTableName.setText(tableName);
                    linearLayout.setVisibility(View.VISIBLE);


                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalStudent = totalStudentEdtx.getText().toString();
                totalStudent = totalStudent.replaceAll("\\s+","");
                studentIdCode = studentIdCodeEdtx.getText().toString();
                studentIdCode = studentIdCode.replaceAll("\\s+","");
                if (department.equals("Select")||batch.equals("Select")||totalStudent.isEmpty()||studentIdCode.isEmpty()){
                    showData("Error..","Please fill up all the option...");
                }else {

                    final StringBuilder stringBuilder = new StringBuilder();
                    RequestQueue requestQueue ;

                    {
                        int j = 0, value = 100;
                        stringBuilder.append("create table `"+tableName+"` (`date` varchar (12) NOT NULL,");
                        for(j = 01; j <Integer.parseInt(totalStudent) ; j++){
                            if (j > 99)
                                value = 1000;
                            int column = (Integer.parseInt(studentIdCode)*value+j);
                            stringBuilder.append("`"+column+"`"+" varchar (12) NOT NULL,");
                        }
                        if (j > 99)
                            value = 1000;
                        int column = (Integer.parseInt(studentIdCode)*value+j);
                        stringBuilder.append("`"+column+"` varchar(12) NOT NULL,`teacher_id` varchar(12) NOT NULL,`course_id` varchar(12) NOT NULL);");

                        String reg_url = "http://192.168.2.100/attendence/create_batch_table.php";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, reg_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("onResponse",response);
                                        Log.d("onResponse",stringBuilder.toString());
                                        try {
                                            //progressDialog.dismiss();

                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            String message = jsonObject.getString("message");
                                            if (code.equals("Success..")){
                                                DatabaseHelper_2 databaseHelper_2 = new DatabaseHelper_2(Create_TableActivity.this);
                                                databaseHelper_2.insertIntoAllBatch_TABLE(tableName, totalStudent);
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
                                params.put("batch_name",tableName);
                                params.put("create_table_query",stringBuilder.toString());
                                return params;
                            }
                        };
                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);
                    }
                    totalStudentEdtx.setText("");
                    studentIdCodeEdtx.setText("");
                }
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    public void Modify_Old_Batch(View view) {
        ArrayAdapter<String> allBatchArrayAdapter;


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.adding_id_layout);

        Spinner batchSpinner = dialog.findViewById(R.id.modifyTableBatchSpinnerId);
        Button saveNewIdButton = dialog.findViewById(R.id.saveNewIDButtonId);
        Button modifyOldIdButton = dialog.findViewById(R.id.removeOldIDButtonId);
        newIdEditText = dialog.findViewById(R.id.newidTextEditorId);

        //batchNameArrayList.add(0,"Select");

        allBatchArrayAdapter = new ArrayAdapter<String>(Create_TableActivity.this,R.layout.samplelayoutfortext_for_spinner,R.id.spinnerID,batchNameArrayList);

        batchSpinner.setAdapter(allBatchArrayAdapter);


        batchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modifyTableName = batchNameArrayList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        saveNewIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newId = newIdEditText.getText().toString();
                newId = newId.replaceAll("\\s+","");
                if (modifyTableName.equals("Select")||newId.isEmpty()){
                    showData("Error..","Please fill up all the option...");
                }else {
                    {
                        String add_new_id_url = "http://192.168.2.100/attendence/insert_new_id.php";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, add_new_id_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            //progressDialog.dismiss();
                                            Log.d("stringRequest",response);

                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            String message = jsonObject.getString("message");

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
                                params.put("student_id",newId);
                                params.put("batch",modifyTableName);
                                return params;
                            }
                        };
                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);
                    }
                    //showData("Success..",modifyTableName+"\n"+newId);
                    newIdEditText.setText("");
                }
            }
        });


        modifyOldIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newId = newIdEditText.getText().toString();
                newId = newId.replaceAll("\\s+","");
                if (modifyTableName.equals("Select")||newId.isEmpty()){
                    showData("Error..","Please fill up all the option...");
                }else {
                    {
                        String remove_id_url = "http://192.168.2.100/attendence/remove_id.php";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, remove_id_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            //progressDialog.dismiss();
                                            Log.d("stringRequest",response);

                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            String message = jsonObject.getString("message");

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
                                params.put("student_id",newId);
                                params.put("batch",modifyTableName);
                                return params;
                            }
                        };
                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);
                    }
                    //showData("Success..",modifyTableName+"\n"+newId);
                    newIdEditText.setText("");
                }
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    public void showData(String title, String data) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(data);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDataForBatch();
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
        ArrayList<String> newArrayListBatches = new ArrayList<>();
        ArrayList<String> newArrayListTotal = new ArrayList<>();

        for(String name : batchNameArrayList){
            if (name.toLowerCase().contains(input)){
                int i = batchNameArrayList.indexOf(name);
                newArrayListBatches.add(batchNameArrayList.get(i));
                newArrayListTotal.add(totalStudentArrayList.get(i));
            }
        }

        myadapter.upDateArrayList(newArrayListBatches,newArrayListTotal);
        return true;
    }
}
