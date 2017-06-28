package com.a.n.smartgym.multicolumnlistview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.a.n.smartgym.DBModel.MuscleExercise;
import com.a.n.smartgym.DBRepo.MuscleExerciseRepo;
import com.a.n.smartgym.R;

public class MainActivity2 extends DialogFragment {

	private ArrayList<Model> productList;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final Dialog dialog = new Dialog(getActivity());

		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.activity_main3);

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
		dialog.setCanceledOnTouchOutside(true);


		//dialog.show();

		productList = new ArrayList<Model>();
		ListView lview = (ListView) dialog.findViewById(R.id.listview);
		listviewAdapter adapter = new listviewAdapter(getActivity(), productList);
		lview.setAdapter(adapter);

		populateList(getTag());

		adapter.notifyDataSetChanged();

		return dialog;
	}


	private void populateList(String day) {

		Model item;

		Map<String, ArrayList<MuscleExercise>> exercises = new MuscleExerciseRepo().getDayPlan(day);

		for (String key : exercises.keySet()) {

			// gets the value
			List<MuscleExercise> muscleExerciseList = exercises.get(key);
			// checks for null value
			if (muscleExerciseList != null) {
				// iterates over String elements of value
				for (MuscleExercise muscleExercise : muscleExerciseList) {
					// checks for null
					if (muscleExercise != null) {
						productList.add(
								new Model(
										muscleExercise.getExerciseid(),
										key,
										muscleExercise.getNumberofsets(),
										muscleExercise.getNumberofreps(),
										muscleExercise.getWeight()
										));

					}
				}
			}
		}

	}
}
