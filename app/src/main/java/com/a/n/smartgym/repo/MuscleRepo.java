package com.a.n.smartgym.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.a.n.smartgym.DatabaseManager;
import com.a.n.smartgym.Objects.Muscles;
import com.a.n.smartgym.Quary.DailyAverage;
import com.a.n.smartgym.Quary.MachineUsage;
import com.a.n.smartgym.model.Exercise;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.model.Sets;
import com.a.n.smartgym.model.User;
import com.a.n.smartgym.model.Visits;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * Created by Tan on 1/26/2016.
 */
public class MuscleRepo {

    private Muscle muscle;
    public static final String TAG = MuscleRepo.class.getSimpleName();


    public MuscleRepo(){

        muscle= new Muscle();

    }



    public static String createTable(){
        return "CREATE TABLE " + Muscle.TABLE  + "("
                + Muscle.KEY_ID + " TEXT  PRIMARY KEY,"
                + Muscle.KEY_MUSCLE + " TEXT, "
                + Muscle.KEY_MAIN + " TEXT, "
                + Muscle.KEY_SECONDARY + " TEXT, "
                + Muscle.KEY_NAME + " TEXT, "
                + Muscle.KEY_IMAGE + " TEXT, "
                + Muscle.KEY_DESCRIPTION + " TEXT )";
    }



    public void insert(Muscle muscle) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Muscle.KEY_ID, muscle.getId());
        values.put(Muscle.KEY_MUSCLE, muscle.getMuscle());
        values.put(Muscle.KEY_MAIN, muscle.getMain());
        values.put(Muscle.KEY_SECONDARY, muscle.getSecondary());
        values.put(Muscle.KEY_NAME, muscle.getName());
        values.put(Muscle.KEY_IMAGE, muscle.getImage());
        values.put(Muscle.KEY_DESCRIPTION, muscle.getDescription());


        // Inserting Row
        db.insert(Muscle.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

    }


    public void BulkMuscle(List<Muscle> list) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Muscle muscle : list) {
                values.put(Muscle.KEY_ID, muscle.getId().toLowerCase());
                values.put(Muscle.KEY_MUSCLE, muscle.getMuscle().toLowerCase());
                values.put(Muscle.KEY_MAIN, muscle.getMain().toLowerCase());
                values.put(Muscle.KEY_SECONDARY, muscle.getSecondary().toLowerCase());
                values.put(Muscle.KEY_NAME, muscle.getName().toLowerCase());
                values.put(Muscle.KEY_IMAGE, muscle.getImage());
                values.put(Muscle.KEY_DESCRIPTION, muscle.getDescription());

                //db.insertWithOnConflict(Muscle.TABLE, null, values,SQLiteDatabase.CONFLICT_IGNORE);
                String quary = "REPLACE INTO " + Muscle.TABLE +
                        " VALUES ("+
                        "'"+muscle.getId().toLowerCase() +"'," +
                        "'"+muscle.getMuscle().toLowerCase() +"'," +
                        "'"+muscle.getMain().toLowerCase() +"'," +
                        "'"+muscle.getSecondary().toLowerCase() +"'," +
                        "'"+muscle.getName().toLowerCase() +"'," +
                        "'"+muscle.getImage() +"'," +
                        "'"+muscle.getDescription()+"'"+");";

                db.execSQL(quary);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            DatabaseManager.getInstance().closeDatabase();

        }
    }

    public ArrayList<Muscle> getSubMuscle(String main, String sub){
        Muscle muscle;
        ArrayList<Muscle> muscleArrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + Muscle.TABLE
                + " WHERE " + Muscle.TABLE +"."+Muscle.KEY_MUSCLE + "="+ "'"+main+"'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                muscle= new Muscle();
                muscle.setId(cursor.getString(cursor.getColumnIndex(Muscle.KEY_ID)));
                muscle.setMuscle(cursor.getString(cursor.getColumnIndex(Muscle.KEY_MUSCLE)));
                muscle.setMain(cursor.getString(cursor.getColumnIndex(Muscle.KEY_MAIN)));
                muscle.setSecondary(cursor.getString(cursor.getColumnIndex(Muscle.KEY_SECONDARY)));
                muscle.setName(cursor.getString(cursor.getColumnIndex(Muscle.KEY_NAME)));
                muscle.setImage(cursor.getString(cursor.getColumnIndex(Muscle.KEY_IMAGE)));
                muscle.setDescription(cursor.getString(cursor.getColumnIndex(Muscle.KEY_DESCRIPTION)));

                muscleArrayList.add(muscle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return muscleArrayList;

    }


    public Muscle getExerciseByID(String id){
        Muscle muscle = new Muscle();
        ArrayList<Muscle> muscleArrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + Muscle.TABLE
                + " WHERE " + Muscle.TABLE +"."+Muscle.KEY_ID + "="+ "'"+id+"'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                muscle.setId(cursor.getString(cursor.getColumnIndex(Muscle.KEY_ID)));
                muscle.setMuscle(cursor.getString(cursor.getColumnIndex(Muscle.KEY_MUSCLE)));
                muscle.setMain(cursor.getString(cursor.getColumnIndex(Muscle.KEY_MAIN)));
                muscle.setSecondary(cursor.getString(cursor.getColumnIndex(Muscle.KEY_SECONDARY)));
                muscle.setName(cursor.getString(cursor.getColumnIndex(Muscle.KEY_NAME)));
                muscle.setImage(cursor.getString(cursor.getColumnIndex(Muscle.KEY_IMAGE)));
                muscle.setDescription(cursor.getString(cursor.getColumnIndex(Muscle.KEY_DESCRIPTION)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return muscle;

    }

    public Hashtable<String,String> getMainMuscle(){
        Hashtable<String,String> keyValue = new Hashtable<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Muscle.TABLE +"."+Muscle.KEY_MUSCLE
                + ", "+Muscle.TABLE +"."+Muscle.KEY_IMAGE
                + " FROM " + Muscle.TABLE
                + " GROUP BY " + Muscle.TABLE +"."+Muscle.KEY_MUSCLE;

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                keyValue.put(cursor.getString(cursor.getColumnIndex(Muscle.KEY_MUSCLE)),
                        cursor.getString(cursor.getColumnIndex(Muscle.KEY_IMAGE)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return keyValue;

    }


    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Muscle.TABLE, null,null);
        DatabaseManager.getInstance().closeDatabase();
    }








}
