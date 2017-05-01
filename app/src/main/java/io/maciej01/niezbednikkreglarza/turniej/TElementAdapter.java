package io.maciej01.niezbednikkreglarza.turniej;

/**
 * Created by Maciej on 2017-04-28.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;

import io.maciej01.niezbednikkreglarza.Article;
import io.maciej01.niezbednikkreglarza.PathUtil;
import io.maciej01.niezbednikkreglarza.R;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

import io.maciej01.niezbednikkreglarza.R;

/**
 * Created by Maciej on 2017-04-25.
 */

public class TElementAdapter extends RecyclerView.Adapter implements Serializable {
    private Turniej turniej;
    private RecyclerView mRecyclerView;
    private int kategoria;
    public TElementActivity contex;
    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mN;
        public TextView mNazwa;
        public TextView mKlub;
        public TextView mTekst;
        public TextView mLacznie;
        public TextView hData;
        public TextView hDatadesc;
        public TextView hNazwa;
        public TextView hKregielnia;

        public MyViewHolder(View pItem) {
            super(pItem);
            mN = (TextView) pItem.findViewById(R.id.elem_n);
            mNazwa = (TextView) pItem.findViewById(R.id.elem_nazwa);
            mKlub = (TextView) pItem.findViewById(R.id.elem_klub);
            mTekst = (TextView) pItem.findViewById(R.id.elem_tekst);
            mLacznie = (TextView) pItem.findViewById(R.id.elem_lacznie);

            hData = (TextView) pItem.findViewById(R.id.head_data);
            hDatadesc = (TextView) pItem.findViewById(R.id.head_datadesc);
            hNazwa = (TextView) pItem.findViewById(R.id.head_nazwa);
            hKregielnia = (TextView) pItem.findViewById(R.id.head_kregielnia);
        }
    }

    @Override
    public int getItemViewType(int position){
        if(position == 0){
            return 0;
        } else {
            return 1;
        }
    }

    // konstruktor adaptera
    public TElementAdapter(Turniej pTurniej, RecyclerView pRecyclerView, TElementActivity cnt, int ktg){
        turniej = pTurniej;
        mRecyclerView = pRecyclerView;
        contex = cnt;
        kategoria = ktg;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        final View view;
        if(i==1){
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.zawynik_fragment, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.zawody_header, viewGroup, false);
        }

        // dla elementu listy ustawiamy obiekt OnClickListener,
        // który usunie element z listy po kliknięciu na niego
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*int positionToDelete = mRecyclerView.getChildAdapterPosition(v);
                Turniej art = mTurnieje.getLista().get(positionToDelete);
                //mArticles.remove(positionToDelete);
                seriotakielatwedonaprawienia(view, art);
                //notifyItemRemoved(positionToDelete);*/
            }
        });

        // tworzymy i zwracamy obiekt ViewHolder
        return new TElementAdapter.MyViewHolder(view);
    }

    public void seriotakielatwedonaprawienia(View view, Turniej wynik) {
        //contex.openWynik(wynik);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        if (i == 0){
            String sData = turniej.getDateStart() + " - " + turniej.getDateEnd();
            ((TElementAdapter.MyViewHolder) viewHolder).hNazwa.setText(turniej.getNazwa());
            ((TElementAdapter.MyViewHolder) viewHolder).hKregielnia.setText(turniej.getKregielnia());
            ((TElementAdapter.MyViewHolder) viewHolder).hData.setText(sData);
            ((TElementAdapter.MyViewHolder) viewHolder).hDatadesc.setText(turniej.desc_date());

            Button jedenzdrugim = (Button) viewHolder.itemView.findViewById(R.id.head_delete);
            jedenzdrugim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contex.jedenzdrugim();
                }
            });

            (viewHolder.itemView.findViewById(R.id.head_change)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contex.kolejnyprzycisk();
                }
            });
        } else {
            ArrayList<ArrayList<ArrayList<Article>>> posortowane = turniej.getPosortowane();
            Article ex = posortowane.get(kategoria).get(i-1).get(0);
            String lacznie = contex.getString(R.string.total);
            lacznie += Integer.toString(ex.podliczWyniki(posortowane.get(kategoria).get(i-1)));
            ((TElementAdapter.MyViewHolder) viewHolder).mN.setText("#"+Integer.toString(i));
            ((TElementAdapter.MyViewHolder) viewHolder).mNazwa.setText(ex.getZawodnik());
            ((TElementAdapter.MyViewHolder) viewHolder).mKlub.setText(ex.getKlub());
            ((TElementAdapter.MyViewHolder) viewHolder).mTekst.setText(ex.summaryFromWyniki(posortowane.get(kategoria).get(i-1)));
            ((TElementAdapter.MyViewHolder) viewHolder).mLacznie.setText(lacznie);

        }
    }
    /*@Override
    public Filter getFilter() {
        Log.v("test", "getfilter");
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mArticles = (ArrayList<Article>) results.values;
                notifyDataSetChanged();
                Log.v("test", "publishresults");
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.v("test", "performfiltering");
                ArrayList<Article> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = originalList;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    protected ArrayList<Article> getFilteredResults(String constraint) {
        Log.v("test", "getfiltered");
        ArrayList<Article> results = new ArrayList<>();

        for (Article item : originalList) {
            if (item.contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }
    */
    @Override
    public int getItemCount() {
        ArrayList<ArrayList<ArrayList<Article>>> posortowane = turniej.getPosortowane();
        if (!posortowane.isEmpty()) {
            int n = posortowane.get(kategoria).size()+1;
            Log.v("mojametoda", Integer.toString(n));
            return n;
        } else {
            return 1;
        }
    }
}
