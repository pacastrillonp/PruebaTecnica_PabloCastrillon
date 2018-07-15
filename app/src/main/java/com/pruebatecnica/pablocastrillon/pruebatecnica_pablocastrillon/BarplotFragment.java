package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils.SerializationTool;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model.NotificationBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BarplotFragment extends Fragment {
    private BarChart barChart;

    private NotificationBody[] notificationBodies;
    private int[] impactsPerHour;
    private int[] totalPerHour;
    private int hour = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.barplot_fragment, container, false);
        Bundle bundle = getArguments();

        assert bundle != null;

        if (bundle.getInt("index") > 0) {

            notificationBodies = new NotificationBody[bundle.getInt("index")];
            impactsPerHour = new int[bundle.getInt("index")];
            totalPerHour = new int[24];

            notificationBodies = SerializationTool.deserializeFromJson(bundle.getString("notificationBodies"), NotificationBody[].class);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            try {

                for (int i = 0; i < notificationBodies.length; i++) {
                    Date date = formatter.parse(notificationBodies[i].getDate());
                    hour = date.getHours();
                    impactsPerHour[i] = hour;
                }

                for (int i = 0; i < totalPerHour.length; i++) {
                    for (int anImpactsPerHour : impactsPerHour) {
                        if (anImpactsPerHour == i) {
                            totalPerHour[i] = totalPerHour[i] + 1;
                        }
                    }
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        barChart = view.findViewById(R.id.ll_barplot_fragment);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(70);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);





        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int index = 0; index < totalPerHour.length; index++) {
            barEntries.add(new BarEntry(index, totalPerHour[index]));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Data set 1");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.9f);

        barChart.setData(data);
        return view;
    }
}
