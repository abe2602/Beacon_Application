package com.example.beacon;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.beacon.dialogs.ConnectSmartwatchDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.patloew.rxwear.RxWear;

import java.util.ArrayList;

import static androidx.core.content.PermissionChecker.checkSelfPermission;


//todo: Colocar o botão de cancelar
public class SmartwatchFragment extends Fragment  implements SmartwatchRecyclerViewAdapter.ListenItemClick{
    private RecyclerView smartwatchRecyclerView;
    private ArrayList<String> connectedDevices = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_smartwatch, container, false);
        showSmartwatch(rootView);

//        Button tempButton = rootView.findViewById(R.id.tempButton);
//        tempButton.setOnClickListener(view -> showSmartwatch());

        return rootView;
    }

    private void setupRecyclerView(View rootView){
        smartwatchRecyclerView = rootView.findViewById(R.id.smartwatch_recycler_view);
        smartwatchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        SmartwatchRecyclerViewAdapter smartwatchAdapter = new SmartwatchRecyclerViewAdapter(connectedDevices, this);
        smartwatchRecyclerView.setAdapter(smartwatchAdapter);
        smartwatchRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private boolean isGooglePlayServicesAvailable() {
        MainActivity activity = (MainActivity)getActivity();
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, activity, 0).show();
            return false;
        }
    }

    private void showSmartwatch(View rootView) {
        MainActivity activity = (MainActivity)getActivity();
        if (isGooglePlayServicesAvailable()) {
            RxWear rxWear = new RxWear(activity);

            //Permissões
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                    Log.d("HelpMe", "Permissões garantidas!");
                }
            }

            //Descobre todos os relógios conectdos
            rxWear.node().getConnectedNodes()
                    .doOnNext(it -> connectedDevices.add(it.getDisplayName()))
                    .doOnError(it -> Log.d("HelpMe", it.toString()))
                    .doOnComplete(() -> setupRecyclerView(rootView))
                    .subscribe();

        }else{
            Log.d("HelpMe", "sem play service");
        }
    }

    @Override
    public void onItemClick(int clickItem) {
        ConnectSmartwatchDialog dialog = new ConnectSmartwatchDialog();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        dialog.show(fm, "goToMainScreen");
    }
}
