package com.example.beacon.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.beacon.R;

import java.util.ArrayList;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.NumberViewHolder> {
    private ArrayList<String> availableThings;

    /*Construtor da classe, recebe como parâmetro a quantidade de views*/
    public MainRecyclerViewAdapter(ArrayList<String> availableThings){
        this.availableThings = availableThings;
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
    public MainRecyclerViewAdapter.NumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.main_recyclerview_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediately = false;

        View view = inflater.inflate(layoutId, parent, attachImmediately);
        MainRecyclerViewAdapter.NumberViewHolder viewHolder = new MainRecyclerViewAdapter.NumberViewHolder(view);

        return viewHolder;
    }

    /*
     * É chamado pelo RecyclerView para exibir os dados em suas posições corretas
     * recebe um ViewHolder e a posição do dado
     * */
    @Override
    public void onBindViewHolder(MainRecyclerViewAdapter.NumberViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return availableThings.size();
    }

    /*O ViewHolder ajuda a melhorar a performance do aplicativo, para que
     * não tenhamos que ficar procurando os itens com findviewbyid toda santa
     * hora*/
    class NumberViewHolder extends RecyclerView.ViewHolder {

        /*Como em nosso layout existe somente um TextView, aqui dentro
         * teremos apenas um TextView*/
        TextView thingName;

        public NumberViewHolder(View itemView) {
            super(itemView);
            thingName = itemView.findViewById(R.id.thingNameTextView);
        }

        /*Seta um texto no TextView, nesse caso, um inteiro*/
        public void bind(int listIndex){
            thingName.setText(availableThings.get(listIndex));
        }

    }
}
