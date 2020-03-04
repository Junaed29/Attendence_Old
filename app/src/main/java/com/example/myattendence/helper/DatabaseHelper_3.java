package com.example.myattendence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseHelper_3 extends SQLiteOpenHelper {

    Context context;
    private static final int VERSION_NUMBER = 1;
    private static final String DATABASE_NAME = "AllAttendance.db";



    //Table Names
    private static final String AllBatch_TABLE = "AllBatchInfo";
    private static final String AllBatchNAME_COLUMN = "allbatchname";
    private static final String AllBatchTotalStudent_COLUMN = "totalStudent";
    private static final String AllCourse_TABLE = "AllCourseInfo";
    private static final String AllCourseNAME_COLUMN = "allcoursename";
    private static final String AllTeacher_TABLE = "AllTeacherInfo";
    private static final String AllTeacherName_COLUMN = "allteachername";
    private static final String AllTeacherId_COLUMN = "allteacherid";
    private static final String AllTeacherUserName_COLUMN = "username";
    private static final String AllTeacherPassword_COLUMN = "password";
    private static final String CourseDistribution_TABLE = "CourseDistributionInfo";
    private static final String CourseName_Column = "courseName";
    private static final String TeacherId_Column = "teacherId";
    private static final String Batch_Column = "batch";

    private static final String SELECTED_BATCH_TABLE = "CourseUsedIn";

    private static final String CREATE_SELECTED_COURSE_TABLE = "CREATE TABLE "+SELECTED_BATCH_TABLE+" ("+Batch_Column+" VARCHAR(50));";
    private  static final String DROP_SELECTED_COURSE_TABLE = "DROP TABLE IF EXISTS "+SELECTED_BATCH_TABLE;


    private static final String CREATE_AllBatch_TABLE = "CREATE TABLE "+AllBatch_TABLE+" ("+AllBatchNAME_COLUMN+" VARCHAR(50), "+AllBatchTotalStudent_COLUMN+" VARCHAR(10));";
    private  static final String DROP_AllBatch_TABLE = "DROP TABLE IF EXISTS "+AllBatch_TABLE;

    private static final String CREATE_AllCourse_TABLE = "CREATE TABLE "+AllCourse_TABLE+" ("+AllCourseNAME_COLUMN+" VARCHAR(50));";
    private  static final String DROP_AllCourse_TABLE = "DROP TABLE IF EXISTS "+AllCourse_TABLE;

    private static final String CREATE_AllTeacher_TABLE = "CREATE TABLE "+AllTeacher_TABLE+" ("+AllTeacherName_COLUMN+" VARCHAR(50),"+AllTeacherId_COLUMN+" VARCHAR(50),"+AllTeacherUserName_COLUMN+" VARCHAR(50),"+AllTeacherPassword_COLUMN+" VARCHAR(50));";
    private  static final String DROP_AllTeacher_TABLE = "DROP TABLE IF EXISTS "+AllTeacher_TABLE;

    private static final String CREATE_CourseDistribution_TABLE = "CREATE TABLE "+CourseDistribution_TABLE+" ("+TeacherId_Column+" VARCHAR(50),"+CourseName_Column+" VARCHAR(50),"+Batch_Column+" VARCHAR(50));";
    private  static final String DROP_CourseDistribution_TABLE = "DROP TABLE IF EXISTS "+CourseDistribution_TABLE;




    public DatabaseHelper_3(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(CREATE_AllBatch_TABLE);
            db.execSQL(CREATE_AllCourse_TABLE);
            db.execSQL(CREATE_AllTeacher_TABLE);
            db.execSQL(CREATE_CourseDistribution_TABLE);
            db.execSQL(CREATE_SELECTED_COURSE_TABLE);
            Toast.makeText(context,"Successful",Toast.LENGTH_SHORT).show();
        }catch (SQLException e){
            Toast.makeText(context,"UnSuccessful",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_AllBatch_TABLE);
            db.execSQL(DROP_AllCourse_TABLE);
            db.execSQL(DROP_AllTeacher_TABLE);
            db.execSQL(DROP_CourseDistribution_TABLE);
            db.execSQL(DROP_SELECTED_COURSE_TABLE);
            onCreate(db);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void insertIntoAllBatch_TABLE(ArrayList<String> batch, ArrayList<String> total){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + AllBatch_TABLE);
        for (int i = 0; i < batch.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(AllBatchNAME_COLUMN,batch.get(i));
            contentValues.put(AllBatchTotalStudent_COLUMN,total.get(i));
            sqLiteDatabase.insert(AllBatch_TABLE,null,contentValues);
        }
        sqLiteDatabase.close();
    }


    public void insertIntoAllCourse_TABLE(ArrayList<String> courses){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + AllCourse_TABLE);
        for (int i = 0; i < courses.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(AllCourseNAME_COLUMN,courses.get(i));
            sqLiteDatabase.insert(AllCourse_TABLE,null,contentValues);
        }
        sqLiteDatabase.close();
    }


    public Cursor getBatchNames(String teacher_id) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String insert = "SELECT " + Batch_Column + " FROM " + CourseDistribution_TABLE + " where " + TeacherId_Column + " = " + teacher_id;

        Cursor cursor = sqLiteDatabase.rawQuery(insert, null);

        return cursor;

    }

    public void insertIntoUsedCourse_TABLE(ArrayList<String> courses) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        for (int i = 0; i < courses.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(Batch_Column,courses.get(i));
            sqLiteDatabase.insert(SELECTED_BATCH_TABLE,null,contentValues);
        }
        sqLiteDatabase.close();
    }



    public void insertIntoCourseDistribution_TABLE(ArrayList<String> teacherId, ArrayList<String> courseId, ArrayList<String> batch){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + CourseDistribution_TABLE);
        for (int i = 0; i < teacherId.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(TeacherId_Column,teacherId.get(i));
            contentValues.put(CourseName_Column,courseId.get(i));
            contentValues.put(Batch_Column,batch.get(i));
            sqLiteDatabase.insert(CourseDistribution_TABLE,null,contentValues);
        }
        sqLiteDatabase.close();
    }


    public void insertIntoAllTeacher_TABLE(ArrayList<String> teacherName, ArrayList<String> teacherId, ArrayList<String> teacherUserName, ArrayList<String> teacherPassword){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + AllTeacher_TABLE);
        for (int i = 1; i < teacherName.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(AllTeacherName_COLUMN,teacherName.get(i));
            contentValues.put(AllTeacherId_COLUMN,teacherId.get(i));
            contentValues.put(AllTeacherPassword_COLUMN,teacherPassword.get(i));
            contentValues.put(AllTeacherUserName_COLUMN,teacherUserName.get(i));
            sqLiteDatabase.insert(AllTeacher_TABLE,null,contentValues);
        }
        sqLiteDatabase.close();
    }


}
