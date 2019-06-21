package com.example.beacon;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.pwittchen.reactivebeacons.library.rx2.ReactiveBeacons;

import java.util.ArrayList;

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

    //Me ajuda a fazer a média
    class BeaconHelper{
        private String beaconName;

        public ArrayList<Double> getDist() {
            return dist;
        }

        public void setDist(ArrayList<Double> dist) {
            this.dist = dist;
        }

        private ArrayList<Double> dist = new ArrayList<>();

        public String getBeaconName() {
            return beaconName;
        }

        public void setBeaconName(String beaconName) {
            this.beaconName = beaconName;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Beacon conhecido
        this.knownBeacons.add("0C:F3:EE:54:2F:C6");
        BeaconHelper bh = new BeaconHelper();
        bh.setBeaconName("0C:F3:EE:54:2F:C6");

        this.meanRssi.add(0, bh);

        reactiveBeacons = new ReactiveBeacons(this);
    }

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

                            Log.d(TAG, "OBSERVANDO o beacon: " + myBeacon);

                            //Transforma RSSI em Metros (BUGANDOOOOOOOOOOOOOO)
                            double ratio = beacon.rssi*1.0/beacon.txPower;
                            ratio = ratio/(10 * 3.0);
                            double dist = Math.pow(10, ratio);

                            Log.d(TAG, "Distancia: " + (dist));

                            for (BeaconHelper bh: meanRssi){
                                if(bh.getBeaconName().equals(myBeacon)){
                                    bh.getDist().add(dist);
                                }
                            }

                            //Faz a média de 5 medições
                            this.count++;

                            if(count == 5){
                                double auxDist = 0;
                                int sizeList = 0;

                                for(BeaconHelper bh: meanRssi){
                                    if(bh.getBeaconName().equals(myBeacon)){
                                        sizeList = bh.getDist().size();
                                        for (double sum: bh.getDist()){
                                            auxDist += sum;
                                        }
                                    }
                                }
                                count = 0;
                                double finalDist = (auxDist/sizeList);

                                Log.d(TAG, "Distancia Final: " + (finalDist));
                            }
                        }else{
                            Log.d(TAGERROR, "Não eh o meu!");
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
}