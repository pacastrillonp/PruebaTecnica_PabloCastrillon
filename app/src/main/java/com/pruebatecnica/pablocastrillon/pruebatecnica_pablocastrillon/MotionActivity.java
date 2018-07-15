package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.controller.utils.FragmentManagerActivity;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network.WebService;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.network.WebServiceListener;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils.NotificationListAdapter;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils.SerializationTool;
import com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.model.NotificationBody;

public class MotionActivity extends FragmentManagerActivity implements WebServiceListener, SelectionFragment.ButtonActionClickListener, NotificationListAdapter.ButtonActionClickListener {


    private WebService webService;
    private NotificationBody[] notificationBodies;
    private NotificationBody notificationBody;


    //fragments
    private SelectionFragment selectionFragment;
    private final String selectionFragmentTag = "selectionFragmentTag";

    private LineChartFragment lineChartFragment;
    private final String lineChartFragmentTag = "lineChartFragmentTag";

    private BarplotFragment barplotFragment;
    private final String barplotFragmentTag = "barplotFragmentTag";

    private NotificationFragment notificationFragment;
    private final String notificationFragmentTag = "notificationFragmentTag";

    private String activeFragment;


    private Bundle bundle;

    public static final String LAUNCHER_FILTER = "AddItem";

    private int dataChanged = 0;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_motion);

            try {
                LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                        new IntentFilter(LAUNCHER_FILTER));
            } catch (Exception e) {
                e.printStackTrace();
            }


//            Iniciliazacion del servicio
            startService(new Intent(this, MotionDetectorService.class));


            // Servicio web
//            webService = new WebService(this, this);
            webService = new WebService(this, this);
            notificationBody = new NotificationBody();

            // Inicializacion de fragmentos
            selectionFragment = new SelectionFragment();
            lineChartFragment = new LineChartFragment();
            barplotFragment = new BarplotFragment();
            notificationFragment = new NotificationFragment();
            bundle = new Bundle();

            addFragment(selectionFragment, selectionFragmentTag);
            activeFragment = selectionFragmentTag;

        }

    }


    //regionWebServiceListener


    //Lanza fragmento notificaciones
    @Override
    public void onGetNotifications(NotificationBody[] notificationBodies) {

        this.notificationBodies = notificationBodies;

        bundle.putInt("index", notificationBodies.length);
        bundle.putString("notificationBodies", SerializationTool.serializeToJson(notificationBodies));
        removeFragment(activeFragment);
        notificationFragment.setArguments(bundle);
        addFragment(notificationFragment, notificationFragmentTag);
        activeFragment = notificationFragmentTag;
    }

    //Actualiza el modelo.
    @Override
    public void onGetNotification(NotificationBody notificationBody) {
        this.notificationBody = notificationBody;
    }

    @Override
    public void onPutNotification(NotificationBody notificationBodies) {
        notificationFragment.dataChanged(notificationBodies);
    }

    @Override
    public void onDelNotification() {

    }
    //endregion


    //regionSelectionFragment
    @Override
    public void OnShowNotificationsClick() {
        webService.getNotifications();
    }

    @Override
    public void OnShowBarPlotClick() {
        if (notificationBodies == null) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.it_is_not_possible_to_obtain_the_plot, Toast.LENGTH_SHORT);
            toast.show();
            webService.getNotifications();
        } else if (notificationBodies.length != 0) {
            bundle.putInt("index", notificationBodies.length);
            bundle.putString("notificationBodies", SerializationTool.serializeToJson(notificationBodies));
            removeFragment(activeFragment);
            barplotFragment.setArguments(bundle);
            addFragment(barplotFragment, barplotFragmentTag);
            activeFragment = barplotFragmentTag;
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.it_is_not_possible_to_obtain_the_plot, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    public void OnShowLineChartClick() {
        if (notificationBodies == null) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.it_is_not_possible_to_obtain_the_plot, Toast.LENGTH_SHORT);
            toast.show();
            webService.getNotifications();
        } else if (notificationBodies.length != 0) {
            bundle.putInt("index", notificationBodies.length);
            bundle.putString("notificationBodies", SerializationTool.serializeToJson(notificationBodies));
            removeFragment(activeFragment);
            lineChartFragment.setArguments(bundle);
            addFragment(lineChartFragment, lineChartFragmentTag);
            activeFragment = lineChartFragmentTag;
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.it_is_not_possible_to_obtain_the_plot, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


//    endregion


    @Override
    public void onBackPressed() {

        switch (activeFragment) {
            case selectionFragmentTag:
                super.onBackPressed();
                break;
            case lineChartFragmentTag:
                removeFragment(lineChartFragmentTag);
                addFragment(selectionFragment, selectionFragmentTag);
                activeFragment = selectionFragmentTag;
                break;
            case barplotFragmentTag:
                removeFragment(barplotFragmentTag);
                addFragment(selectionFragment, selectionFragmentTag);
                activeFragment = selectionFragmentTag;
                break;
            case notificationFragmentTag:
                removeFragment(notificationFragmentTag);
                addFragment(selectionFragment, selectionFragmentTag);
                activeFragment = selectionFragmentTag;
                break;

        }

    }


    @Override
    protected void onResume() {
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(LAUNCHER_FILTER));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {


        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onStart() {
//
        super.onStart();
    }

    @Override
    public void OnDeleteChannelClick(int notificationId) {
        webService.delNotification(String.valueOf(notificationId));
    }


    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dataChanged++;
            if (activeFragment.equals(notificationFragmentTag)) {
                if (dataChanged == 2) {
                    notificationFragment.dataChanged(SerializationTool.deserializeFromJson(intent.getStringExtra("notificationBody"), NotificationBody.class));
                    dataChanged = 0;
                }

            }

        }
    };

}
