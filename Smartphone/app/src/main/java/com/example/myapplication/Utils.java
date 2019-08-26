package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class Utils {
    public void navigateToFragment(MainActivity mainActivity, int idFragment, Fragment destinationFragment, boolean hasBackStack){
        if(mainActivity.getSupportFragmentManager() != null){
            FragmentTransaction fragmentTransaction = mainActivity.getSupportFragmentManager().beginTransaction();
            if(hasBackStack)
                fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(idFragment, destinationFragment).commit();
        }
    }

    public void navigateToFragmentWithData(MainActivity mainActivity, int idFragment, Fragment destinationFragment, boolean hasBackStack,
                                           String label,ArrayList<TrackedThing> monitoredThings){
        Bundle bundle = new Bundle();
        bundle.putSerializable(label, monitoredThings);
        destinationFragment.setArguments(bundle);

        if(mainActivity.getSupportFragmentManager() != null){
            FragmentTransaction fragmentTransaction = mainActivity.getSupportFragmentManager().beginTransaction();
            if(hasBackStack)
                fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(idFragment, destinationFragment).commit();
        }
    }
}
