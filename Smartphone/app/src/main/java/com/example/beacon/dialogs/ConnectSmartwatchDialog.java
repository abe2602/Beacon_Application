package com.example.beacon.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.beacon.MainActivity;
import com.example.beacon.MainFragment;
import com.example.beacon.R;

public class ConnectSmartwatchDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.custom_connect_smartwatch_dialog, container, false);

        Button yesButton = rootView.findViewById(R.id.buttonYes);
        yesButton.setOnClickListener(click -> {
            navigateToMainScreen();
            this.dismiss();
        });

        Button noButton = rootView.findViewById(R.id.buttonNo);
        noButton.setOnClickListener(click -> this.dismiss());

        return rootView;
    }

    private void navigateToMainScreen(){
        MainActivity x = (MainActivity)getActivity();

        if(x.getSupportFragmentManager() != null){
            FragmentTransaction fragmentTransaction = x.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.fragment_content, new MainFragment()).commit();
        }
    }
}
