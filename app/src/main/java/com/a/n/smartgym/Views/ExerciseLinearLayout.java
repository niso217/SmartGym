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

    TextView name;
    NumberProgressBar progressBar;


    public void setTitle(String title) {
        this.name.setText(title);
    }

    public void setProgress(int progress) {
        this.progressBar.setProgress(progress);
    }

    public ExerciseLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public ExerciseLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExerciseLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public ExerciseLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.custom_exercise, this);
        //setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        name = (TextView) findViewById(R.id.exercise_name);
        progressBar = (NumberProgressBar) findViewById(R.id.number_progress_bar);

    }
}
