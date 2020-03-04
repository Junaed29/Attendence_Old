package com.example.myattendence.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myattendence.R;
import com.example.myattendence.helper.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public Button attendenceButton, showButton, modifyTableButton;

    RequestQueue requestQueue ;

    StringBuilder stringBuilder, stringBuilder_2, batch_names, course_ids;
    StringBuilder allUpdateQueries ;

    SharedPreferences sharedPreferences;

    String teacher_id, course_id,batch;

    ArrayList <String> batches;

    ProgressDialog progressDialog;

    StringBuilder batchQuery;
    MaintainDatabase maintainDatabase;
    DatabaseHelper databaseHelper;

    String get_information_url = "http://192.168.2.100/attendence/load_courseId_and_batch.php";

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //getSupportActionBar().hide();
        Toolbar toolbar = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);



        attendenceButton = findViewById(R.id.takeattendenceButtonId);
        showButton = findViewById(R.id.showAttendenceButtonId);
        modifyTableButton = findViewById(R.id.modifyTableButtonId);


        sharedPreferences = getSharedPreferences("SaveData", Context.MODE_PRIVATE);


        teacher_id = sharedPreferences.getString("teacher_id", "Not Found");
        databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        batch_names = new StringBuilder();
        course_ids = new StringBuilder();



        insertIntoServer();

        showButton.setOnClickListener(this);
        attendenceButton.setOnClickListener(this);
        modifyTableButton.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.takeattendenceButtonId){

            Intent intent = new Intent(MainActivity.this, AllTablesActivity.class);
            intent.putExtra("tableName", "froma_take");
            startActivity(intent);

        }else if (v.getId()==R.id.showAttendenceButtonId){
            Intent intent = new Intent(MainActivity.this, AllTablesActivity.class);
            intent.putExtra("tableName", "froma_show");
            startActivity(intent);

        }
        else if (v.getId()==R.id.modifyTableButtonId){
            Intent intent = new Intent(MainActivity.this, AllTablesActivity.class);
            intent.putExtra("tableName", "froma_modify");
            startActivity(intent);

        }/*else if (v.getId()==R.id.createTableButtonId){
            Intent intent = new Intent(MainActivity.this, Create_TableActivity.class);
            intent.putExtra("tableName", username);
            startActivity(intent);
        }*/

    }

    public void saveToLocalStorageToDistributionTable(){

        //
        {
            stringBuilder_2 = new StringBuilder();
            batches = new ArrayList<>();

            stringBuilder = new StringBuilder();

            batchQuery = new StringBuilder();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, get_information_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {

                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = null;
                                databaseHelper.deleteCart(MainActivity.this);

                                for (int i = 0; i < response.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);

                                    batch = jsonObject.getString("batch");
                                    course_id = jsonObject.getString("course_id");
                                    course_ids.append(course_id+",");
                                    stringBuilder_2.append("SELECT * FROM "+batch+" WHERE course_id = '"+course_id+"' AND teacher_id = '"+teacher_id+"'; @");
                                    batches.add(batch);
                                    long k = databaseHelper.insertDataIntoDistributionTable(course_id,batch);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            for (int i = 0; i < batches.size()-1; i++){
                                batch_names.append(batches.get(i)+",");
                            }
                            batch_names.append(batches.get(batches.size()-1));
                            Log.d("onResponse", batch_names.toString());
                            createTable(batch_names.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("teacher_id", teacher_id);
                    return params;
                }
            };
            requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequest);
        }
    }

    public void createTable(final String name)
    {
        {
            String get_information_url= "http://192.168.2.100/attendence/Copy_all_ids.php";
            
            StringRequest stringRequest = new StringRequest(Request.Method.POST, get_information_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String[] arr = response.split("@");
                            for (int j = 0; j < arr.length-1; j++) {
                                databaseHelper.createBatchTable(batches.get(j),arr[j]);
                            }

                            insertIntoCreatedTable();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("batch", name);
                    return params;
                }
            };
            requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequest);
        }
    }

    public void insertIntoCreatedTable()
    {
        {
            batch_names.append(",");

            stringBuilder = new StringBuilder();
            String get_information_url= "http://192.168.2.100/attendence/get_all_querys.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, get_information_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("query",response);
                            try {

                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = null;

                                for (int i = 0; i < response.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    String string = (jsonObject.getString("all"));

                                    databaseHelper.insertDataIntoTable(string);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                            //Log.d("SharedPreferences", "SharedPreferences");

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("teacher_id", teacher_id);
                    params.put("batch", batch_names.toString());
                    params.put("course_id", course_ids.toString());
                    return params;
                }
            };
            requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequest);
        }
    }




    public void insertIntoServer()
    {

        //Progress Dialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Updating Data...");
        progressDialog.setTitle("Syncing");
        progressDialog.show();
        //progressDialog.setCancelable(false);

        final String allQuery = getAllQuery();
        Log.d("allQuery", allQuery);
        if (allQuery.length() > 5){
            String get_information_url= "http://192.168.2.100/attendence/store_local_data.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, get_information_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();

                            setAllUpdateData();


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("allQuery", allQuery);
                    return params;
                }
            };
            requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequest);
        }else {
            setAllUpdateData();

        }
    }



    public String getAllQuery()
    {
        StringBuilder allQueries ;
        allQueries = new StringBuilder();
        ArrayList<String> arrayList = new ArrayList<>();

        Cursor c = databaseHelper.displayCourseId();
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                //Toast.makeText(MainActivity.this, "Display ",Toast.LENGTH_SHORT).show();
                arrayList.add(c.getString(1));
            }

            for (int k = 0; k < arrayList.size(); k++){
                Cursor cursor = databaseHelper.getUnsingedData(arrayList.get(k));

                while (cursor.moveToNext()){
                    allQueries.append("insert into "+arrayList.get(k)+" values ('");
                    for (int i = 0; i < cursor.getColumnCount()-2; i++){
                        allQueries.append(cursor.getString(i)+"', '");
                    }
                    allQueries.append(cursor.getString(cursor.getColumnCount()-2)+"' );   @");
                }
            }
        }

        return allQueries.toString();
    }

    public void setAllUpdateData(){

        allUpdateQueries = new StringBuilder();

        Cursor c = databaseHelper.getUpdateInfo();

        final SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Log.d("allUpdateQueries", String.valueOf(c.getCount()));
        if (c.getCount() > 0){
            while (c.moveToNext()){
                String student_id = c.getString(0);
                String batch = c.getString(1);
                String date = c.getString(2);
                String course_id = c.getString(3);
                String status = c.getString(4);

                allUpdateQueries.append("UPDATE "+batch+" set `"+student_id+"`='"+status+"' WHERE `date` = '"+date+"' and `teacher_id` = '"+teacher_id+"' and `course_id` = '"+course_id+"';   @");
            }
            Log.d("allUpdateQueries",allUpdateQueries.toString());


            String get_information_url= "http://192.168.2.100/attendence/updateData.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, get_information_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("allUpdateQueries",response);
                            Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();

                            sqLiteDatabase.delete("table_update", null, null);

                            saveToLocalStorageToDistributionTable();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("allUpdateQuery", allUpdateQueries.toString());
                    return params;
                }
            };
            requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequest);
        }else {
            saveToLocalStorageToDistributionTable();
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
            startActivity(new Intent(MainActivity.this,LogInActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
