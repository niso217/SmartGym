package com.a.n.smartgym.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager;
import android.widget.ListView;

import com.a.n.smartgym.Adapter.ListViewAdapter;
import com.a.n.smartgym.DBModel.MuscleExercise;
import com.a.n.smartgym.DBRepo.ExerciseRepo;
import com.a.n.smartgym.DBRepo.MuscleExerciseRepo;
import com.a.n.smartgym.Object.DayPlanTable;
import com.a.n.smartgym.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DayExerciseDialogFragment extends DialogFragment {

	private ArrayList<DayPlanTable> productList;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final Dialog dialog = new Dialog(getActivity());

//		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.getWindow().setLayout(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.fragment_dayplan);

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
		dialog.setCanceledOnTouchOutside(true);


		//dialog.show();

		productList = new ArrayList<DayPlanTable>();
		ListView lview = (ListView) dialog.findViewById(R.id.listview);
		ListViewAdapter adapter = new ListViewAdapter(getActivity(), productList);
		lview.setAdapter(adapter);

		populateList(getTag());

		adapter.notifyDataSetChanged();

		if (productList.size()==1) dismiss();

		return dialog;
	}


	private void populateList(String day) {


		ArrayList<DayPlanTable> dayPlanTableArrayList = new ExerciseRepo().getDaySummary(FirebaseAuth.getInstance().getCurrentUser().getUid(),day);

		productList.add(
				new DayPlanTable(
						"NAME",
						"",
						"SETS",
						"REPS",
						"WEIGHT"
				));

				// iterates over String elements of value
				for (DayPlanTable dayPlanTable : dayPlanTableArrayList) {
					// checks for null
					if (dayPlanTable != null) {
						productList.add(
								new DayPlanTable(
                                        dayPlanTable.getExercise_name(),
                                        "",
										dayPlanTable.getNumber_sets(),
										dayPlanTable.getNumber_reps(),
										dayPlanTable.getWeight()
										));

					}
				}


	}
}
