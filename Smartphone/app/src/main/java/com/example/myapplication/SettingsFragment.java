package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import androidx.fragment.app.Fragment;
import com.pacoworks.rxpaper2.RxPaperBook;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        RxPaperBook.init(getActivity());
        RxPaperBook book = RxPaperBook.with("settings");
        SeekBar seekBar = rootView.findViewById(R.id.seekBar);
        Switch notificationSwitch = rootView.findViewById(R.id.notificationSwitch);
        ImageView backButton = rootView.findViewById(R.id.backArrowImageView);

        Single<Object> savedSettings = book.read("settings").onErrorReturnItem(new Settings(1, true));
        savedSettings
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(savedSetting -> {
                    Settings newSettings = (Settings) savedSetting;
                    seekBar.setProgress(newSettings.getRange());
                    notificationSwitch.setChecked(newSettings.getHasNotification());
                })
                .doOnError(error -> Log.d("HelpMe", error.toString()))
                .subscribe();


        backButton.setOnClickListener(v ->{
            Settings newSavedSettings = new Settings(seekBar.getProgress(), notificationSwitch.isChecked());
            book.write("settings", newSavedSettings).subscribe();
            getActivity().getSupportFragmentManager().popBackStack();
        });

        return rootView;
    }

}
