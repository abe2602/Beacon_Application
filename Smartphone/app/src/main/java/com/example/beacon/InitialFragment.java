package com.example.beacon;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.pm.PackageManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.patloew.rxwear.RxWear;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class InitialFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_initial, container, false);

        Button chooseButton = rootView.findViewById(R.id.chooseButton);
        chooseButton.setOnClickListener(view -> navigateToSmartwatchScreen());

        return rootView;
    }

    private void navigateToSmartwatchScreen(){
        MainActivity x = (MainActivity)getActivity();

        if(x.getSupportFragmentManager() != null){
            FragmentTransaction fragmentTransaction = x.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_content, new SmartwatchFragment()).commit();
        }
    }
}
