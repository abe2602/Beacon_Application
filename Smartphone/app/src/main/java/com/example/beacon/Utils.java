package com.example.beacon;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.beacon.MainActivity;
import com.example.beacon.main.MainFragment;

public class Utils {
    public void navigateToFragment(MainActivity mainActivity, int idFragment, Fragment destinationFragment, boolean hasBackStack){
        if(mainActivity.getSupportFragmentManager() != null){
            FragmentTransaction fragmentTransaction = mainActivity.getSupportFragmentManager().beginTransaction();
            if(hasBackStack)
                fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(idFragment, destinationFragment).commit();
        }
    }
}
