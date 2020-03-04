package com.example.myattendence.helper;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Attendence.db";
    private static final int VERSION_NUMBER = 1;
    private Context context;



    private static final String COURSE_DISTRIBUTION_TABLE = "course_distribution";
    private static final String TABLE_UPDATE_TABLE = "table_update";
    private static final String TEACHERS_ID = "teacher_Id";
    private static final String COURSE_ID = "course_id";
    private static final String BATCH = "batch";
    private static final String SYNC_STATUS = "sync_status";
    private static final String MODIFY_TABLE = "modify";
    private static final String NEW_BATCH = "new_natch";
    private static final String OLD_BATCH = "old_batch";
    private static final String NEW_ID = "new_id";
    private static final String DATE = "date";
    private static final String STUDENT_ID = "student_id";
    private static final String STUDENT_STATUS = "student_status";


    private static final String CREATE_MODIFY_TABLE = "CREATE TABLE "+MODIFY_TABLE+" ("+NEW_BATCH+" VARCHAR(50),"+OLD_BATCH+" VARCHAR(50), "+NEW_ID+" VARCHAR(50));";
    private static final String CREATE_TABLE_UPDATE_TABLE = "CREATE TABLE "+TABLE_UPDATE_TABLE+" ("+STUDENT_ID+" VARCHAR(50),"+BATCH+" VARCHAR(50), "+DATE+" VARCHAR(50),"+COURSE_ID+" VARCHAR(50), "+STUDENT_STATUS+" VARCHAR(50));";
    private  static final String DROP_TABLE_MODIFY_TABLE = "DROP TABLE IF EXISTS "+MODIFY_TABLE;
    private  static final String DROP_TABLE_UPDATE_TABLE = "DROP TABLE IF EXISTS "+TABLE_UPDATE_TABLE;
    private static final String CREATE_COURSE_DISTRIBUTION_TABLE = "CREATE TABLE "+COURSE_DISTRIBUTION_TABLE+" ("+COURSE_ID+" VARCHAR(20), "+BATCH+" VARCHAR(50));";
    private  static final String DROP_TABLE_COURSE_DISTRIBUTION_TABLE = "DROP TABLE IF EXISTS "+COURSE_DISTRIBUTION_TABLE;




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_COURSE_DISTRIBUTION_TABLE);
            db.execSQL(CREATE_MODIFY_TABLE);
            db.execSQL(CREATE_TABLE_UPDATE_TABLE);
            Toast.makeText(context,"Successful ",Toast.LENGTH_SHORT).show();
        }catch (SQLException e){
            e.printStackTrace();
            Toast.makeText(context,"Unsuccessful",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE_COURSE_DISTRIBUTION_TABLE);
            db.execSQL(DROP_TABLE_MODIFY_TABLE);
            db.execSQL(DROP_TABLE_UPDATE_TABLE);
            Toast.makeText(context,"OnUpgrade is called ",Toast.LENGTH_SHORT).show();
            onCreate(db);
        }catch (SQLException e){
            e.printStackTrace();
            Toast.makeText(context,"ExcepTion on OnUpgrade  : "+e,Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteCart(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            db.execSQL("DELETE FROM " + DatabaseHelper.COURSE_DISTRIBUTION_TABLE);

        } catch (SQLException e) {
            Toast.makeText(context,"ExcepTion "+e,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public long insertDataIntoDistributionTable(String course_id, String batch){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COURSE_ID,course_id);
        contentValues.put(BATCH,batch);

        return sqLiteDatabase.insert(COURSE_DISTRIBUTION_TABLE,null,contentValues);
    }

    public void insertDataIntoTable(String data){
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL(data);
            //Toast.makeText(context,"Successful",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            //Toast.makeText(context,"UnSuccessful",Toast.LENGTH_LONG).show();
        }
    }


    public void createBatchTable(String drop,String string){

        String drop_table = "DROP TABLE IF EXISTS "+drop;
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL(drop_table);
            db.execSQL(string);
            Toast.makeText(context,"Table Created",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context,"Already Created",Toast.LENGTH_LONG).show();
        }
    }

    public Cursor displayCourseId()
    {
        String SELECT_ALL = "select `"+COURSE_ID+"`,`"+BATCH+"` from '"+COURSE_DISTRIBUTION_TABLE+"';";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_ALL,null);
        return cursor;
    }

    public Cursor getUnsingedData(String TABLE_NAME)
    {
        String SELECT_ALL = "select * FROM `"+TABLE_NAME+"` WHERE `"+SYNC_STATUS+"` = '0';";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_ALL,null);
        return cursor;
    }

    public Cursor showCallomName(String TABLE_NAME)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(" SELECT * from '"+TABLE_NAME+"' WHERE 0 ", null);
        return cursor;
    }


    public Cursor getTotalClass( String batch, String course_id, String teacher_id,String Id)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String count_date = "select COUNT(`date`) from "+batch+" where course_id = '"+course_id+"' and teacher_id = '"+teacher_id+"' and (`"+Id+"` = 'PRESENT' or `"+Id+"` = 'ABSENT' or `"+Id+"` = 'LATE');";
        return sqLiteDatabase.rawQuery(count_date,null);
    }

    public Cursor getTotalPresent(String Id,String batch){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String count_present = "select COUNT(`"+Id+"`) FROM "+batch+" WHERE `"+Id+"` = 'PRESENT';";
        return sqLiteDatabase.rawQuery(count_present,null);
    }


    public Cursor getAllDetailsForStudent(String Id,String batch,String teacher_id, String course_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String count_present = "SELECT `date`, `"+Id+"` FROM `"+batch+"` where teacher_id = '"+teacher_id+"' and course_id = '"+course_id+"' and (`"+Id+"` = 'PRESENT' or `"+Id+"` = 'ABSENT' or `"+Id+"` = 'LATE');";
        return sqLiteDatabase.rawQuery(count_present,null);
    }



    public  void deleteColumn(String id, String TABLE_NAME){
        SQLiteDatabase db = getWritableDatabase();
        String deleteColumn = "ALTER TABLE `"+TABLE_NAME+"` DROP COLUMN '"+id+"';";
        try {
            db.execSQL(deleteColumn);
            Toast.makeText(context,"Column Delete Successful",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context,"Not Found",Toast.LENGTH_LONG).show();
        }
    }


    public  void addColumn(String id, String TABLE_NAME){
        SQLiteDatabase db = getWritableDatabase();
        String deleteColumn = "ALTER TABLE '"+TABLE_NAME+"' ADD '"+id+"' VARCHAR(10);";
        try {
            db.execSQL(deleteColumn);
            Toast.makeText(context,"Column Added Successful",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context,"Already Added",Toast.LENGTH_LONG).show();
        }
    }


    public void insertIntoTABLE_UPDATE_TABLE(String student_id, String batch, String course_id, String date, String student_status, String teacher_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COURSE_ID,course_id);
        contentValues.put(BATCH,batch);
        contentValues.put(DATE,date);
        contentValues.put(STUDENT_ID,student_id);
        contentValues.put(STUDENT_STATUS,student_status);
        sqLiteDatabase.insert(TABLE_UPDATE_TABLE,null,contentValues);
        String updateColumn = "UPDATE "+batch+" set `"+student_id+"`='"+student_status+"' WHERE `date` = '"+date+"' and `teacher_id` = '"+teacher_id+"' and `course_id` = '"+course_id+"';";
        try {
            sqLiteDatabase.execSQL(updateColumn);
            Toast.makeText(context,"Column Update Successful",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context,"Not Successful",Toast.LENGTH_LONG).show();
        }
        sqLiteDatabase.close();
    }



    public Cursor getUpdateInfo(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String count_present = "SELECT * from "+TABLE_UPDATE_TABLE+";";
        return sqLiteDatabase.rawQuery(count_present,null);
    }


}

