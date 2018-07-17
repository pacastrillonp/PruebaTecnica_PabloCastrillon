package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
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

    //maquina de estados de botones
    private boolean barcharClicked = false;
    private boolean linecharClicked = false;
    private boolean notificationClicked = false;

    private Bundle bundle;

    public static final String LAUNCHER_FILTER = "AddItem";



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
        if (notificationBodies.length != 0) {
            if (barcharClicked) {
                bundle.putInt("index", notificationBodies.length);
                bundle.putString("notificationBodies", SerializationTool.serializeToJson(notificationBodies));
                removeFragment(activeFragment);
                barplotFragment.setArguments(bundle);
                addFragment(barplotFragment, barplotFragmentTag);
                activeFragment = barplotFragmentTag;
                barcharClicked = false;
            } else if (linecharClicked) {
                bundle.putInt("index", notificationBodies.length);
                bundle.putString("notificationBodies", SerializationTool.serializeToJson(notificationBodies));
                removeFragment(activeFragment);
                lineChartFragment.setArguments(bundle);
                addFragment(lineChartFragment, lineChartFragmentTag);
                activeFragment = lineChartFragmentTag;
                linecharClicked = false;
            } else if (notificationClicked) {
                removeFragment(activeFragment);
                notificationFragment.setArguments(bundle);
                addFragment(notificationFragment, notificationFragmentTag);
                activeFragment = notificationFragmentTag;
                notificationClicked = false;            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.the_action_is_not_possible, Toast.LENGTH_SHORT);
            toast.show();
        }
    }






//endregion


    //regionSelectionFragment
    @Override
    public void OnShowNotificationsClick() {
        notificationClicked = true;
        webService.getNotifications();
    }

    @Override
    public void OnShowBarPlotClick() {
        barcharClicked = true;
        webService.getNotifications();

    }

    @Override
    public void OnShowLineChartClick() {
        linecharClicked = true;
        webService.getNotifications();

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
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                addFragment(selectionFragment, selectionFragmentTag);
                activeFragment = selectionFragmentTag;
                break;
            case barplotFragmentTag:
                removeFragment(barplotFragmentTag);
                addFragment(selectionFragment, selectionFragmentTag);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                activeFragment = selectionFragmentTag;
                break;
            case notificationFragmentTag:
                removeFragment(notificationFragmentTag);
                addFragment(selectionFragment, selectionFragmentTag);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                activeFragment = selectionFragmentTag;
                break;

        }

    }


    @Override
    public void OnDeleteChannelClick(int notificationId) {
        webService.delNotification(String.valueOf(notificationId));
    }


    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notificationFragment.dataChanged(SerializationTool.deserializeFromJson(intent.getStringExtra("notificationBody"), NotificationBody.class));

        }
    };

}
