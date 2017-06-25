package com.a.n.smartgym.DBRepo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.a.n.smartgym.Utils.DatabaseManager;
import com.a.n.smartgym.DBModel.Sets;

import java.util.List;


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
                + Sets.KEY_COUNT + " INTEGER, "
                + Sets.KEY_START + " INTEGER, "
                + Sets.KEY_END + " INTEGER )"
                ;
    }



    public void insert(Sets set) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Sets.KEY_SET_ID, set.getSetid());
        values.put(Sets.KEY_EXERCISE_ID, set.getexerciseid());
        values.put(Sets.KEY_WEIGHT, set.getWeight());
        values.put(Sets.KEY_COUNT, set.getCount());
        values.put(Sets.KEY_START, set.getStart());
        values.put(Sets.KEY_END, set.getEnd());


        // Inserting Row
        db.insert(Sets.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
    }


    public void BulkSets(List<Sets> list) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Sets set : list) {
                values.put(Sets.KEY_SET_ID, set.getSetid());
                values.put(Sets.KEY_EXERCISE_ID, set.getexerciseid());
                values.put(Sets.KEY_WEIGHT, set.getWeight());
                values.put(Sets.KEY_COUNT, set.getCount());
                values.put(Sets.KEY_START, set.getStart());
                values.put(Sets.KEY_END, set.getEnd());

                db.insert(Sets.TABLE, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            DatabaseManager.getInstance().closeDatabase();

        }
    }



    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Sets.TABLE, null,null);
        DatabaseManager.getInstance().closeDatabase();
    }



}
