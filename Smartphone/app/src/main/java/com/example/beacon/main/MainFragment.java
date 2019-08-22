package com.example.beacon.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.beacon.AddMonitoringFragment;
import com.example.beacon.BeaconSensor;
import com.example.beacon.MainActivity;
import com.example.beacon.R;
import com.example.beacon.Utils;
import com.github.pwittchen.reactivebeacons.library.rx2.ReactiveBeacons;
import com.pacoworks.rxpaper2.RxPaperBook;

import java.util.ArrayList;
import java.util.Collections;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*
 * todo: o que vai acontecer:
 *   vou ter 2 books, 1 que diz quais são os sensores cadastrados e o outro com os objetos que queremos monitorar
 *
 * todo: integrar o monitoramento com os beacons
 * */
public class MainFragment extends Fragment {
    private ArrayList<BeaconSensor> availablesBeacon = new ArrayList<>();
    private String TAG = "HelpMe";
    private String TAGERROR = "ErrorHelp";
    private ArrayList<String> knownBeacons = new ArrayList<>();
    private ReactiveBeacons reactiveBeacons;
    private Disposable subscription;
    private ArrayList<BeaconHelper> meanRssi = new ArrayList<>();
    private int count = 0;
    private double aux = 0;

    //Me ajuda a fazer a média
    class BeaconHelper{
        private String beaconName;

        private ArrayList<Double> getDist() {
            return dist;
        }

        public void setDist(ArrayList<Double> dist) {
            this.dist = dist;
        }

        private ArrayList<Double> dist = new ArrayList<>();

        private String getBeaconName() {
            return beaconName;
        }

        void setBeaconName(String beaconName) {
            this.beaconName = beaconName;
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        TextView addTextView = rootView.findViewById(R.id.addItemTextView);
        addTextView.setOnClickListener(v -> {
            Utils util = new Utils();
            util.navigateToFragment((MainActivity)getActivity(), R.id.fragment_content, new AddMonitoringFragment(), true);
        });

        RxPaperBook.init(getActivity());
        RxPaperBook book = RxPaperBook.with("monitored_things");
        Single<Object> cache = book.read("monitored_things").onErrorReturnItem(Collections.emptyList());
        cache.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(items -> {
                    Log.d("HelpMe", items.toString());
                    setupRecyclerView(rootView, (ArrayList<String>) items);
                })
                .doOnError(error -> Log.d("HelpMe", error.toString()))
                .ignoreElement().onErrorComplete()
                .subscribe();

        BeaconSensor sensor1 = new BeaconSensor("0F:F3:EE:54:2F:C6", "Sensor 1", true);
        BeaconSensor sensor2 = new BeaconSensor("0C:F3:EE:54:0C:FE", "Sensor 2", true);
        availablesBeacon.add(sensor1);
        availablesBeacon.add(sensor2);

        RxPaperBook.init(getActivity());
        RxPaperBook sensorBook = RxPaperBook.with("beacon_sensor");
        sensorBook.write("beacon_sensor", availablesBeacon);
        reactiveBeacons = new ReactiveBeacons(getActivity());

        return rootView;
    }

    private void callBeaconFunction(){
        //Beacon conhecido
        this.knownBeacons.add("0C:F3:EE:54:2F:C6");
        BeaconHelper bh = new BeaconHelper();
        bh.setBeaconName("0C:F3:EE:54:2F:C6");

        this.meanRssi.add(0, bh);

        reactiveBeacons = new ReactiveBeacons(getActivity());
    }

    private void setupRecyclerView(View rootView, ArrayList<String> connectedDevices){
        RecyclerView mainRecyclerView = rootView.findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MainRecyclerViewAdapter mainRecyclerViewAdapter = new MainRecyclerViewAdapter(connectedDevices);
        mainRecyclerView.setAdapter(mainRecyclerViewAdapter);
        mainRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onResume() {
        super.onResume();
        //Caso não suporte BLE
        if (!reactiveBeacons.isBleSupported()) {
            Log.d(TAG, "Não suportado");
            return;
        }

        // Checa se tem as permissões necessárias
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAGERROR, "Não há permissões");
            return;
        }

        //Observable!
        subscription = reactiveBeacons.observe()
                .subscribeOn(Schedulers.computation()) //Faz o trabalho numa thread separada
                .observeOn(AndroidSchedulers.mainThread()) //Observa na thread principal
                .subscribe(beacon-> {

                    /*
                     * txPower = MeasuredPower: RSSI esperado na distancia de 1 metro
                     * RSSI = Received Signal Strength Indicator: Força do sinal do beacon
                     * N: constante do ambiente, varia de 2 - 4
                     * */
                    for (String myBeacon: knownBeacons) {
                        if(beacon.macAddress.address.equals(myBeacon)){
                            int N = 4;
                            double dist = Math.pow(10d, ((double) beacon.txPower - beacon.rssi) / (10 * N));

                            //Tratando interferência
                            if(dist >= 0.5)
                                dist = dist - 0.5;

                            Log.d(TAG, "Distancia: " + (dist));
                            Log.d("myRSSI", Double.toString(beacon.rssi));

                            //Log.d(TAG, "Proxi.: " + beacon.getProximity().maxDistance);
                            double maxValue = 0;
                            for (BeaconHelper bh: meanRssi){
                                if(bh.getBeaconName().equals(myBeacon)){
                                    bh.getDist().add(dist);
                                    aux+= dist;
                                    maxValue = Collections.max(bh.getDist());
                                }
                            }

                            //Faz a média de 5 medições
                            this.count++;

                            if(count == 6) {
                                aux -= maxValue;
                                Log.d(TAG + " teste", Double.toString(aux/(count - 1)));
                                count = 0;
                                aux = 0;
                            }
                        }
                    }

                }, error ->{
                    Log.d(TAGERROR, "Erro no subscribe");
                });
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
