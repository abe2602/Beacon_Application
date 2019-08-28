package com.example.myapplication.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.main.MainFragment;

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
        Utils util = new Utils();
        util.navigateToFragmentWithStringData((MainActivity)getActivity(), R.id.fragment_content, new MainFragment(), true, "chosenOne", this.getTag());
    }
}
