package com.example.myapplication;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.main.MainFragment;
import com.jakewharton.rxbinding3.view.RxView;
import com.pacoworks.rxpaper2.RxPaperBook;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */

public class AddMonitoringFragment extends Fragment{
    private int selectedSensor = -1;
    private ArrayList<String> arrayString = new ArrayList<>();
    private ArrayList<String> monitoredThings = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_monitoring, container, false);
        ImageView backButton = rootView.findViewById(R.id.backArrowImageView2);

        RxView.clicks(backButton).doOnNext(item -> getActivity().getSupportFragmentManager().popBackStack()).subscribe();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            monitoredThings = (ArrayList<String>) bundle.getSerializable("monitored_things");
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RxPaperBook.init(getActivity());
        RxPaperBook book = RxPaperBook.with("available_sensors");

        Disposable x = book.read("available_sensors")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(item -> {
                    arrayString = (ArrayList<String>) item;
                    //Log.d("HelpMe", "addMonitoring:  " + Integer.toString(arrayString.size()));
                    setView(view, arrayString);
                })
                .doOnError(error -> {
                    Log.d("HelpMe", "addMonitoring:  " + error.toString());
                    arrayString.add("Sensor 1");
                    arrayString.add("Sensor 2");

                    book.write("available_sensors", arrayString)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete(() -> {
                                setView(view, arrayString);
                            }).doOnError(error2 -> Log.d("HelpMe", "addMonitoring:  " + error2.toString())).subscribe();
                }).ignoreElement().onErrorComplete()
                .subscribe();
    }

    private void setView(View view, ArrayList<String> arrayString){
        addRadioButtons(arrayString, view);

        Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(click -> {
            EditText editText = view.findViewById(R.id.addThingEditText);

            if(!editText.getText().toString().isEmpty()){
                if(selectedSensor >= 0){
                    monitoredThings.add(arrayString.get(selectedSensor));
                    arrayString.remove(arrayString.get(selectedSensor));

                    RxPaperBook.init(getActivity());
                    RxPaperBook book = RxPaperBook.with("available_sensors");
                    book.write("available_sensors", arrayString).subscribe();
                    ((ViewGroup) view.findViewById(R.id.radiogroup)).removeAllViews();
                    Utils utils = new Utils();
                    utils.navigateToFragmentWithData((MainActivity) getActivity(),R.id.fragment_content, new MainFragment(), false,
                             "available_sensors", arrayString);
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();

                    RxPaperBook.init(getActivity());
                    RxPaperBook monitoredThingsBook = RxPaperBook.with("monitored_things");
                    monitoredThingsBook.write("monitored_things", monitoredThings).subscribe();

                }
                else
                    Log.d("HelpMe", "SELECIONA ALGUÉM, CABAÇO");
            }else{
                Log.d("HelpMe", "ta vazio, mermão");
            }
        });
    }

    private void addRadioButtons(ArrayList<String> arrayString, View rootView) {
        final RadioGroup rg = new RadioGroup(getActivity());
        RadioButton[] rb = new RadioButton[arrayString.size()];

        for (int i = 0; i < arrayString.size(); i++) {
            rb[i] = new RadioButton(getActivity());
            rb[i].setText(arrayString.get(i));
            rb[i].setId(i);
            rg.addView(rb[i]);
        }

        rg.setOnCheckedChangeListener((arg0, arg1) -> {
            selectedSensor = rg.getCheckedRadioButtonId();
        });

        ((ViewGroup) rootView.findViewById(R.id.radiogroup)).addView(rg);
    }

}
