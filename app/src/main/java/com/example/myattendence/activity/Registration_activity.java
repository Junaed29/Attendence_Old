package com.example.myattendence.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class Registration_activity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText, confirmPasswordEditText, emailEditText,  contactNbrEditText;
    Button saveButton;

    TextView nameTextView;

    //String username, contactNbr, password,confirmPassword, email;
    String username,contactNbr,email,password,confirmPass, names;

    RequestQueue requestQueue ;

    AlertDialog.Builder builder;

    ProgressDialog progressDialog;

    String reg_url = "http://192.168.2.100/attendence/registration.php";

    //DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_activity);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Bundle bundle = getIntent().getExtras();

        names = bundle.getString("name");

        usernameEditText = findViewById(R.id.teacherUserNmaeId);
        passwordEditText = findViewById(R.id.teacherPasswordId);
        confirmPasswordEditText = findViewById(R.id.teacherConfirmPasswordId);
        emailEditText = findViewById(R.id.teacherEmailId);
        contactNbrEditText = findViewById(R.id.teacherContactNbrId);

        nameTextView = findViewById(R.id.teacherNameId);

        builder = new AlertDialog.Builder(Registration_activity.this);

        //Progress Dialog
        progressDialog = new ProgressDialog(Registration_activity.this);
        progressDialog.setMessage("Updating Data...");
        progressDialog.setTitle("Syncing");
        //progressDialog.setCancelable(false);


        //databaseHelper = new DatabaseHelper(this);

        saveButton = findViewById(R.id.saveRegistrationId);


        nameTextView.setText(bundle.getString("name"));



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                username = usernameEditText.getText().toString();
                username = username.replaceAll("\\s+","");
                contactNbr = contactNbrEditText.getText().toString();
                contactNbr = contactNbr.replaceAll("\\s+","");
                email = emailEditText.getText().toString();
                email = email.replaceAll("\\s+","");
                password = passwordEditText.getText().toString();
                password = password.replaceAll("\\s+","");
                confirmPass = confirmPasswordEditText.getText().toString();
                confirmPass = confirmPass.replaceAll("\\s+","");


                //long rowNumber = databaseHelper.insertDataIntoTeachersTable(username,name,department,email,password);
                if(username.isEmpty()|contactNbr.isEmpty()|password.isEmpty()|email.isEmpty()|confirmPass.isEmpty()){
                    builder.setTitle("Something went wrong....");
                    builder.setMessage("Please fill all the fields...");
                    displayAlert("input_error");
                }else if (!(password.equals(confirmPass))){
                    builder.setTitle("Something went wrong....");
                    builder.setMessage("Your password are not matching...");
                    displayAlert("input_error");
                }else{
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
                                        displayAlert(code);
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
                                    displayAlert("input_error");
                                }
                            }
                    ){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map <String,String> params = new HashMap<String, String>();
                            params.put("contact_nbr",contactNbr);
                            params.put("user_pass",password);
                            params.put("user_name",username);
                            params.put("email",email);
                            params.put("teacher_name",names);
                            return params;
                        }
                    };
                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                    //MySinleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                }
            }



        });
    }

    public void displayAlert(final String code){
        progressDialog.dismiss();
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code.equals("input_error")){
                    passwordEditText.setText("");
                    confirmPasswordEditText.setText("");
                }else if (code.equals("reg_faild")){
                    usernameEditText.setText("");
                    emailEditText.setText("");
                    contactNbrEditText.setText("");
                    passwordEditText.setText("");
                    confirmPasswordEditText.setText("");
                }else {
                    //Log.d("response","finish");
                    finish();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
