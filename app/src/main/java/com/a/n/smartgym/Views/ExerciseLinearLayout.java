package com.a.n.smartgym.Views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a.n.smartgym.R;
import com.daimajia.numberprogressbar.NumberProgressBar;

/**
 * Created by nirb on 27/06/2017.
 */

public class ExerciseLinearLayout extends LinearLayout {

    private TextView tv_num,tv_reps,tv_weight;



    public void setTv_num(String num) {
        this.tv_num.setText(num);
    }


    public void setTv_reps(String tv_reps) {
        this.tv_reps.setText(tv_reps);
    }



    public void setTv_weight(String tv_weight) {
        this.tv_weight.setText(tv_weight);
    }

    public ExerciseLinearLayout(Context context) {
        super(context);
        init(context,R.layout.custom_exercise_tab);
    }

    public ExerciseLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,R.layout.custom_exercise_tab);
    }

    public ExerciseLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,R.layout.custom_exercise_tab);

    }

    public ExerciseLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, int id) {
        View.inflate(context, id, this);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_reps = (TextView) findViewById(R.id.tv_reps);
        tv_weight = (TextView) findViewById(R.id.tv_weight);

    }
}
