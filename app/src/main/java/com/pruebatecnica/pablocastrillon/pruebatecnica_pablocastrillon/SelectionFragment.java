package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SelectionFragment extends Fragment implements View.OnClickListener {
    private ButtonActionClickListener buttonActionClickListener;

    private Button showNotifications;
    private Button showBarPlot;
    private Button showLineChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.selection_fragment, container, false);

        showNotifications = view.findViewById(R.id.bt_show_notification);
        showNotifications.setOnClickListener(this);
        showBarPlot = view.findViewById(R.id.bt_show_barplot);
        showBarPlot.setOnClickListener(this);
        showLineChart = view.findViewById(R.id.bt_show_linechart);
        showLineChart.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.bt_show_notification):
                buttonActionClickListener.OnShowNotificationsClick();
                break;
            case (R.id.bt_show_barplot):
                buttonActionClickListener.OnShowBarPlotClick();
                break;
            case (R.id.bt_show_linechart):
                buttonActionClickListener.OnShowLineChartClick();
                break;

        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        buttonActionClickListener = (ButtonActionClickListener) context;
    }

    public interface ButtonActionClickListener {

        void OnShowNotificationsClick();

        void OnShowBarPlotClick();

        void OnShowLineChartClick();

    }

}
