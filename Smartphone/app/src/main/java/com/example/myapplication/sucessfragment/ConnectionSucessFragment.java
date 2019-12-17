package com.example.myapplication.sucessfragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.main.MainFragment;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionSucessFragment extends Fragment {


    public ConnectionSucessFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_conectado, container, false);

        Observable.just(1).debounce(2000, TimeUnit.MILLISECONDS).doOnNext( notUsed -> {
            Bundle bundle = this.getArguments();
            if(bundle != null){
                Utils util = new Utils();
                util.navigateToFragmentWithStringData((MainActivity)getActivity(), R.id.fragment_content, new MainFragment(), true, "chosenOne", (String) bundle.getSerializable("chosenOne"));
            }
        }).subscribe();

        return rootView;
    }

}
