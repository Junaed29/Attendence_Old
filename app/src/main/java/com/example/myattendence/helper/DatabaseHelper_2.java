package com.example.myattendence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseHelper_2 extends SQLiteOpenHelper {
    private Context context;
    private static final int VERSION_NUMBER = 1;
    private static final String DATABASE_NAME = "Admin.db";


    //Table Names
    private static final String AllBatch_TABLE = "AllBatchInfo";
    private static final String AllBatchNAME_COLUMN = "allbatchname";
    private static final String AllBatchTotalStudent_COLUMN = "totalStudent";
    private static final String AllCourse_TABLE = "AllCourseInfo";
    private static final String AllCourseNAME_COLUMN = "allcoursename";
    private static final String AllTeacher_TABLE = "AllTeacherInfo";
    private static final String AllTeacherName_COLUMN = "allteachername";
    private static final String AllTeacherId_COLUMN = "allteacherid";
    private static final String CourseDistribution_TABLE = "CourseDistributionInfo";
    private static final String CourseName_Column = "courseName";
    private static final String TeacherId_Column = "teacherId";
    private static final String Batch_Column = "batch";

    private static final String CREATE_AllBatch_TABLE = "CREATE TABLE "+AllBatch_TABLE+" ("+AllBatchNAME_COLUMN+" VARCHAR(50), "+AllBatchTotalStudent_COLUMN+" VARCHAR(10));";
    private  static final String DROP_AllBatch_TABLE = "DROP TABLE IF EXISTS "+AllBatch_TABLE;

    private static final String CREATE_AllCourse_TABLE = "CREATE TABLE "+AllCourse_TABLE+" ("+AllCourseNAME_COLUMN+" VARCHAR(50));";
    private  static final String DROP_AllCourse_TABLE = "DROP TABLE IF EXISTS "+AllCourse_TABLE;

    private static final String CREATE_AllTeacher_TABLE = "CREATE TABLE "+AllTeacher_TABLE+" ("+AllTeacherName_COLUMN+" VARCHAR(50),"+AllTeacherId_COLUMN+" VARCHAR(50));";
    private  static final String DROP_AllTeacher_TABLE = "DROP TABLE IF EXISTS "+AllTeacher_TABLE;

    private static final String CREATE_CourseDistribution_TABLE = "CREATE TABLE "+CourseDistribution_TABLE+" ("+TeacherId_Column+" VARCHAR(50),"+CourseName_Column+" VARCHAR(50),"+Batch_Column+" VARCHAR(50));";
    private  static final String DROP_CourseDistribution_TABLE = "DROP TABLE IF EXISTS "+CourseDistribution_TABLE;




    public DatabaseHelper_2(Context context) {
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
            Toast.makeText(context,"Successful",Toast.LENGTH_SHORT).show();
        }catch (SQLException e){
            Toast.makeText(context,"Unuccessful",Toast.LENGTH_SHORT).show();
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

    public void insertIntoAllBatch_TABLE(String batch, String total){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(AllBatchNAME_COLUMN,batch);
            contentValues.put(AllBatchTotalStudent_COLUMN,total);
            sqLiteDatabase.insert(AllBatch_TABLE,null,contentValues);
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

    public void insertIntoAllCourse_TABLE(String courses){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(AllCourseNAME_COLUMN,courses);
            sqLiteDatabase.insert(AllCourse_TABLE,null,contentValues);

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

    public void insertIntoCourseDistribution_TABLE(String teacherId, String courseId, String batch){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(TeacherId_Column,teacherId);
            contentValues.put(CourseName_Column,courseId);
            contentValues.put(Batch_Column,batch);
            sqLiteDatabase.insert(CourseDistribution_TABLE,null,contentValues);

        sqLiteDatabase.close();
    }

    public void insertIntoAllTeacher_TABLE(ArrayList<String> teacherName, ArrayList<String> teacherId){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + AllTeacher_TABLE);
        for (int i = 1; i < teacherName.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(AllTeacherName_COLUMN,teacherName.get(i));
            contentValues.put(AllTeacherId_COLUMN,teacherId.get(i));
            sqLiteDatabase.insert(AllTeacher_TABLE,null,contentValues);
        }
        sqLiteDatabase.close();
    }

    public void insertSingleValueIntoAllTeacher_TABLE(String name, String Id)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AllTeacherName_COLUMN,name);
        contentValues.put(AllTeacherId_COLUMN,Id);
        sqLiteDatabase.insert(AllTeacher_TABLE,null,contentValues);
        sqLiteDatabase.close();
    }

    public Cursor getAllTeacherInfo()
    {
        String SELECT_ALL = "select `"+AllTeacherName_COLUMN+"`,`"+AllTeacherId_COLUMN+"` from '"+AllTeacher_TABLE+"';";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_ALL,null);
        return cursor;
    }

    public Cursor getAllCourseName()
    {
        String SELECT_ALL = "select `"+AllCourseNAME_COLUMN+"` from '"+AllCourse_TABLE+"';";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_ALL,null);
        return cursor;
    }


    public Cursor getAllBatchInfo()
    {
        String SELECT_ALL = "select `"+AllBatchNAME_COLUMN+"`,`"+AllBatchTotalStudent_COLUMN+"` from '"+AllBatch_TABLE+"';";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_ALL,null);
        return cursor;
    }

    public Cursor getDistributedCoursesInfo()
    {
        String SELECT_ALL = "select * from '"+CourseDistribution_TABLE+"';";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_ALL,null);
        return cursor;
    }

    public Cursor getTeacherNameFromTeacherInfo(String teacherId)
    {
        String SELECT_ALL = "select allteachername from AllTeacherInfo where allteacherid = '"+teacherId+"';";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_ALL,null);
        return cursor;
    }


}
