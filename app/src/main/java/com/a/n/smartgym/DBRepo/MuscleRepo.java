package com.a.n.smartgym.DBRepo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.a.n.smartgym.DBModel.Exercise;
import com.a.n.smartgym.DBModel.MuscleExercise;
import com.a.n.smartgym.DBModel.Plan;
import com.a.n.smartgym.DBModel.PlanMuscle;
import com.a.n.smartgym.DBModel.User;
import com.a.n.smartgym.DBModel.Visits;
import com.a.n.smartgym.Utils.DatabaseManager;
import com.a.n.smartgym.DBModel.Muscle;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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
                + " WHERE " + Muscle.TABLE +"."+Muscle.KEY_MUSCLE + " IN("+main+")";

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

    public LinkedHashMap<String,ArrayList<Muscle>> getHashSubMuscle(String main, String sub){
        Muscle muscle;
        String mainMuscle;
        LinkedHashMap<String,ArrayList<Muscle>> muscleHashtable = new LinkedHashMap<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + Muscle.TABLE
                + " WHERE " + Muscle.TABLE +"."+Muscle.KEY_MUSCLE + " IN("+main+")";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                mainMuscle = cursor.getString(cursor.getColumnIndex(Muscle.KEY_MUSCLE));
                muscle= new Muscle();
                muscle.setId(cursor.getString(cursor.getColumnIndex(Muscle.KEY_ID)));
                muscle.setMuscle(mainMuscle);
                muscle.setMain(cursor.getString(cursor.getColumnIndex(Muscle.KEY_MAIN)));
                muscle.setSecondary(cursor.getString(cursor.getColumnIndex(Muscle.KEY_SECONDARY)));
                muscle.setName(cursor.getString(cursor.getColumnIndex(Muscle.KEY_NAME)));
                muscle.setImage(cursor.getString(cursor.getColumnIndex(Muscle.KEY_IMAGE)));
                muscle.setDescription(cursor.getString(cursor.getColumnIndex(Muscle.KEY_DESCRIPTION)));

                ArrayList<Muscle> temp = muscleHashtable.get(mainMuscle);
                if (temp!=null) {
                    temp.add(muscle);
                    muscleHashtable.put(mainMuscle,temp);
                }
                else
                {
                    temp = new ArrayList<>();
                    temp.add(muscle);
                    muscleHashtable.put(mainMuscle,temp);
                }

            }
            while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return muscleHashtable;

    }



    public String getMainMuscle(String sub){
        String Mainmuscle = "";

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Muscle.TABLE +"." +Muscle.KEY_MUSCLE
                + " FROM " + Muscle.TABLE
                + " WHERE " + Muscle.TABLE +"."+Muscle.KEY_NAME + "='"+ sub+ "'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Mainmuscle = cursor.getString(cursor.getColumnIndex(Muscle.KEY_MUSCLE));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return Mainmuscle;

    }

    public String [] getExerciseNameById(String id)
    {
        String sub = "";
        String main = "";

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT  " + Muscle.TABLE +"."+Muscle.KEY_NAME +","+Muscle.TABLE +"."+Muscle.KEY_MUSCLE
                + " FROM " + Muscle.TABLE
                + " WHERE " + Muscle.TABLE +"."+Muscle.KEY_ID +"=" + "'"+id+"'";

        Log.d(TAG, selectQuery);



        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                 sub =  cursor.getString(cursor.getColumnIndex(Muscle.KEY_NAME));
                main = cursor.getString(cursor.getColumnIndex(Muscle.KEY_MUSCLE));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return new String []{main,sub};

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


    public Muscle getExerciseByID(String id, String day){
        Muscle muscle = new Muscle();
        ArrayList<Muscle> muscleArrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT * "
                + " FROM " + Muscle.TABLE
                + " INNER JOIN " + MuscleExercise.TABLE + " ON " + MuscleExercise.TABLE + "." + MuscleExercise.KEY_EXERCISE_ID + "=" + Muscle.TABLE + "." + Muscle.KEY_NAME
                + " INNER JOIN " + PlanMuscle.TABLE + " ON " + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_MUSCLE_ID + "=" + MuscleExercise.TABLE + "." + MuscleExercise.KEY_PLAN_MUSCLE_ID
                + " INNER JOIN " + Plan.TABLE + " ON " + Plan.TABLE + "." + Plan.KEY_PLAN_ID + "=" + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_ID
                + " WHERE " + Muscle.TABLE +"."+Muscle.KEY_ID + "="+ "'"+id+"'"
                + " AND " + Plan.TABLE +"."+Plan.KEY_DATE + " IN(" +day+ ")";


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
                muscle.setNum_reps(cursor.getString(cursor.getColumnIndex(MuscleExercise.KEY_NUM_OF_REPS)));
                muscle.setNum_sets(cursor.getString(cursor.getColumnIndex(MuscleExercise.KEY_NUM_OF_SETS)));
                muscle.setWeight(cursor.getString(cursor.getColumnIndex(MuscleExercise.KEY_WEIGHT)));

            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return muscle;

    }

    public ArrayList<String> getAllMuscleID(){
        Muscle muscle;
        ArrayList<String> muscleArrayList = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + Muscle.TABLE +"."+Muscle.KEY_ID
                + " FROM " + Muscle.TABLE;

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                muscleArrayList.add(cursor.getString(cursor.getColumnIndex(Muscle.KEY_ID)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return muscleArrayList;

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

    public List<String> getMainMuscleNames(){
        List<String> main_muscle = new ArrayList<>();
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
                main_muscle.add(cursor.getString(cursor.getColumnIndex(Muscle.KEY_MUSCLE)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return main_muscle;

    }


    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Muscle.TABLE, null,null);
        DatabaseManager.getInstance().closeDatabase();
    }








}
