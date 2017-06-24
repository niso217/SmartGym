package com.a.n.smartgym.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.a.n.smartgym.DatabaseManager;
import com.a.n.smartgym.model.Plan;
import com.a.n.smartgym.model.Plan;


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


    public void delete() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Plan.TABLE, null, null);
        DatabaseManager.getInstance().closeDatabase();
    }


}
