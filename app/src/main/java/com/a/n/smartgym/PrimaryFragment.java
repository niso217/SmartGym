package com.a.n.smartgym;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a.n.smartgym.Objects.Dates;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ratan on 7/29/2015.
 */
public class PrimaryFragment extends Fragment {
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mAuth;
    private static final String TAG = PrimaryFragment.class.getSimpleName();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootFragment = inflater.inflate(R.layout.primary_layout, null);

        GraphView graph = (GraphView) rootFragment.findViewById(R.id.graph);
        addSessionChangeListener();
        //initGraph(graph);
        return rootFragment;
    }

    public void initGraph(GraphView graph) {
        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graph.getContext()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);

        // set manual x bounds to have nice steps
        //graph.getViewport().setMinX(d1.getTime());
        //graph.getViewport().setMaxX(d3.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not nessecary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }



    private void addSessionChangeListener() {
        // User data change listener
        mFirebaseDatabase
                .child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            // TODO: handle the post
                            //Dates dates = postSnapshot.getValue(Dates.class);
                            Log.d(TAG,postSnapshot.toString());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.e(TAG, "Failed to read user", error.toException());
                    }
                });
    }
}
