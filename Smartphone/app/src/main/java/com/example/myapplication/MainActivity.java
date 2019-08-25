package com.example.myapplication;

import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.patloew.rxwear.RxWear;

public class MainActivity extends AppCompatActivity {
    public FragmentManager fragmentManager = getSupportFragmentManager();
    public FragmentTransaction fragmentTransaction;
    static RxWear rxWear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rxWear = new RxWear(this);

        Utils util = new Utils();
        util.navigateToFragment(this, R.id.fragment_content, new InitialFragment(), false);

    }

    @Override
    public void onBackPressed() {
        fragmentManager.popBackStackImmediate();
    }

}