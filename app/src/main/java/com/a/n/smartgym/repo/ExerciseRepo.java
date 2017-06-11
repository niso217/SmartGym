package com.a.n.smartgym.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.a.n.smartgym.DatabaseManager;
import com.a.n.smartgym.Objects.LastExercise;
import com.a.n.smartgym.Quary.DailyAverage;
import com.a.n.smartgym.Quary.MachineUsage;
import com.a.n.smartgym.model.Exercise;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.model.Sets;
import com.a.n.smartgym.model.User;
import com.a.n.smartgym.model.Visits;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tan on 1/26/2016.
 */
public class ExerciseRepo {

    private Exercise exercise;
    public static final String TAG = ExerciseRepo.class.getSimpleName();


    public ExerciseRepo() {

        exercise = new Exercise();

    }


    public static String createTable() {
        return "CREATE TABLE " + Exercise.TABLE + "("
                + Exercise.KEY_EXERCISE_ID + " TEXT  PRIMARY KEY,"
                + Exercise.KEY_VISIT_ID + " TEXT, "
                + Exercise.KEY_MACHINE_NAME + " TEXT, "
                + Exercise.KEY_START + " INTEGER, "
                + Exercise.KEY_END + " INTEGER )";
    }


    public void insert(Exercise exercise) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Exercise.KEY_EXERCISE_ID, exercise.getexerciseid());
        values.put(Exercise.KEY_VISIT_ID, exercise.getVisitid());
        values.put(Exercise.KEY_MACHINE_NAME, exercise.getMachinename());
        values.put(Exercise.KEY_START, String.valueOf(exercise.getStart()));
        values.put(Exercise.KEY_END, String.valueOf(exercise.getEnd()));

        // Inserting Row
        db.insert(Exercise.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

    }

    public void update(String id, long time) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Exercise.KEY_END, time);
        // update Row
        db.update(Exercise.TABLE, values, Exercise.TABLE + "." + Exercise.KEY_EXERCISE_ID + "=" + "'" + id + "'", null);
        DatabaseManager.getInstance().closeDatabase();

    }


    public ArrayList<DailyAverage> getAllDaysAverages(String user_id) {
        DailyAverage dailyAverage;
        ArrayList<DailyAverage> DailyAverages = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + User.TABLE + "." + User.KEY_FIRST_NAME
                + ", " + Visits.TABLE + "." + Visits.KEY_DATE
                + ", " + Exercise.TABLE + "." + Exercise.KEY_MACHINE_NAME
                + ", AVG(" + Sets.TABLE + "." + Sets.KEY_WEIGHT + ") as average FROM " + User.TABLE
                + " INNER JOIN " + Visits.TABLE + " ON " + Visits.TABLE + "." + Visits.KEY_USER_ID + "=" + User.TABLE + "." + User.KEY_USER_ID
                + " INNER JOIN " + Exercise.TABLE + " ON " + Exercise.TABLE + "." + Exercise.KEY_VISIT_ID + "=" + Visits.TABLE + "." + Visits.KEY_VISIT_ID
                + " INNER JOIN " + Sets.TABLE + " ON " + Sets.TABLE + "." + Sets.KEY_EXERCISE_ID + "=" + Exercise.TABLE + "." + Exercise.KEY_EXERCISE_ID
                + " WHERE " + User.TABLE + "." + User.KEY_USER_ID + "=" + "'" + user_id + "'"
                + " GROUP BY " + Exercise.TABLE + "." + Exercise.KEY_MACHINE_NAME + " , " + Visits.TABLE + "." + Visits.KEY_DATE
                + " ORDER BY date(" + Visits.TABLE + "." + Visits.KEY_DATE + ") ASC";


        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                dailyAverage = new DailyAverage();
                dailyAverage.setMachine_name(cursor.getString(cursor.getColumnIndex(Exercise.KEY_MACHINE_NAME)));
                dailyAverage.setDate(cursor.getString(cursor.getColumnIndex(Visits.KEY_DATE)));
                //dailyAverage.setAverage(cursor.getDouble(cursor.getColumnIndex("average")));

                DailyAverages.add(dailyAverage);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return DailyAverages;

    }


    public ArrayList<DailyAverage> getAllDaysAverages2(String user_id, String ex) {
        DailyAverage dailyAverage;
        ArrayList<DailyAverage> DailyAverages = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = "\t\t\t\tselect Visits.date,Exercise.machinename,sum(Sets.count) as count,avg(Sets.weight) as average\n" +
                "\t\t\t\tFROM User INNER JOIN Visits ON Visits.userid=User.userid\n" +
                "                INNER JOIN Exercise ON Exercise.visitid=Visits.visitid\n" +
                "                INNER JOIN Sets ON Sets.exerciseid=Exercise.exerciseid\n" +
                "                WHERE User.userid='3fiPmozQFuanqa7SfBbfqD0mlRj2' and Exercise.machinename='"+ex+"'\n" +
                "\t\t\t\tgroup by Exercise.machinename,Visits.date";


        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                dailyAverage = new DailyAverage();
                dailyAverage.setMachine_name(cursor.getString(cursor.getColumnIndex(Exercise.KEY_MACHINE_NAME)));
                dailyAverage.setDate(cursor.getString(cursor.getColumnIndex(Visits.KEY_DATE)));
                dailyAverage.setAverage(cursor.getInt(cursor.getColumnIndex("average")));
                dailyAverage.setCount(cursor.getInt(cursor.getColumnIndex(Sets.KEY_COUNT)));

                DailyAverages.add(dailyAverage);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return DailyAverages;

    }

    public ArrayList<String> getAllExercises(String user_id) {
        ArrayList<String> exercises = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = "\t\tselect Exercise.machinename\n" +
                "\t\t\t\tFROM User INNER JOIN Visits ON Visits.userid=User.userid\n" +
                "                INNER JOIN Exercise ON Exercise.visitid=Visits.visitid\n" +
                "                INNER JOIN Sets ON Sets.exerciseid=Exercise.exerciseid\n" +
                "                WHERE User.userid='3fiPmozQFuanqa7SfBbfqD0mlRj2'\n" +
                "\t\t\t\tgroup by Exercise.machinename";


        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                exercises.add(cursor.getString(cursor.getColumnIndex(Exercise.KEY_MACHINE_NAME)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return exercises;

    }


    public ArrayList<LastExercise> getLastSummary(String user_id, String name) {
        LastExercise lastExercise;
        ArrayList<LastExercise> LastExercises = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =
        "SELECT sum(Sets.count) as count, avg(Sets.weight) as weight, Exercise.machinename, strftime('%Y-%m-%d', Exercise.start / 1000, 'unixepoch') as date\n" +
                "                FROM User INNER JOIN Visits ON Visits.userid=User.userid\n" +
                "                INNER JOIN Exercise ON Exercise.visitid=Visits.visitid\n" +
                "                INNER JOIN Sets ON Sets.exerciseid=Exercise.exerciseid\n" +
                "                WHERE User.userid='3fiPmozQFuanqa7SfBbfqD0mlRj2'\n" +
                "\t\t\t\tand date>=(\n" +
                "\t\t\t\tselect date(strftime('%Y-%m-%d', max(Exercise.start) / 1000, 'unixepoch'))\n" +
                "\t\t\t\tFROM User INNER JOIN Visits ON Visits.userid=User.userid\n" +
                "                INNER JOIN Exercise ON Exercise.visitid=Visits.visitid\n" +
                "                INNER JOIN Sets ON Sets.exerciseid=Exercise.exerciseid\n" +
                "                WHERE User.userid='3fiPmozQFuanqa7SfBbfqD0mlRj2'\n" +
                "\t\t\t\t)\n" +
                "                group by Exercise.machinename,date";


        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                lastExercise = new LastExercise();
                lastExercise.setDate(cursor.getString(cursor.getColumnIndex(Visits.KEY_DATE)));
                lastExercise.setWeight(cursor.getInt(cursor.getColumnIndex(Sets.KEY_WEIGHT)));
                lastExercise.setCount(cursor.getInt(cursor.getColumnIndex(Sets.KEY_COUNT)));
                lastExercise.setName(cursor.getString(cursor.getColumnIndex(Exercise.KEY_MACHINE_NAME)));

                LastExercises.add(lastExercise);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return LastExercises;

    }

    public ArrayList<LastExercise> getLastExercise(String user_id, String name) {
        LastExercise lastExercise;
        ArrayList<LastExercise> LastExercises = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + Visits.TABLE + "." + Visits.KEY_DATE
                + ", " + Sets.TABLE + "." + Sets.KEY_WEIGHT
                + ", " + Sets.TABLE + "." + Sets.KEY_COUNT
                + ",COUNT(*) " + Sets.TABLE
                + " FROM " + User.TABLE
                + " INNER JOIN " + Visits.TABLE + " ON " + Visits.TABLE + "." + Visits.KEY_USER_ID + "=" + User.TABLE + "." + User.KEY_USER_ID
                + " INNER JOIN " + Exercise.TABLE + " ON " + Exercise.TABLE + "." + Exercise.KEY_VISIT_ID + "=" + Visits.TABLE + "." + Visits.KEY_VISIT_ID
                + " INNER JOIN " + Sets.TABLE + " ON " + Sets.TABLE + "." + Sets.KEY_EXERCISE_ID + "=" + Exercise.TABLE + "." + Exercise.KEY_EXERCISE_ID
                + " WHERE " + User.TABLE + "." + User.KEY_USER_ID + "=" + "'" + user_id + "'"
                + " AND " + Exercise.TABLE + "." + Exercise.KEY_MACHINE_NAME + "=" + "'" + name + "'"
                + " AND " + Visits.TABLE + "." + Visits.KEY_DATE + "=" +
                "(SELECT MAX(" + Visits.TABLE + "." + Visits.KEY_DATE + ") " + " FROM " + Visits.TABLE
                + " WHERE " + User.TABLE + "." + User.KEY_USER_ID + "=" + "'" + user_id + "' AND " + Visits.TABLE + "." + Visits.KEY_DATE + "<" + "DATE('now'))"
                + " GROUP BY " + Visits.TABLE + "." + Visits.KEY_DATE + "," + Sets.TABLE + "." + Sets.KEY_WEIGHT + "," + Sets.TABLE + "." + Sets.KEY_COUNT;


        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                lastExercise = new LastExercise();
                lastExercise.setDate(cursor.getString(cursor.getColumnIndex(Visits.KEY_DATE)));
                lastExercise.setWeight(cursor.getInt(cursor.getColumnIndex(Sets.KEY_WEIGHT)));
                lastExercise.setCount(cursor.getInt(cursor.getColumnIndex(Sets.KEY_COUNT)));
                lastExercise.setSets(cursor.getInt(cursor.getColumnIndex(Sets.TABLE)));

                LastExercises.add(lastExercise);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return LastExercises;

    }

    public ArrayList<MachineUsage> getUsage(String user_id) {
        MachineUsage machineUsage;
        ArrayList<MachineUsage> machineUsages = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT "
                + Exercise.TABLE + "." + Exercise.KEY_MACHINE_NAME
                + ", COUNT(" + Sets.TABLE + "." + Sets.KEY_SET_ID + ") as counter FROM " + User.TABLE
                + " INNER JOIN " + Visits.TABLE + " ON " + Visits.TABLE + "." + Visits.KEY_USER_ID + "=" + User.TABLE + "." + User.KEY_USER_ID
                + " INNER JOIN " + Exercise.TABLE + " ON " + Exercise.TABLE + "." + Exercise.KEY_VISIT_ID + "=" + Visits.TABLE + "." + Visits.KEY_VISIT_ID
                + " INNER JOIN " + Sets.TABLE + " ON " + Sets.TABLE + "." + Sets.KEY_EXERCISE_ID + "=" + Exercise.TABLE + "." + Exercise.KEY_EXERCISE_ID
                + " WHERE " + User.TABLE + "." + User.KEY_USER_ID + "=" + "'" + user_id + "'"
                + " GROUP BY " + Exercise.TABLE + "." + Exercise.KEY_MACHINE_NAME
                + " ORDER BY counter DESC "
                ;


        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                machineUsage = new MachineUsage();
                machineUsage.setMachine_name(cursor.getString(cursor.getColumnIndex(Exercise.KEY_MACHINE_NAME)));
                machineUsage.setCounter(cursor.getInt(cursor.getColumnIndex("counter")));

                machineUsages.add(machineUsage);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        int sum = CalcSum(machineUsages);

        for (int i = 0; i < machineUsages.size(); i++) {
            machineUsages.get(i).setPresent(100L * machineUsages.get(i).getCounter() / sum);

        }

        return machineUsages;

    }

    public ArrayList<MachineUsage> getUsage2(String user_id) {
        MachineUsage machineUsage;
        ArrayList<MachineUsage> machineUsages = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = "\t\t\t\tselect sum(Sets.count) as count, MUSCLE.muscle\n" +
                "\t\t\t\tFROM User INNER JOIN Visits ON Visits.userid=User.userid\n" +
                "                INNER JOIN Exercise ON Exercise.visitid=Visits.visitid\n" +
                "                INNER JOIN Sets ON Sets.exerciseid=Exercise.exerciseid\n" +
                "                INNER JOIN MUSCLE  ON MUSCLE.name = Exercise.machinename\n" +
                "                WHERE User.userid='3fiPmozQFuanqa7SfBbfqD0mlRj2'\n" +
                "\t\t\t\tgroup by MUSCLE.muscle"
                ;


        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                machineUsage = new MachineUsage();
                machineUsage.setMuscle(cursor.getString(cursor.getColumnIndex(Muscle.KEY_MUSCLE)));
                machineUsage.setCounter(cursor.getInt(cursor.getColumnIndex(Sets.KEY_COUNT)));

                machineUsages.add(machineUsage);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

//        int sum = CalcSum(machineUsages);
//
//        for (int i = 0; i < machineUsages.size(); i++) {
//            machineUsages.get(i).setPresent(100L * machineUsages.get(i).getCounter() / sum);
//
//        }

        return machineUsages;

    }



    private int CalcSum(List<MachineUsage> list) {
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i).getCounter();
        }
        return sum;
    }


    public void delete() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Exercise.TABLE, null, null);
        DatabaseManager.getInstance().closeDatabase();
    }


}
