package com.example.myapplication.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AddMonitoringFragment;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Settings;
import com.example.myapplication.SettingsFragment;
import com.example.myapplication.Utils;
import com.github.pwittchen.reactivebeacons.library.rx2.ReactiveBeacons;
import com.pacoworks.rxpaper2.RxPaperBook;
import com.patloew.rxwear.RxWear;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class MainFragment extends Fragment{
    private CompositeDisposable subscription = new CompositeDisposable();
    private ReactiveBeacons reactiveBeacons;
    private RxWear rxWear;
    private ArrayList<String> monitoredThings = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        rxWear = new RxWear(getActivity());
        reactiveBeacons = new ReactiveBeacons(getActivity());
        RxPaperBook.init(getActivity());
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setKeepScreenOn(true);

        ImageView backButton = view.findViewById(R.id.backArrowImageView);
        backButton.setOnClickListener(backButtonClick -> Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate());

        ImageView settingsButton = view.findViewById(R.id.settingsImageView);
        settingsButton.setOnClickListener(settingsClick -> {
            Utils util = new Utils();
            util.navigateToFragment((MainActivity) Objects.requireNonNull(getActivity()), R.id.fragment_content, new SettingsFragment(), true);
        });

        if(hasBleSupport()){
            TextView addTextView = view.findViewById(R.id.addItemTextView);
            addTextView.setOnClickListener(v -> {
                Utils util = new Utils();
                util.navigateToFragmentWithData((MainActivity) Objects.requireNonNull(getActivity()), R.id.fragment_content, new AddMonitoringFragment(), true,
                        "monitored_things", monitoredThings);
            });

            RxPaperBook settingsBook = RxPaperBook.with("settings");
            Single<Object> savedSettings = settingsBook.read("settings").onErrorReturnItem(new Settings(1, true));
            savedSettings.flatMapObservable( settings -> {
                        Settings mySettings = (Settings) settings;

                        RxPaperBook book = RxPaperBook.with("monitored_things");
                        Single<Object> cache = book.read("monitored_things").onErrorReturnItem(Collections.emptyList());

                        return cache.subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSuccess(listItems -> {
                                    monitoredThings = (ArrayList<String>) listItems;
                                    setupRecyclerView(view, (ArrayList<String>) listItems);
                                })
                                .flatMapObservable(item ->
                                        reactiveBeacons.observe()
                                                .subscribeOn(Schedulers.io()) //Faz o trabalho numa thread separada
                                                .observeOn(AndroidSchedulers.mainThread()) //Observa na thread principal
                                                .flatMap(beaconData -> {
                                                    double distance;

                                                    if(!mySettings.getHasNotification()){
                                                        distance = 0;
                                                    }else {
                                                        distance = beaconData.getDistance();
                                                    }
                                                    if(beaconData.macAddress.address.equals("0C:F3:EE:54:2F:C6")){
                                                        Log.d("HelpMe", Double.toString(distance));
                                                        if(distance> mySettings.getRange()){
                                                            //Colocar toda a lógica que queremos fazer aqui dentro
                                                            return rxWear.message().sendDataMapToAllRemoteNodes("/message")
                                                                    .putBoolean("notification", true)
                                                                    .toObservable()
                                                                    .doOnComplete(() -> Log.d("HelpMe", "enviei ;)"));
                                                        }else{
                                                            return Observable.just(false);
                                                        }
                                                    }else{
                                                        return Observable.just(false); //caso não nos interesse, não faz nada
                                                    }

                                                })
                                );
                    }
            ).doOnError(error -> {Log.d("HelpMe", error.toString());})
                    .ignoreElements().onErrorComplete()
                    .subscribe();
        }else{
            Log.d("HelpMe", "CAGUEI PRO BEACON");
        }
    }

    private void setupRecyclerView(View rootView, ArrayList<String> connectedDevices){
        RecyclerView mainRecyclerView = rootView.findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MainRecyclerViewAdapter mainRecyclerViewAdapter = new MainRecyclerViewAdapter(getActivity(), connectedDevices);
        mainRecyclerView.setAdapter(mainRecyclerViewAdapter);
    }

    private Boolean hasBleSupport(){
        //Caso não suporte BLE
        if (!reactiveBeacons.isBleSupported()) {
            Log.d("HelpMe", "Não suportado");
            return false;
        }

        // Checa se tem as permissões necessárias
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("HelpMe", "Não há permissões");
            return false;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        subscription.dispose();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscription.dispose();
    }
}
