package com.example.myattendence.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import static java.lang.Thread.sleep;

public class FlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.example.myattendence.R.layout.activity_flash);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences sharedPreferences = getSharedPreferences("SaveData", Context.MODE_PRIVATE);
        String teacher_id = sharedPreferences.getString("teacher_id", "Not Found");
        //String status = sharedPreferences.getString("status", "Not Found");


        if ((teacher_id.equals("Not Found")||teacher_id.isEmpty())){
            set_activity(LogInActivity.class);
        }else /*if (status.equals("admin")){
            startActivity(new Intent(FlashActivity.this,Admin_Activity.class));
            finish();
        }else */{
            set_activity(MainActivity.class);
        }
    }

    public void set_activity(final Class activity){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    sleep(1000 );
                    Intent intent = new Intent(FlashActivity.this,activity);
                    startActivity(intent);
                    FlashActivity.this.finish();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
