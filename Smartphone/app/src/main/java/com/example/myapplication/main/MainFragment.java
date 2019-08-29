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

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AddMonitoringFragment;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Settings;
import com.example.myapplication.SettingsFragment;
import com.example.myapplication.TrackedThing;
import com.example.myapplication.Utils;
import com.github.pwittchen.reactivebeacons.library.rx2.Beacon;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*
 *   todo: ajustar o que envia para o relógio
 *    todo: ajustar layout do relógio
 * */

public class MainFragment extends Fragment{
    private CompositeDisposable subscription = new CompositeDisposable();
    private ReactiveBeacons reactiveBeacons;
    private RxWear rxWear;
    private ArrayList<TrackedThing> monitoredThings = new ArrayList<>();
    private String smartWatch = " ";
    private ArrayList<Double> distanceListSensor1 = new ArrayList<>();
    private ArrayList<Double> distanceListSensor2 = new ArrayList<>();
    private View view = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        view = rootView;
        view.setKeepScreenOn(true);

        rxWear = new RxWear(getActivity());
        reactiveBeacons = new ReactiveBeacons(getActivity());
        RxPaperBook.init(getActivity());

        Bundle bundle = this.getArguments();
        if(bundle != null){
            smartWatch = (String) bundle.getSerializable("chosenOne");
        }

        ImageView backButton = view.findViewById(R.id.backArrowImageView);
        backButton.setOnClickListener(backButtonClick -> Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate());

        ImageView settingsButton = view.findViewById(R.id.settingsImageView);
        settingsButton.setOnClickListener(settingsClick -> {
            Utils util = new Utils();
            util.navigateToFragment((MainActivity) Objects.requireNonNull(getActivity()), R.id.fragment_content, new SettingsFragment(), true);
        });

        if(hasBleSupport()){
            TextView addTextView = view.findViewById(R.id.addItemTextView );
            addTextView.setOnClickListener(v -> {
                Utils util = new Utils();
                util.navigateToFragmentWithData((MainActivity) Objects.requireNonNull(getActivity()), R.id.fragment_content, new AddMonitoringFragment(), true,
                        "monitored_things", monitoredThings);
            });

            findBeacons();

        }else{
            Log.d("HelpMe", "Não suporta BLE");
        }

        return rootView;
    }

    void findBeacons(){
        RxPaperBook settingsBook = RxPaperBook.with("settings");
        Single<Object> savedSettings = settingsBook.read("settings").onErrorReturnItem(new Settings(1, true));

        Disposable listItemsDisposable =  savedSettings.flatMapObservable(settings -> {
                    Settings mySettings = (Settings) settings;

                    RxPaperBook book = RxPaperBook.with("monitored_things");
                    Single<Object> cache = book.read("monitored_things").onErrorReturnItem(Collections.emptyList());

                    return cache.subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSuccess(listItems -> {
                                monitoredThings = (ArrayList<TrackedThing>) listItems;
                                setupRecyclerView(this, (ArrayList<TrackedThing>) listItems);
                                this.labelToMac(monitoredThings);
                            })
                            .flatMapObservable(item ->
                                    reactiveBeacons.observe()
                                            .subscribeOn(Schedulers.io()) //Faz o trabalho numa thread separada
                                            .observeOn(AndroidSchedulers.mainThread()) //Observa na thread principal
                                            .flatMap(beaconData -> {
                                                for(TrackedThing auxThing: monitoredThings){
                                                    if(beaconData.macAddress.address.equals(auxThing.getBeaconMac())){
                                                        double distance = rssiToMeters(beaconData);

                                                        if(!mySettings.getHasNotification()){
                                                            distance = -1;
                                                        }

                                                        if(distance> (mySettings.getRange() + 1) && distance != -1 && auxThing.isAvailable()){
                                                            //Colocar toda a lógica que queremos fazer aqui dentro
                                                            return rxWear.message().sendDataMapToAllRemoteNodes("/" + this.smartWatch)
                                                                    .putBoolean("notification", true)
                                                                    .toObservable()
                                                                    .doOnComplete(() -> Log.d("HelpMe", "enviei ;)"));
                                                        }
                                                    }
                                                }
                                                return Observable.just(false);
                                            })
                            );
                }
        ).doOnError(error -> Log.d("HelpMe", error.toString())).ignoreElements().onErrorComplete().subscribe();


        subscription.add(listItemsDisposable);
    }

    private double rssiToMeters(Beacon beacon){
        double aux =  (beacon.txPower - beacon.rssi)/40.0;
        double dist = Math.pow(10, aux);

        if(beacon.macAddress.address.equals("0C:F3:EE:54:2F:C6")){
            if(distanceListSensor1.size() > 2 ){
                dist = Collections.min(distanceListSensor1);
                dist = dist / 2;
                distanceListSensor1.clear();
                return dist;
            }else{
                distanceListSensor1.add(dist);
            }
        }else if(beacon.macAddress.address.equals("0C:F3:EE:54:0C:FE")){
            if(distanceListSensor2.size() > 2 ){
                dist = Collections.min(distanceListSensor2);
                dist = dist / 2;
                distanceListSensor2.clear();
                return dist;
            }else{
                distanceListSensor2.add(dist);
            }
        }

        return -1;
    }

    //Popula a reyclerview
    private void setupRecyclerView(MainFragment mainFragment, ArrayList<TrackedThing> connectedDevices){
        RecyclerView mainRecyclerView = mainFragment.view.findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MainRecyclerViewAdapter mainRecyclerViewAdapter = new MainRecyclerViewAdapter(mainFragment, connectedDevices);
        mainRecyclerView.setAdapter(mainRecyclerViewAdapter);
    }

    //Check se há suporte ao BLE
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

    //Função necessária pois todas as informações são locais, não tem serviço
    private void labelToMac(ArrayList<TrackedThing> trackedThings){
        for(int i = 0; i < trackedThings.size(); i++){
            if("Sensor 1".equals(trackedThings.get(i).getSensor())){
                trackedThings.get(i).setBeaconMac("0C:F3:EE:54:2F:C6");
            }else if("Sensor 2".equals(trackedThings.get(i).getSensor())){
                trackedThings.get(i).setBeaconMac("0C:F3:EE:54:0C:FE");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscription.dispose();
    }
}

