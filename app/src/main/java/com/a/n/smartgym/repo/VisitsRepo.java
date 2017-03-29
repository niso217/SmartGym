package com.a.n.smartgym.repo;

import android.bluetooth.BluetoothClass;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.a.n.smartgym.DatabaseManager;
import com.a.n.smartgym.model.StudentCourse;
import com.a.n.smartgym.model.User;
import com.a.n.smartgym.model.Visits;


/**
 * Created by Tan on 1/26/2016.
 */
public class VisitsRepo {

    private Visits visit;
    private static final String TAG = UserRepo.class.getSimpleName();

    public VisitsRepo(){

        visit= new Visits();

    }



    public static String createTable(){
        return "CREATE TABLE " + Visits.TABLE  + "("
                + Visits.KEY_VISIT_ID + " TEXT  PRIMARY KEY,"
                + Visits.KEY_USER_ID + " TEXT, "
                + Visits.KEY_DATE + " DATETIME DEFAULT CURRENT_DATE )";
    }



    public void insert(Visits visit) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Visits.KEY_VISIT_ID, visit.getVisitid());
        values.put(Visits.KEY_USER_ID, visit.getUserid());
        values.put(Visits.KEY_DATE, String.valueOf(visit.getDate()));

        // Inserting Row
        db.insert(Visits.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

    }

    //return true if user exist
    public boolean isDateExist() {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + Visits.TABLE +"." + Visits.KEY_DATE
                + " FROM " + Visits.TABLE
                + " WHERE " + Visits.KEY_DATE + "=" +  "date('now')";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return cursor.getCount() > 0;
    }

    public String getCurrentUUID(String user_id){
        String uuid = "";
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + Visits.TABLE +"." + Visits.KEY_VISIT_ID
                + " FROM " + Visits.TABLE
                + " WHERE " + Visits.KEY_DATE + "=" +  "date('now') and " + User.KEY_USER_ID+"='"+user_id+"'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst())
        {
             uuid = cursor.getString(cursor.getColumnIndex(Visits.KEY_VISIT_ID));
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return uuid;

    }



    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Visits.TABLE, null,null);
        DatabaseManager.getInstance().closeDatabase();
    }








}
