package com.example.myapplication;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageApi;
import com.patloew.rxwear.RxWear;
import com.patloew.rxwear.transformers.MessageEventGetDataMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private RxWear rxWear;
    private String aux;
    private double beacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();

        rxWear = new RxWear(this);
        Disposable wearRX = rxWear.message().listen("/message", MessageApi.FILTER_LITERAL)
                .subscribeOn(Schedulers.computation()) //Faz o trabalho numa thread separada
                .observeOn(AndroidSchedulers.mainThread())
                .compose(MessageEventGetDataMap.noFilter())
                .doOnNext(dataMap -> {
                    Log.d("HelpMe", dataMap.getString("title", aux));
                    Log.d("HelpMe", dataMap.getString("message", aux));
                    Log.d("HelpMe", String.valueOf(dataMap.getDouble("beacon", beacon)));
                })
                .doOnError(it -> Log.d("HelpMe", it.toString()))
                .doOnComplete(() -> Log.d("HelpMe", "deu bom"))
                .ignoreElements().onErrorComplete()
                .subscribe();
    }
}
