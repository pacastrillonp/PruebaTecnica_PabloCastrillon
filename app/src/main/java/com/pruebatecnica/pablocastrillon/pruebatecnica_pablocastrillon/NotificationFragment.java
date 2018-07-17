package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils.NotificationListAdapter;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils.SerializationTool;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model.NotificationBody;

public class NotificationFragment extends Fragment {

    private NotificationBody[] notificationBodies;
    private RecyclerView notificationsRecyclerView;
    private LinearLayoutManager layoutManager;
    private NotificationListAdapter notificationListAdapter;
    private NotificationListAdapter.ButtonActionClickListener buttonActionClickListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_fragment, container, false);
        Bundle bundle = getArguments();
        notificationsRecyclerView = view.findViewById(R.id.rv_notification_list);
        notificationsRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        notificationsRecyclerView.setLayoutManager(layoutManager);
        notificationsRecyclerView.setItemAnimator(new DefaultItemAnimator());


        assert bundle != null;
        notificationBodies = new NotificationBody[bundle.getInt("index")];
        notificationBodies = SerializationTool.deserializeFromJson(bundle.getString("notificationBodies"), NotificationBody[].class);


        notificationListAdapter = new NotificationListAdapter(buttonActionClickListener);
        notificationsRecyclerView.setAdapter(notificationListAdapter);


        loadFirstPage(notificationBodies);

        return view;
    }


    //Carga primeta pagina de canales
    private void loadFirstPage(NotificationBody[] notificationBodies) {
        notificationListAdapter.addAll(notificationBodies);

    }

    // Carga nuevos datos
    private void loadNextPage(NotificationBody notificationBodies) {
        notificationListAdapter.add(notificationBodies);
    }


    // Sirve para notifica que los datos han cambiado y se necesita cargar más
    public void dataChanged(NotificationBody notificationBodies) {

        loadNextPage(notificationBodies);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        buttonActionClickListener = (NotificationListAdapter.ButtonActionClickListener) context;
    }


}
