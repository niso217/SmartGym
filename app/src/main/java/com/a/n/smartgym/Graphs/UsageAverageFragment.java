package com.a.n.smartgym.Graphs;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a.n.smartgym.Object.MachineUsage;
import com.a.n.smartgym.R;
import com.a.n.smartgym.DBRepo.ExerciseRepo;
import com.a.n.smartgym.Utils.Constants;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
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


        mChart.setTransparentCircleColor(ContextCompat.getColor(getContext(), R.color.background));
        mChart.setTransparentCircleAlpha(255); // fully opaque
        mChart.setHoleColor(ContextCompat.getColor(getContext(), R.color.background));

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener

        setData();

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);


        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextColor(ContextCompat.getColor(getContext(), R.color.system_green));
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setTextSize(13);

        // entry label styling
        mChart.setEntryLabelColor(ContextCompat.getColor(getContext(), R.color.flame));
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

        ArrayList<MachineUsage> machineUsages = new ExerciseRepo().getUsage2("");

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < machineUsages.size(); i++) {
            entries.add(new PieEntry((float) machineUsages.get(i).getCounter(),  machineUsages.get(i).getMuscle().toUpperCase()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : Constants.PIE_COLORs)
            colors.add(c);

//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);

//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);

        //colors.add(ColorTemplate.getHoloBlue());

//        colors.add(Color.rgb(103, 110, 129));
//        colors.add(Color.rgb(121, 162, 175));

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);


        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLineColor(ContextCompat.getColor(getContext(), R.color.system_green));
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.6f);
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.system_green));
       // data.setValueTypeface(tf);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

//        Paint p1 = mChart.getPaint(Chart.PAINT_HOLE);
//        p1.setColor(ContextCompat.getColor(getContext(), R.color.background));

        mChart.invalidate();
    }



    private void ExactDataFromDB() {



    }


}
