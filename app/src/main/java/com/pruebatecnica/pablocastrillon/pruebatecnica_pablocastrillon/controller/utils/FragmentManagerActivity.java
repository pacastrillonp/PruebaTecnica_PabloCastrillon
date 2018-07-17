package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.controller.utils;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class FragmentManagerActivity extends AppCompatActivity {
    public void addFragment(android.support.v4.app.Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment previousFragment = fragmentManager.findFragmentByTag(tag);

        if (previousFragment == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content, fragment, tag);
            fragmentTransaction.commitAllowingStateLoss();
        } else {

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.commitAllowingStateLoss();
            fragmentTransaction.show(previousFragment);
        }
    }


    public void addFragment(android.support.v4.app.Fragment fragment, String tag, int id) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment previousFragment = fragmentManager.findFragmentByTag(tag);

        if (previousFragment == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(id, fragment, tag);
            fragmentTransaction.commitAllowingStateLoss();
        } else {

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.commitAllowingStateLoss();
            fragmentTransaction.show(previousFragment);

        }
    }

    public void removeFragment(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fragmentManager.findFragmentByTag(tag);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();
    }


    public void hideFragment(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fragmentManager.findFragmentByTag(tag);

        if (fragment != null && !fragment.isHidden()) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(fragment);
            transaction.commitAllowingStateLoss();
        }
    }

    public void showFragment(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fragmentManager.findFragmentByTag(tag);

        if (fragment != null && !fragment.isHidden()) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.show(fragment);
            transaction.commitAllowingStateLoss();
        }
    }
}
