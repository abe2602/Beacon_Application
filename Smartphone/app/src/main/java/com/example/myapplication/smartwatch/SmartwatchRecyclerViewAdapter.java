package com.example.myapplication.smartwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class SmartwatchRecyclerViewAdapter extends RecyclerView.Adapter<SmartwatchRecyclerViewAdapter.NumberViewHolder> {
    private ArrayList<String> connectedDevices;
    private ListenItemClick myClickListenner;

    /*Construtor da classe, recebe como parâmetro a quantidade de views*/
    public SmartwatchRecyclerViewAdapter(ArrayList<String> connectedDevices, ListenItemClick myClickListenner){
        this.connectedDevices = connectedDevices;
        this.myClickListenner = myClickListenner;
    }

    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.smartwatch_recyclerview_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediately = false;

        View view = inflater.inflate(layoutId, parent, attachImmediately);
        NumberViewHolder viewHolder = new NumberViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NumberViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return connectedDevices.size();
    }

    class NumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView deviceName = null;

        public NumberViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.textViewTeste);
            itemView.setOnClickListener(this);
        }

        public void bind(int listIndex){
            deviceName.setText(connectedDevices.get(listIndex));
        }

        @Override
        public void onClick(View v) {
            int clickPosition = getAdapterPosition();
            myClickListenner.onItemClick(clickPosition);
        }
    }

    public void clear() {
        int size = connectedDevices.size();
        connectedDevices.clear();
        notifyItemRangeRemoved(0, size);
    }

    public interface ListenItemClick {
        public void onItemClick(int clickItem);
    }
}