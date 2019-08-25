package com.example.myapplication;

import android.os.Bundle;
import android.provider.Settings;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageApi;
import com.patloew.rxwear.RxWear;
import com.patloew.rxwear.transformers.MessageEventGetDataMap;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private RxWear rxWear;
    private String aux;
    private double beacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();

        //Recupe o nome do device
        String x = Settings.Secure.getString(getContentResolver(), "bluetooth_name");

        Log.d("HelpMe", x);

        rxWear = new RxWear(this);
        rxWear.message().listen("/oba", MessageApi.FILTER_LITERAL)
                .compose(MessageEventGetDataMap.noFilter())
                .doOnNext(item ->{
                    Log.d("HelpMe", "buga buga");
                })
                .subscribe(bom -> {
                    Log.d("HelpMe", bom.toString());
                }, ruim ->{
                    Log.d("HelpMe", ruim.toString());
                });

        Log.d("HelpMe", "uga ugaa");
    }
}
