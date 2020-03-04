package com.example.myattendence.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.myattendence.R;
import com.example.myattendence.adapter.CustomAdapter;
import com.example.myattendence.helper.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AttendenceTable extends AppCompatActivity {

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date,teacher_id,course_id, section = "ALL";

    private RequestQueue requestQueue;

    RadioButton allRadioButton,evenRadioButton,oddRadioButton;
    RadioGroup radioGroup;

    private ListView listView;
    //private String[] strings;
    private Button savebutton;

    String load_ids_url = "http://192.168.2.100/attendence/load_ids.php";
    String insert_ids_url = "http://192.168.2.100/attendence/insert_ids.php";

    private int a, p,l;
    private boolean bl = false;


    private CustomAdapter customAdapter;
    private StringBuilder stringBuilder, stringBuilder_2;
    StringBuilder finalstringBuilder;

    public ArrayList<String> arrayList2, arrayList;

    private TextView dateTextView;


    String TABLE_NAME;
    //DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_table);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        final Bundle b = intent.getExtras();
        if (b != null) {
            TABLE_NAME = (String) b.get("tableName");
        }

        SharedPreferences sharedPreferences = getSharedPreferences("SaveData", Context.MODE_PRIVATE);
        teacher_id = sharedPreferences.getString("teacher_id", "Not Found");
        course_id = sharedPreferences.getString("course_id", "Not Found");

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/YYYY (E)");
        date = dateFormat.format(calendar.getTime());
        final DatePicker datePicker = new DatePicker(AttendenceTable.this);

        listView = findViewById(R.id.listViewId);
        savebutton = findViewById(R.id.saveButtonId);
        dateTextView = findViewById(R.id.dateTextViewId);
        radioGroup = findViewById(R.id.sectionRadioGroupId);
        allRadioButton = findViewById(R.id.allRadioButtonId);
        evenRadioButton = findViewById(R.id.evenRadioButtonId);
        oddRadioButton = findViewById(R.id.oddRadioButtonId);







        dateTextView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                int currentDay = datePicker.getDayOfMonth();
                int currentMonth = datePicker.getMonth() ;
                int currentYear = datePicker.getYear();

                DatePickerDialog datePickerDialog = new DatePickerDialog(AttendenceTable.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar = Calendar.getInstance();
                                calendar.set(year,month,dayOfMonth);
                                dateFormat = new SimpleDateFormat("dd/MM/YYYY (E)");
                                date = dateFormat.format(calendar.getTime());
                                dateTextView.setText(date);
                            }
                        }, currentYear, currentMonth, currentDay
                );
                datePickerDialog.show();
            }
        });

        dateTextView.setText(date);


        //databaseHelper = new DatabaseHelper(this);
        //SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();



        /*Cursor c = databaseHelper.showCallomName(TABLE_NAME);
        String[] columnNames = c.getColumnNames(); /// For all columns
        for (int i = 0; i < (columnNames.length - 1); i++) {
            arrayList2.add(i, columnNames[(i + 1)]);
        }*/
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checked = findViewById(checkedId);
                section = "";
                section = checked.getText().toString();
                loadDataFromLocalStorage();

            }
        });

        loadDataFromLocalStorage();






    }



    /*
    public void loadData() {

        SharedPreferences sharedPreferences = getSharedPreferences("SaveData", Context.MODE_PRIVATE);
        teacher_id = sharedPreferences.getString("teacher_id", "Not Found");
        course_id = sharedPreferences.getString("course_id", "Not Found");

        Log.d("onResponse", teacher_id);

        {

            {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, load_ids_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("onResponse", response);
                                try {
                                    //progressDialog.dismiss();
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = null;

                                    for (int i = 0; i < response.length(); i++) {
                                        jsonObject = jsonArray.getJSONObject(i);
                                        arrayList2.add(jsonObject.getString("batch"));
                                        //Log.d("onResponse", arrayList2.get(i));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                setListView();
                            }


                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("batch", TABLE_NAME);
                        return params;
                    }
                };
                requestQueue = Volley.newRequestQueue(AttendenceTable.this);
                requestQueue.add(stringRequest);


            }
        }

    }
    */


    private void setListView() {
        //strings = getResources().getStringArray(R.array.string);
        customAdapter = new CustomAdapter(AttendenceTable.this, arrayList2);

        listView.setAdapter(customAdapter);


        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList = new ArrayList<>();
                stringBuilder = new StringBuilder();
                stringBuilder.append("DATE : " + date + "\n");
                a = 0;
                p = 0;
                l = 0;
                for (int i = 0; i < customAdapter.hashMap.size(); i++) {
                    if (customAdapter.hashMap.get(i) == "p") {
                        arrayList.add("PRESENT");
                        stringBuilder.append(arrayList2.get(i) + " = PRESENT\n");
                        p++;
                    } else if (customAdapter.hashMap.get(i) == "a") {
                        arrayList.add("ABSENT");
                        stringBuilder.append(arrayList2.get(i) + " = ABSENT\n");
                        a++;
                    } else if (customAdapter.hashMap.get(i) == "l") {
                        arrayList.add("LATE");
                        stringBuilder.append(arrayList2.get(i) + " = LATE\n");
                        l++;
                    }
                }
                stringBuilder.append("Total present = " + p + "\nTotal Absent = " + a + "\nTotal Late = " + l);
                showData("Result", stringBuilder.toString());
                //databaseHelper.insertData(date, arrayList, arrayList2, TABLE_NAME);

                Toast.makeText(AttendenceTable.this, "Display is called after insert", Toast.LENGTH_SHORT).show();


            }
        });
    }

    public void showData(String title, String data) {
        bl = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(data);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //insertData(date, arrayList, arrayList2, TABLE_NAME);
                insertDataIntoLocalDatabase(date, arrayList, arrayList2, TABLE_NAME,0);
                Toast.makeText(AttendenceTable.this, "Display is called in set positive" + bl, Toast.LENGTH_SHORT).show();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        Toast.makeText(AttendenceTable.this, "Display is called in end in alertdialog" + bl, Toast.LENGTH_SHORT).show();

    }


    /*
    public void insertData(String date, ArrayList<String> arrayList, ArrayList<String> arrayList2, String TABLE_NAME)
    {

        int i ;
        finalstringBuilder = new StringBuilder();
        finalstringBuilder.append("insert into `"+TABLE_NAME+"`(`date`, `");


        for(i= 0; i < arrayList.size()-1; i++){
            finalstringBuilder.append(arrayList2.get(i)+"`, `");
        }
        finalstringBuilder.append(arrayList2.get(i)+"`,`teacher_id`,`course_id`) values ('"+date+"','");


        for(i = 0; i < arrayList.size()-1; i++){
            finalstringBuilder.append(arrayList.get(i)+"','");
        }
        finalstringBuilder.append(arrayList.get(i)+"','"+teacher_id+"','"+course_id+"');");

        {

            {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, insert_ids_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("onResponse", finalstringBuilder.toString());
                                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                            }


                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("data", finalstringBuilder.toString());
                        return params;
                    }
                };
                requestQueue = Volley.newRequestQueue(AttendenceTable.this);
                requestQueue.add(stringRequest);


            }
        }


    }
    */

    public void loadDataFromLocalStorage()
    {
        int id;
        arrayList2 = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(AttendenceTable.this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor c = dbHelper.showCallomName(TABLE_NAME);
        String[] columnNames = c.getColumnNames(); /// For all columns
        if (section.equals("ALL")){
            for (int i = 1; i < (columnNames.length - 1); i++) {
                if (!columnNames[i].equals("teacher_id") && !columnNames[i].equals("course_id")){
                    arrayList2.add(columnNames[i]);
                }


            }
        }else if (section.equals("EVEN")){
            for (int i = 1; i < (columnNames.length - 1); i++) {
                Log.d("section",columnNames[i]);
                String name = columnNames[i];
                id = Character.getNumericValue(name.charAt(name.length()-1));
                Log.d("section",""+id);
                if (id%2==0)
                    if (!columnNames[i].equals("teacher_id") && !columnNames[i].equals("course_id")){
                        arrayList2.add(columnNames[i]);
                    }

                 name = "";
            }
        }else if (section.equals("ODD")){
            for (int i = 1; i < (columnNames.length - 1); i++) {
                Log.d("section",columnNames[i]);
                String name = columnNames[i];
                id = Character.getNumericValue(name.charAt(name.length()-1));
                Log.d("section",""+id);
                if (id%2!=0)
                    if (!columnNames[i].equals("teacher_id") && !columnNames[i].equals("course_id")){
                        arrayList2.add(columnNames[i]);
                    }

                name = "";
            }
        }

        setListView();
    }


    public void insertDataIntoLocalDatabase(String date, ArrayList<String> arrayList, ArrayList<String> arrayList2, String TABLE_NAME, int sync_status)
    {

        int i ;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("insert into `"+TABLE_NAME+"`(`date`, `");


        for(i= 0; i < arrayList.size()-1; i++){
            stringBuilder.append(arrayList2.get(i)+"`, `");
        }
        stringBuilder.append(arrayList2.get(i)+"`,`teacher_id`,`course_id`,`sync_status`) values ('"+date+"','");


        for(i = 0; i < arrayList.size()-1; i++){
            stringBuilder.append(arrayList.get(i)+"','");
        }
        stringBuilder.append(arrayList.get(i)+"','"+teacher_id+"','"+course_id+"',"+sync_status+");");

        Log.d("teacher_id",teacher_id);
        DatabaseHelper dbHelper = new DatabaseHelper(AttendenceTable.this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        sqLiteDatabase = dbHelper.getWritableDatabase();
        try {
            sqLiteDatabase.execSQL(stringBuilder.toString());
            Toast.makeText(AttendenceTable.this,"Data Insert Successful",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(AttendenceTable.this,"Not Successful",Toast.LENGTH_SHORT).show();
        }
    }



}
