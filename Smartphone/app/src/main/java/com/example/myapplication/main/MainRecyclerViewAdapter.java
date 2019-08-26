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

    /*Construtor da classe, recebe como parâmetro a quantidade de views*/
    public MainRecyclerViewAdapter(Context context, ArrayList<TrackedThing> availableThings){
        this.availableThings = availableThings;
        this.context = context;
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

        Log.d("MySize", Integer.toString(availableThings.size()));
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
            RxPaperBook.init(context);
            RxPaperBook bookAvailableSensors = RxPaperBook.with("available_sensors");
            RxPaperBook book = RxPaperBook.with("monitored_things");

            RxView.clicks(deleteThing).flatMapCompletable(s ->
                    bookAvailableSensors.read("available_sensors").flatMapCompletable(list -> {
                        deletedThings = (ArrayList<TrackedThing>) list;
                        deletedThings.add(availableThings.get(listIndex));
                        availableThings.remove(availableThings.get(listIndex));
                        return bookAvailableSensors.write("available_sensors", deletedThings)
                                .andThen(book.write("monitored_things", availableThings)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnComplete(() ->{
                                            notifyItemRemoved(listIndex);
                                            notifyItemRangeChanged(listIndex, availableThings.size()); }
                                        )
                                );
                    })).doOnError(error -> Log.d("HelpMe", error.toString())).subscribe();

            RxView.clicks(switchItem).subscribe();
        }
    }
}
