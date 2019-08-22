package com.example.beacon.smartwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.beacon.R;

import java.util.ArrayList;

public class SmartwatchRecyclerViewAdapter extends RecyclerView.Adapter<SmartwatchRecyclerViewAdapter.NumberViewHolder> {
    private ArrayList<String> connectedDevices;
    private ListenItemClick myClickListenner;

    /*Construtor da classe, recebe como parâmetro a quantidade de views*/
    public SmartwatchRecyclerViewAdapter(ArrayList<String> connectedDevices, ListenItemClick myClickListenner){
        this.connectedDevices = connectedDevices;
        this.myClickListenner = myClickListenner;
    }

    /*
     * Esse método é chamado sempre que um novo ViewHolder é criado, ou seja, sempre que
     * há um "scroll" do RecylerView para cima/baixo, de modo que seja necessário reciclar
     * os dados.
     * Como parâmetros, temos dois caras, o "ViewGroup" e "viewType".
     * ViewGroup: Serve para identificar em qual grupo os ViewHolders estão contidos
     * viewType: Serve para identificar quais tipos de layout teremos na recyclerView
     * Tem como retorno uma ViewHolder que contem as views de cada item
     * */
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

    /*
     * É chamado pelo RecyclerView para exibir os dados em suas posições corretas
     * recebe um ViewHolder e a posição do dado
     * */
    @Override
    public void onBindViewHolder(NumberViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return connectedDevices.size();
    }

    /*O ViewHolder ajuda a melhorar a performance do aplicativo, para que
     * não tenhamos que ficar procurando os itens com findviewbyid toda santa
     * hora*/
    class NumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /*Como em nosso layout existe somente um TextView, aqui dentro
         * teremos apenas um TextView*/
        TextView deviceName = null;

        public NumberViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.textViewTeste);
            itemView.setOnClickListener(this);
        }

        /*Seta um texto no TextView, nesse caso, um inteiro*/
        public void bind(int listIndex){
            deviceName.setText(connectedDevices.get(listIndex));
        }

        @Override
        public void onClick(View v) {
            /*Linkamos a posição do click com o nosso onItemClick*/
            int clickPosition = getAdapterPosition();
            myClickListenner.onItemClick(clickPosition);
        }
    }

    public void clear() {
        int size = connectedDevices.size();
        connectedDevices.clear();
        notifyItemRangeRemoved(0, size);
    }

    /*Como não há tratadores de toques no RecyclerView, criaremos o nosso próprio!*/
    public interface ListenItemClick {
        public void onItemClick(int clickItem);
    }
}