package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BarPlotFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.barplot_fragment, container, false);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
