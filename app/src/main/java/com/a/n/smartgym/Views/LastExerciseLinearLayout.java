package com.a.n.smartgym.Views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a.n.smartgym.R;

/**
 * Created by nirb on 27/06/2017.
 */

public class LastExerciseLinearLayout extends LinearLayout {

    private TextView tv_num,tv_reps,tv_weight,tv_sets;



    public void setTv_num(String num) {
        this.tv_num.setText(num);
    }


    public void setTv_reps(String tv_reps) {
        this.tv_reps.setText(tv_reps);
    }



    public void setTv_weight(String tv_weight) {
        this.tv_weight.setText(tv_weight);
    }

    public void setTv_sets(String tv_sets) {
        this.tv_sets.setText(tv_sets);
    }


    public LastExerciseLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public LastExerciseLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LastExerciseLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public LastExerciseLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.custom_last_exercise_tab, this);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_reps = (TextView) findViewById(R.id.tv_reps);
        tv_weight = (TextView) findViewById(R.id.tv_weight);
        tv_sets = (TextView) findViewById(R.id.tv_sets);

    }
}
