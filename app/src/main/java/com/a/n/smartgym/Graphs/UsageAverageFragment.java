package com.a.n.smartgym.Graphs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a.n.smartgym.Object.MachineUsage;
import com.a.n.smartgym.R;
import com.a.n.smartgym.DBRepo.ExerciseRepo;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by DAT on 9/1/2015.
 */
public class UsageAverageFragment extends Fragment {

    private LinkedHashMap<String, LinkedHashMap<String, Double>> dicCodeToIndex;
    private List<List<String>> labels = new ArrayList<>();
    private PieChart mChart;
    private final static int MAX_EXERCISE = 4;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ExactDataFromDB();

        View view = inflater.inflate(R.layout.activity_piechart, container, false);
        mChart = (PieChart) view.findViewById(R.id.chart1);

        SetupPieChart();

        return view;
    }

    private void SetupPieChart() {
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);


        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener

        setData();

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);


        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTextSize(12f);
    }

    private SpannableString generateCenterSpannableText(int size) {

        SpannableString s = new SpannableString("Top " +size +  " Exercises");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 15, 0);
//        s.setSpan(new StyleSpan(Typeface.NORMAL), 15, s.length() - 15, 0);
//        s.setSpan(new ForegroundColorSpan(Color.GRAY), 15, s.length() - 15, 0);
//        s.setSpan(new RelativeSizeSpan(.8f), 15, s.length() - 15, 0);
//        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 15, s.length(), 0);
        return s;
    }



    private void setData() {

        int size = 0;
        long other = 0;
        ArrayList<MachineUsage> machineUsages = new ExerciseRepo().getUsage(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //if (machineUsages.size()<5) return;


        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        if (MAX_EXERCISE < machineUsages.size()){
            for (int i = MAX_EXERCISE; i < machineUsages.size(); i++) {
                other += machineUsages.get(i).getPresent();
            }
            size = MAX_EXERCISE;
            entries.add(new PieEntry(other, "Others"));
        }
        else{
            size = machineUsages.size();
        }

        for (int i = 0; i < size ; i++) {
            entries.add(new PieEntry(machineUsages.get(i).getPresent(),
                            machineUsages.get(i).getMachine_name()));
        }




        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);
        mChart.setCenterText(generateCenterSpannableText(size));


        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }



    private void ExactDataFromDB() {



    }


}
