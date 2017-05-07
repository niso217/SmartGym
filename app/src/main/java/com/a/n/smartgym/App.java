package com.a.n.smartgym;

import android.app.Application;
import android.content.Context;
import android.renderscript.Sampler;
import android.util.Log;

import com.a.n.smartgym.Helpers.DBHelper;
import com.a.n.smartgym.Objects.Muscles;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.a.n.smartgym.repo.MuscleRepo;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Tan on 1/26/2016.
 */
public class App extends Application {
    private static Context context;
    private static DBHelper dbHelper;
    private RequestQueue requestQueue;
    private static final String TAG = Application.class.getSimpleName();
    //private static final String ENDPOINT = "https://api.myjson.com/bins/actel";
    //private static final String ENDPOINT = "https://api.myjson.com/bins/7i3y9";
    private static final String ENDPOINT = "https://api.myjson.com/bins/1cgy35";


    private Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        gson = new Gson();
        fetchPosts();


    }

    private void fetchPosts() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onPostsLoaded, onPostsError);

        requestQueue.add(request);
    }

//    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
//        @Override
//        public void onResponse(String response) {
//            Log.i(TAG, response);
//
//            Muscles[][] mus = gson.fromJson(response, Muscles[][].class);
//
//            for (int i = 0; i < mus.length; i++) {
//                ExercisesDB.getInstance().DB.put(Constance.MuscleType.values()[i].toString(), Arrays.asList(mus[i]));
//
//            }
//            ExercisesDB.getInstance().keys = new ArrayList<String>(ExercisesDB.getInstance().DB.keySet());
//        }
//    };

//    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
//        @Override
//        public void onResponse(String response) {
//            Log.i(TAG, response);
//            Map<String, Muscle> categoryMap = gson.fromJson(response, new TypeToken<Map<String, Muscle>>() {
//            }.getType());
//            List<Muscle> list = new ArrayList<>(categoryMap.values());
//            MuscleRepo muscleRepo = new MuscleRepo();
//            muscleRepo.BulkMuscle(list);
//
//
//            ArrayList list2 = muscleRepo.getMuscle("ARMS","");
//            Log.d(TAG, response.toString());
//
//        }
//    };

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.i(TAG, response);
            Map<String, Map<String,ArrayList<Muscle>>> categoryMap = gson.fromJson(response, new TypeToken<Map<String, Map<String,ArrayList<Muscle>>>>() {
            }.getType());
            //List<Muscle> list = new ArrayList<>(categoryMap.values());
            //MuscleRepo muscleRepo = new MuscleRepo();
            //muscleRepo.BulkMuscle(list);
            List<Muscle> list = new ArrayList<>();

            Iterator<Map.Entry<String, Map<String, ArrayList<Muscle>>>> parent = categoryMap.entrySet().iterator();
            while (parent.hasNext()) {
                Map.Entry<String, Map<String, ArrayList<Muscle>>> parentPair = parent.next();
                System.out.println("parentPair.getKey() :   " + parentPair.getKey() + " parentPair.getValue()  :  " + parentPair.getValue());

                Iterator<Map.Entry<String, ArrayList<Muscle>>> child = (parentPair.getValue()).entrySet().iterator();
                while (child.hasNext()) {
                    Map.Entry childPair = child.next();
                    System.out.println("childPair.getKey() :   " + childPair.getKey() + " childPair.getValue()  :  " + childPair.getValue());
                    List<Muscle> mus = (List<Muscle>) childPair.getValue();
                    list.addAll(mus);
                    child.remove(); // avoids a ConcurrentModificationException
                }

            }


            //ArrayList list2 = muscleRepo.getMuscle("ARMS","");
            Log.d(TAG, response.toString());

        }
    };


    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, error.toString());
        }
    };


    public static Context getContext() {
        return context;
    }

}

