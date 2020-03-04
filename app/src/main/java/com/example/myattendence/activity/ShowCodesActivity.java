package com.example.myattendence.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowCodesActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener{
    EditText teacherNameEditText, teacherIdEditText;
    Button saveButton, saveCodeButton;

    String newTeacherName, newTeacherId;

    RequestQueue requestQueue ;

    AlertDialog.Builder builder;

    Dialog dialog;

    ArrayList<String> teacherNameArrayList,teacherIdArrayList;

    private RecyclerView recyclerView;

    RecyclerViewAdapterForTeacherInfo myadapter;

    //ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_codes);


        recyclerView = findViewById(R.id.recyclerviewForTeacherid);
        saveCodeButton = findViewById(R.id.addNewCodeButtonId);
        teacherNameArrayList = new ArrayList<>();
        teacherIdArrayList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Teacher Id");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getDataForTeacher();


        saveCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingCode();
            }
        });

    }

    public void storeDataToServer(){
        newTeacherName = teacherNameEditText.getText().toString();
        newTeacherName = newTeacherName.replaceAll("\\s+","");
        newTeacherId = teacherIdEditText.getText().toString();
        newTeacherId = newTeacherId.replaceAll("\\s+","");

        builder = new AlertDialog.Builder(ShowCodesActivity.this);


        if(newTeacherId.isEmpty()|newTeacherName.isEmpty()){
            builder.setTitle("Something went wrong....");
            builder.setMessage("Please fill all the fields...");
            displayAlert("input_error","","");
        }else {
            String reg_url = "http://192.168.2.100/attendence/storeTeacherInfoToServer.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, reg_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("onResponse",response);
                            try {
                                //progressDialog.dismiss();
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String code = jsonObject.getString("code");
                                String message = jsonObject.getString("message");
                                builder.setTitle("Server Response...");
                                builder.setMessage(message);
                                displayAlert(code, newTeacherName, newTeacherId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            builder.setTitle("Connection lost....");
                            builder.setMessage("Please check your internet connection...");
                            displayAlert("input_error","","");
                        }
                    }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map <String,String> params = new HashMap<String, String>();
                    params.put("teacher_name",newTeacherName);
                    params.put("teacher_id",newTeacherId);
                    return params;
                }
            };
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
            //MySinleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }

    }

    public void addingCode()
    {
         dialog = new Dialog(this);
        dialog.setContentView(R.layout.adding_code_for_teacher);

        teacherNameEditText = dialog.findViewById(R.id.teacherNmaeEditTextId);
        teacherIdEditText = dialog.findViewById(R.id.teacherIdEditTextId);
        saveButton = dialog.findViewById(R.id.saveTeacherDetailsButtonId);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeDataToServer();
            }
        });


        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    public void displayAlert(final String code, final String name, final String Id){
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface a_dialog, int which) {
                if (code.equals("input_error")){

                }else if (code.equals("reg_faild")){
                    teacherIdEditText.setText("");
                }else {
                    teacherNameArrayList.clear();
                    teacherIdArrayList.clear();
                    DatabaseHelper_2 databaseHelper_2 = new DatabaseHelper_2(ShowCodesActivity.this);
                    databaseHelper_2.insertSingleValueIntoAllTeacher_TABLE(name, Id);
                    getDataForTeacher();
                    //Log.d("response","finish");
                    a_dialog.cancel();
                    dialog.dismiss();
                    teacherIdEditText.setText("");
                    teacherNameEditText.setText("");
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void getDataForTeacher()
    {
        DatabaseHelper_2 databaseHelper_2 = new DatabaseHelper_2(this);
        Cursor c = databaseHelper_2.getAllTeacherInfo();
        while (c.moveToNext()){
            String name = c.getString(0);
            String Id = c.getString(1);
            teacherNameArrayList.add(name);
            teacherIdArrayList.add(Id);

            Log.d("getDataForTeacher", name+" "+Id);
        }
        setupRecyclerview();
    }


    public void setupRecyclerview() {

        myadapter = new RecyclerViewAdapterForTeacherInfo(this, teacherNameArrayList,teacherIdArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myadapter);

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
        ArrayList<String> newArrayListForTeacher = new ArrayList<>();
        ArrayList<String> newArrayListForTeacherId = new ArrayList<>();

        for(String name : teacherNameArrayList){
            if (name.toLowerCase().contains(input)){
                int i = teacherNameArrayList.indexOf(name);
                newArrayListForTeacher.add(teacherNameArrayList.get(i));
                newArrayListForTeacherId.add(teacherIdArrayList.get(i));
            }
        }

        myadapter.upDateArrayList(newArrayListForTeacher,newArrayListForTeacherId);
        return true;
    }



}
