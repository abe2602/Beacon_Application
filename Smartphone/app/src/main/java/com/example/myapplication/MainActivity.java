package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.github.pwittchen.reactivebeacons.library.rx2.ReactiveBeacons;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.patloew.rxwear.RxWear;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private CompositeDisposable subscription = new CompositeDisposable();
    private Observable<Boolean> validator;
    private ReactiveBeacons reactiveBeacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendDataToSW();

    }

    @SuppressLint("CheckResult")
    private void sendDataToSW() {
        if (isGooglePlayServicesAvailable()) {
            RxWear rxWear = new RxWear(this);
            reactiveBeacons = new ReactiveBeacons(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                    Log.d("HelpMe", "Permissões garantidas!");
                }
            }
            Disposable wearRX = reactiveBeacons.observe()
                    .subscribeOn(Schedulers.computation()) //Faz o trabalho numa thread separada
                    .observeOn(AndroidSchedulers.mainThread()) //Observa na thread principal
                    .flatMap(beaconData -> {
                        if(beaconData.macAddress.address.equals("0C:F3:EE:54:2F:C6")){
                            //Colocar toda a lógica que queremos fazer aqui dentro
                            return rxWear.message().sendDataMapToAllRemoteNodes("/message")
                                    .putString("title", "Oi ;)")
                                    .putString("message", "thau ;)")
                                    .putDouble("beacon", beaconData.getDistance())
                                    .toObservable();
                        }else{
                            return Observable.just(false); //caso não nos interesse, não faz nada
                        }
                    })
            .subscribe();

            subscription.add(wearRX);
        }else{
            Log.d("HelpMe", "sem play service");
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
}
