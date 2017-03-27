package com.a.n.smartgym.repo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.a.n.smartgym.DatabaseManager;
import com.a.n.smartgym.model.Sets;
import com.a.n.smartgym.model.Sets;


/**
 * Created by Tan on 1/26/2016.
 */
public class SetsRepo {

    private Sets set;

    public SetsRepo(){

        set= new Sets();

    }


    public static String createTable(){
        return "CREATE TABLE " + Sets.TABLE  + "("
                + Sets.KEY_SET_ID + " TEXT  PRIMARY KEY,"
                + Sets.KEY_EXERCISE_ID + " TEXT, "
                + Sets.KEY_WEIGHT + " INTEGER, "
                + Sets.KEY_COUNT + " INTEGER )";
    }



    public void insert(Sets set) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Sets.KEY_SET_ID, set.getSetid());
        values.put(Sets.KEY_EXERCISE_ID, set.getexerciseid());
        values.put(Sets.KEY_WEIGHT, set.getWeight());
        values.put(Sets.KEY_COUNT, set.getCount());

        // Inserting Row
        db.insert(Sets.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
    }



    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Sets.TABLE, null,null);
        DatabaseManager.getInstance().closeDatabase();
    }





}
