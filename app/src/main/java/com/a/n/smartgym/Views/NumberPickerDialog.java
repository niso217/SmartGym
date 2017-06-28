package com.a.n.smartgym.Views;

/**
 * Created by nirb on 25/06/2017.
 */

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;

import com.a.n.smartgym.Listener.onSubmitListener;
import com.a.n.smartgym.R;

public class NumberPickerDialog extends DialogFragment{
    Button mSubmit, mClose;
    NumberPicker numberPickerSets,numberPickerReps,numberPickerWeight;
    public onSubmitListener mListener;




    public void setOnSubmitListener(onSubmitListener listener){
        this.mListener = listener;

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.custom_number_picker);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        numberPickerSets = (NumberPicker) dialog.findViewById(R.id.np_sets);
        numberPickerReps = (NumberPicker) dialog.findViewById(R.id.np_reps);
        numberPickerWeight = (NumberPicker) dialog.findViewById(R.id.np_weight);
        mSubmit = (Button) dialog.findViewById(R.id.btn_submit);
        mClose = (Button) dialog.findViewById(R.id.btn_close);

        numberPickerSets.setMaxValue(100);
        numberPickerSets.setMinValue(1);

        numberPickerReps.setMaxValue(100);
        numberPickerReps.setMinValue(1);

        numberPickerWeight.setMaxValue(100);
        numberPickerWeight.setMinValue(1);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sharedPreferences != null){
            int reps = sharedPreferences.getInt(getString(R.string.key_reps), Integer.parseInt(getString(R.string.default_reps)));
            int sets = sharedPreferences.getInt(getString(R.string.key_sets), Integer.parseInt(getString(R.string.default_sets)));
            int seconds = sharedPreferences.getInt(getString(R.string.key_seconds), Integer.parseInt(getString(R.string.default_seconds)));
            int weight = sharedPreferences.getInt(getString(R.string.key_weight), Integer.parseInt(getString(R.string.default_weight)));

            numberPickerSets.setValue(sets);
            numberPickerReps.setValue(reps);
            numberPickerWeight.setValue(weight);
        }

        mSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.setOnSubmitListener(numberPickerSets.getValue(),numberPickerReps.getValue(),numberPickerWeight.getValue());
                dismiss();
            }
        });

        mClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.setOnDismiss();
                dismiss();
            }
        });
        return dialog;
    }

}
