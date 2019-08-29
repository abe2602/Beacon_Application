package com.example.myapplication;

import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageApi;
import com.patloew.rxwear.RxWear;
import com.patloew.rxwear.transformers.MessageEventGetDataMap;

public class MainActivity extends WearableActivity {
    private RxWear rxWear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();

        //Recupe o nome do device
        String deviceName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");
        TextView mainText = findViewById(R.id.mainText);

        rxWear = new RxWear(this);
        rxWear.message().listen("/" + deviceName, MessageApi.FILTER_LITERAL)
                .compose(MessageEventGetDataMap.noFilter())
                .doOnNext(item ->{
                    mainText.setText("Notificação!");
                })
                .subscribe(bom -> {
                    Log.d("HelpMe", bom.toString());
                }, ruim ->{
                    Log.d("HelpMe", ruim.toString());
                });

        Log.d("HelpMe", "uga ugaa");
    }
}
