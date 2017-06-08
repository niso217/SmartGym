package com.a.n.smartgym;

/**
 * Created by nirb on 17/05/2017.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetJavaScriptEnabled")
public class ShowWebChartActivity extends ActionBarActivity {

    WebView webView;
    int num1, num2, num3, num4, num5;

    Spinner spCharts;
    List<String> listCharts;
    List<String> listHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webchart);

        Intent intent = getIntent();
        num1 = intent.getIntExtra("NUM1", 0);
        num2 = intent.getIntExtra("NUM2", 0);
        num3 = intent.getIntExtra("NUM3", 0);
        num4 = intent.getIntExtra("NUM4", 0);
        num5 = intent.getIntExtra("NUM5", 0);

        spCharts = (Spinner) findViewById(R.id.spcharts);

        listCharts = new ArrayList<String>();
        listCharts.add("Pie Chart");
        listCharts.add("Pie Chart 3D");
        listCharts.add("Scatter Chart");
        listCharts.add("Column Chart");
        listCharts.add("Bar Chart");
        listCharts.add("Histogram");
        listCharts.add("Line Chart");
        listCharts.add("Area Chart");
        listCharts.add("Chart.js");

        listHtml = new ArrayList<String>();
        listHtml.add("file:///android_asset/pie_chart.html");
        listHtml.add("file:///android_asset/pie_chart_3d.html");
        listHtml.add("file:///android_asset/scatter_chart.html");
        listHtml.add("file:///android_asset/column_chart.html");
        listHtml.add("file:///android_asset/bar_chart.html");
        listHtml.add("file:///android_asset/histogram.html");
        listHtml.add("file:///android_asset/line_chart.html");
        listHtml.add("file:///android_asset/area_chart.html");
        listHtml.add("file:///android_asset/chartjs.html");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listCharts);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCharts.setAdapter(dataAdapter);
        spCharts.setOnItemSelectedListener(new OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String chartHtml = listHtml.get(parent.getSelectedItemPosition());
                webView.loadUrl(chartHtml);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }});

        webView = (WebView)findViewById(R.id.web);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);

    }

    public class WebAppInterface {

        @JavascriptInterface
        public int getNum1() {
            return num1;
        }

        @JavascriptInterface
        public int getNum2() {
            return num2;
        }

        @JavascriptInterface
        public int getNum3() {
            return num3;
        }

        @JavascriptInterface
        public int getNum4() {
            return num4;
        }

        @JavascriptInterface
        public int getNum5() {
            return num5;
        }
    }

}
