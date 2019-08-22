package com.example.beacon;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.beacon.R;
import com.example.beacon.smartwatch.SmartwatchRecyclerViewAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
//todo: Problema: como salvar
public class AddMonitoringFragment extends Fragment{
    private int selectedSensor = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_monitoring, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<String> arrayString = new ArrayList<>();
        arrayString.add("UBA");
        arrayString.add("EBA");

        addRadioButtons(arrayString, view);

        Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(click -> {
            EditText editText = view.findViewById(R.id.addThingEditText);

            if(!editText.getText().toString().isEmpty()){
                if(selectedSensor > 0)
                    Log.d("HelpMe", arrayString.get(selectedSensor - 1));
                else
                    Log.d("HelpMe", "SELECIONA ALGUÉM, CABAÇO");
            }else{
                Log.d("HelpMe", "ta vazio, mermão");
            }
        });

    }

    private void addRadioButtons(ArrayList<String> arrayString, View rootView) {
        for (int row = 0; row < 1; row++) {
            RadioGroup ll = new RadioGroup(getActivity());
            ll.setOrientation(LinearLayout.VERTICAL);

            ll.setOnCheckedChangeListener((rg, checkedId) -> {
                for(int i=0; i<rg.getChildCount(); i++) {
                    RadioButton btn = (RadioButton) rg.getChildAt(i);
                    if(btn.getId() == checkedId) {
                        selectedSensor = checkedId;
                        Log.d("HelpMe", Integer.toString(selectedSensor));
                        return;
                    }
                }
            });

            for (int i = 0; i < arrayString.size(); i++) {
                RadioButton rdbtn = new RadioButton(getActivity());
                rdbtn.setId(View.generateViewId());
                rdbtn.setText(arrayString.get(i));
                ll.addView(rdbtn);
            }
            ((ViewGroup) rootView.findViewById(R.id.radiogroup)).addView(ll);
        }
    }
}
