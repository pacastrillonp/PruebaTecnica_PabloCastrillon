package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils.SerializationTool;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model.NotificationBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class LineChartFragment extends Fragment {
    private NotificationBody[] notificationBodies;
    private int[] impactsPerHour;
    private int[] totalPerHour;
    private int hour = 0;

    private LineChart lineChart;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.linechart_fragment, container, false);
        Bundle bundle = getArguments();

        Objects.requireNonNull(getActivity()).setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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


            lineChart = view.findViewById(R.id.ll_linechart_fragment);


            lineChart.setDragEnabled(true);
            lineChart.setScaleEnabled(false);

            LimitLine lowerLimit = new LimitLine(0f, "");
            lowerLimit.setLineWidth(4);
            lowerLimit.enableDashedLine(10f, 10f, 0f);
            lowerLimit.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
            lowerLimit.setTextSize(15);

            // Entradas a graficar
            ArrayList<Entry> yValues = new ArrayList<>();
            for (int index = 0; index < totalPerHour.length; index++) {
                if (totalPerHour[index] != 0) {
                    yValues.add(new Entry(index, totalPerHour[index]));
                }

            }

            LineDataSet lineDataSet1 = new LineDataSet(yValues, getString(R.string.interactions_per_hour));
            lineDataSet1.setFillAlpha(110);
            lineDataSet1.setColor(Color.BLUE);
            lineDataSet1.setLineWidth(3f);
            lineDataSet1.setValueTextSize(10f);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(lineDataSet1);

            LineData lineData = new LineData(dataSets);

            lineChart.setData(lineData);

            String[] values = getResources().getStringArray(R.array.array_rage_hours);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.removeAllLimitLines();
            xAxis.setValueFormatter(new XAxisVAlueFormatter(values));
            xAxis.setGranularity(1f);


        }


        return view;
    }


    public class XAxisVAlueFormatter implements IAxisValueFormatter {

        private String[] xValues;

        public XAxisVAlueFormatter(String[] xValues) {
            this.xValues = xValues;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            return xValues[(int) value];
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


}
