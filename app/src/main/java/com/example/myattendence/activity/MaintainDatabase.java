package com.example.myattendence.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myattendence.helper.DatabaseHelper;
import com.example.myattendence.helper.DatabaseHelper_2;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import java.util.HashMap;
import java.util.Map;

public class MaintainDatabase {
    private Context context;
    DatabaseHelper_2 databaseHelper_2;
    StringBuilder stringBuilder;
    RequestQueue requestQueue ;


    public MaintainDatabase(Context context) {
        this.context = context;

    }



}
