package com.example.beacon;
import android.annotation.SuppressLint;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Eu adicionei o MAC dos MEUS beacons aqui. Rodrigo, caso você vá testar
 * não esqueça de modificar pra os seus beacons;
 * */
public class MainActivity extends AppCompatActivity {
    public FragmentManager fragmentManager = getSupportFragmentManager();
    public FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils util = new Utils();
        util.navigateToFragment(this, R.id.fragment_content, new InitialFragment(), false);

    }

    @Override
    public void onBackPressed() {
        fragmentManager.popBackStackImmediate();
    }
}