package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class BarPlotFragment extends Fragment {
    private NotificationBody[] notificationBodies;
    private int[] impactsPerHour;
    private int[] totalPerHour;
    private int hour = 0;

    private LineChart lineChart;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
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


            lineChart = view.findViewById(R.id.ll_barplot_fragment);


            lineChart.setDragEnabled(true);
            lineChart.setScaleEnabled(false);

            LimitLine lowerLimit = new LimitLine(0f, "");
            lowerLimit.setLineWidth(4f);
            lowerLimit.enableDashedLine(10f, 10f, 0f);
            lowerLimit.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
            lowerLimit.setTextSize(15f);




            ArrayList<Entry> yValues = new ArrayList<>();
            for (int index = 0; index < totalPerHour.length; index++) {
                yValues.add(new Entry(index, totalPerHour[index]));
            }

            LineDataSet lineDataSet1 = new LineDataSet(yValues, "Data Set 1");
            lineDataSet1.setFillAlpha(110);

            lineDataSet1.setColor(Color.BLUE);
            lineDataSet1.setLineWidth(3f);
            lineDataSet1.setValueTextSize(10f);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(lineDataSet1);

            LineData lineData = new LineData(dataSets);

            lineChart.setData(lineData);

            String[] values = new String[]{
                    "00:00 - 01:00",
                    "01:00 - 02:00",
                    "02:00 - 03:00",
                    "03:00 - 04:00",
                    "04:00 - 05:00",
                    "05:00 - 06:00",
                    "06:00 - 07:00",
                    "07:00 - 08:00",
                    "08:00 - 09:00",
                    "09:00 - 10:00",
                    "10:00 - 11:00",
                    "11:00 - 12:00",
                    "12:00 - 13:00",
                    "13:00 - 14:00",
                    "14:00 - 15:00",
                    "15:00 - 16:00",
                    "16:00 - 17:00",
                    "17:00 - 18:00",
                    "18:00 - 19:00",
                    "19:00 - 20:00",
                    "20:00 - 21:00",
                    "21:00 - 22:00",
                    "22:00 - 23:00",
                    "23:00 - 00:00"};

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
