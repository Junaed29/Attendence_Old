package com.example.myattendence.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    Button loginButton, regisButtton;
    EditText idEditText, passwordEditText;
    //DatabaseHelper databaseHelper;

    RequestQueue requestQueue ;

    AlertDialog.Builder builder;
    AlertDialog dialog;

    //getSupportActionBar().hide();
    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    ProgressDialog progressDialog;

    String username, password;

    String check_url = "http://192.168.2.100/attendence/check.php";

    String login_url = "http://192.168.2.100/attendence/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



        loginButton = findViewById(R.id.loginButtonId);
        regisButtton = findViewById(R.id.registrationId);


        //Progress Dialog
        progressDialog = new ProgressDialog(LogInActivity.this);
        progressDialog.setMessage("Checking Connection...");
        progressDialog.setTitle("Logging In");
        progressDialog.setCanceledOnTouchOutside(false);

        //progressDialog.setCancelable(false);

        idEditText = findViewById(R.id.logInusernameId);
        passwordEditText = findViewById(R.id.logInPasswordId);

        loginButton.setOnClickListener(this);
        regisButtton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        username = idEditText.getText().toString();
        username = username.replaceAll("\\s+","");
        password = passwordEditText.getText().toString();
        password = password.replaceAll("\\s+","");


        //username = "admin@bauet";
        //password = "admin";

        builder = new AlertDialog.Builder(LogInActivity.this);

        if (v.getId() == R.id.loginButtonId){
            progressDialog.show();
            //boolean result = databaseHelper.findPassword(username,password);
            if (username.isEmpty()|password.isEmpty()||username.equals("null")|password.equals("null")){
                builder.setTitle("Something went wrong");
                display("Enter proper username and password");
            }else {
                StringRequest request = new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("codes",response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");

                            Log.d("response",response);
                            if (code.equals("login_failed")){
                                builder.setTitle("Login Error.....");
                                display(jsonObject.getString("message"));
                            }else if(code.equals("login_success")) {
                                String status = jsonObject.getString("status");

                                if (status.equals("teacher")){
                                    SharedPreferences sharedPreferences = getSharedPreferences("SaveData",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("teacher_id",jsonObject.getString("teacher_id"));
                                    //editor.putString("status","");
                                    Log.d("codes",jsonObject.getString("teacher_id"));
                                    editor.apply();
                                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                    //Intent intent = new Intent(LogInActivity.this, StoreAllDataActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if(status.equals("admin")){
                                    SharedPreferences sharedPreferences = getSharedPreferences("SaveData",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    //editor.putString("status",status);
                                    editor.putString("teacher_id","");
                                    //Log.d("codes",jsonObject.getString("teacher_id"));
                                    editor.apply();
                                    Intent intent = new Intent(LogInActivity.this, Admin_Activity.class);
                                    startActivity(intent);
                                    finish();

                                }else {
                                    builder.setTitle("Connection Error.....");
                                    display("Please check internet connection..");
                                }
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        builder.setTitle("Connection Error.....");
                        display("Please check internet connection..");

                        error.printStackTrace();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map <String, String> params = new HashMap<String, String>();
                        params.put("user_name",username);
                        params.put("user_pass",password);
                        return params;
                    }
                };
                requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(request);
                // MySinleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
            }
        }else if (v.getId() == R.id.registrationId){
            final AlertDialog.Builder builders = new AlertDialog.Builder(LogInActivity.this);
            View view = getLayoutInflater().inflate(R.layout.check_layout,null);
            final EditText checkEditText = view.findViewById(R.id.codeId);
            Button button = view.findViewById(R.id.checkBtnId);

            button.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    final String codes = checkEditText.getText().toString();

                    if (codes.isEmpty()){
                        progressDialog.dismiss();
                        builder.setTitle("Something went wrong");
                        display("Enter proper code");
                    }else{
                        StringRequest request = new StringRequest(Request.Method.POST, check_url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("codes",response);
                                try {
                                    progressDialog.dismiss();
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String code = jsonObject.getString("code");
                                    Log.d("username",code);
                                    if (code.equals("match_failed")){
                                        Log.d("codes",code);
                                        builder.setTitle("Code Error.....");
                                        builder.setMessage(jsonObject.getString("message"));
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                checkEditText.setText("");
                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }else if (jsonObject.getString("username").equals("null")||jsonObject.getString("username").isEmpty()){
                                        Intent intent = new Intent(LogInActivity.this,Registration_activity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("name",jsonObject.getString("name"));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        dialog.cancel();
                                    }else{
                                        builder.setTitle("Sorry.....");
                                        builder.setMessage("The code had already used..");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                checkEditText.setText("");
                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                } catch (JSONException e) {
                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                builder.setTitle("Connection Error.....");
                                builder.setMessage("Please check internet connection..");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkEditText.setText("");
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                error.printStackTrace();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map <String, String> params = new HashMap<String, String>();
                                params.put("CODE",codes);

                                return params;
                            }
                        };
                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(request);
                    }
                }
            });
            builders.setView(view);
            dialog = builders.create();
            dialog.show();
        }
    }

    public void display(String message){
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
                idEditText.setText("");
                passwordEditText.setText("");

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
