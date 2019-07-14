package com.example.beacon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.pwittchen.reactivebeacons.library.rx2.ReactiveBeacons;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Eu adicionei o MAC dos MEUS beacons aqui. Rodrigo, caso você vá testar
 * não esqueça de modificar pra os seus beacons;
 * */
public class MainActivity extends AppCompatActivity {
    private String TAG = "HelpMe";
    private String TAGERROR = "ErrorHelp";
    private ArrayList<String> knownBeacons = new ArrayList<>();
    private ReactiveBeacons reactiveBeacons;
    private Disposable subscription;
    private ArrayList<BeaconHelper> meanRssi = new ArrayList<>();
    private int count = 0;
    private double aux = 0;
    private TextView meanValue, realTimeValue;

    public FragmentManager fragmentManager = getSupportFragmentManager();
    public FragmentTransaction fragmentTransaction;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //meanValue = findViewById(R.id.meanDistValueTextView);
        //realTimeValue = findViewById(R.id.distValueTextView);

        this.callSmartWatch();
        this.callBeaconFunction();

        fragmentTransaction =  fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_content, new InitialFragment());
        fragmentTransaction.commit();

    }

    private void callSmartWatch(){
    }


    private void callBeaconFunction(){
        //Beacon conhecido
        this.knownBeacons.add("0C:F3:EE:54:2F:C6");
        BeaconHelper bh = new BeaconHelper();
        bh.setBeaconName("0C:F3:EE:54:2F:C6");

        this.meanRssi.add(0, bh);

        reactiveBeacons = new ReactiveBeacons(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        //Caso não suporte BLE
        if (!reactiveBeacons.isBleSupported()) {
            Log.d(TAG, "Não suportado");
            return;
        }

        // Checa se tem as permissões necessárias
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    //Lida com os observables quando não estão mais ativos
    @Override protected void onPause() {
        super.onPause();
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    //Lida com os observables quando são destruídos
    @Override protected void onDestroy(){
        super.onDestroy();
        subscription.dispose();
    }

    @Override
    public void onBackPressed() {
    }
}