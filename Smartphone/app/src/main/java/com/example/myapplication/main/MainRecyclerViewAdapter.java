package com.example.myapplication.main;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.TrackedThing;
import com.jakewharton.rxbinding3.view.RxView;
import com.pacoworks.rxpaper2.RxPaperBook;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.NumberViewHolder> {
    private ArrayList<TrackedThing> availableThings;
    private ArrayList<TrackedThing> deletedThings;
    private Context context;
    private MainFragment mainFragment;

    /*Construtor da classe, recebe como parâmetro a quantidade de views*/
    public MainRecyclerViewAdapter(MainFragment mainFragment, ArrayList<TrackedThing> availableThings){
        this.availableThings = availableThings;
        this.context = mainFragment.getActivity();
        this.mainFragment = mainFragment;
    }

    @Override
    public MainRecyclerViewAdapter.NumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.main_recyclerview_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediately = false;

        View view = inflater.inflate(layoutId, parent, attachImmediately);
        MainRecyclerViewAdapter.NumberViewHolder viewHolder = new MainRecyclerViewAdapter.NumberViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainRecyclerViewAdapter.NumberViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return availableThings.size();
    }

    class NumberViewHolder extends RecyclerView.ViewHolder{

        Switch switchItem;
        TextView thingName;
        ImageView deleteThing;

        public NumberViewHolder(View itemView) {
            super(itemView);
            thingName = itemView.findViewById(R.id.thingNameTextView);
            deleteThing = itemView.findViewById(R.id.deleteThingImageView);
            switchItem = itemView.findViewById(R.id.switch1);
        }

        public void bind(int listIndex){
            thingName.setText(availableThings.get(listIndex).getName());
            switchItem.setChecked(availableThings.get(listIndex).isAvailable());

            RxPaperBook.init(context);
            RxPaperBook availableSensorsBook = RxPaperBook.with("available_sensors");
            RxPaperBook monitoredThingsBook = RxPaperBook.with("monitored_things");

            /*
             * Clique do botão de adicionar:
             * pega da cache todos os sensores disponíveis e atualiza a lista. Uma vez que
             * a lista de sensores disponíveis é atualizada, a lista de objetos observados é modificada
             * */
            RxView.clicks(deleteThing).flatMapCompletable(s ->
                    availableSensorsBook.read("available_sensors").flatMapCompletable(availableItemsList -> {
                        deletedThings = (ArrayList<TrackedThing>) availableItemsList;
                        deletedThings.add(availableThings.get(listIndex));
                        availableThings.remove(availableThings.get(listIndex));
                        return availableSensorsBook.write("available_sensors", deletedThings)
                                .andThen(monitoredThingsBook.write("monitored_things", availableThings)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnComplete(() ->{
                                            notifyItemRemoved(listIndex);
                                            notifyItemRangeChanged(listIndex, availableThings.size()); }
                                        )
                                );
                    })).doOnError(error -> Log.d("HelpMe", error.toString())).subscribe();

            /*
             * Mudança de disponibilidade:
             * Disponibilidade do sensor: se mudar de disponibilidade, atualiza na lista de sensores disponíveis (que está
             * na cache) e reinicia a stream
             * */
            switchItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
                monitoredThingsBook.read("monitored_things").flatMapCompletable(monitoredThingsList ->{
                    ArrayList<TrackedThing> newMonitoredThingsList = (ArrayList<TrackedThing>) monitoredThingsList;
                    newMonitoredThingsList.get(listIndex).setAvailable(isChecked);
                    return monitoredThingsBook.write("monitored_things", newMonitoredThingsList).doOnComplete(() -> {
                        mainFragment.findBeacons();
                    });
                }).subscribe();
            });
        }
    }
}
