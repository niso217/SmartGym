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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


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

    public String getRandomDateUUID(String user_id,Date date){
        String uuid = "";
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + Visits.TABLE +"." + Visits.KEY_VISIT_ID
                + " FROM " + Visits.TABLE
                + " WHERE " + Visits.KEY_DATE + "=" + date+" and " + User.KEY_USER_ID+"='"+user_id+"'";

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

    public List<Date> getAllVisitsDates(String user_id) {
        List<Date> dates = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + Visits.TABLE +"." + Visits.KEY_DATE
                + " FROM " + Visits.TABLE
                + " WHERE " + User.KEY_USER_ID+"='"+user_id+"'"
                + "ORDER BY date("+ Visits.TABLE +"." + Visits.KEY_DATE +") ASC";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex(Visits.KEY_DATE));
                dates.add(StringToDate(date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

//        Collections.sort(dates, new Comparator<Date>() {
//            @Override
//            public int compare(Date r1, Date r2) {
//                return r1.compareTo(r2);
//            }
//        });


        return dates;
    }



    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Visits.TABLE, null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    private Date StringToDate(String dateInString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        long startDate = 0;
        try {
            date = sdf.parse(dateInString);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }








}
