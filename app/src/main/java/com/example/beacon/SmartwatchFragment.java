package com.example.beacon;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class SmartwatchFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_smartwatch, container, false);

        Button tempButton = rootView.findViewById(R.id.tempButton);
        tempButton.setOnClickListener(view -> navigateToSmartwatchScreen());

        return rootView;
    }

    private void navigateToSmartwatchScreen(){
        MainActivity x = (MainActivity)getActivity();

        if(x.getSupportFragmentManager() != null){
            FragmentTransaction fragmentTransaction = x.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_content, new MainFragment()).commit();
        }
    }
}
