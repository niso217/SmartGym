package com.a.n.smartgym.DBRepo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.a.n.smartgym.Utils.DatabaseManager;
import com.a.n.smartgym.DBModel.Plan;


/**
 * Created by Tan on 1/26/2016.
 */
public class PlanRepo {

    private Plan plan;
    private static final String TAG = PlanRepo.class.getSimpleName();

    public PlanRepo() {
        plan = new Plan();
    }


    public static String createTable() {
        return "CREATE TABLE " + Plan.TABLE + "("
                + Plan.KEY_PLAN_ID + " TEXT PRIMARY KEY  ,"
                + Plan.KEY_DATE + " TEXT )";
    }


    public void insert(Plan plan) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Plan.KEY_PLAN_ID, plan.getPlanid());
        values.put(Plan.KEY_DATE, plan.getDate());

        // Inserting Row
        db.insert(Plan.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
    }

    public String getDayUUID(String day){
        String uuid = "";
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + Plan.TABLE +"." + Plan.KEY_PLAN_ID
                + " FROM " + Plan.TABLE
                + " WHERE " + Plan.KEY_DATE+"='"+day+"'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst())
        {
            uuid = cursor.getString(cursor.getColumnIndex(Plan.KEY_PLAN_ID));
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return uuid;

    }


    public void delete() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Plan.TABLE, null, null);
        DatabaseManager.getInstance().closeDatabase();
    }


}
