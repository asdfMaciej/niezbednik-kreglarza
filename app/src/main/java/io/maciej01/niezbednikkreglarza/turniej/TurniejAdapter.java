package io.maciej01.niezbednikkreglarza.turniej;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.maciej01.niezbednikkreglarza.R;

/**
 * Created by Maciej on 2017-04-25.
 */

public class TurniejAdapter extends RecyclerView.Adapter {
    private TurniejList mTurnieje = new TurniejList();
    private TurniejList originalList;
    private RecyclerView mRecyclerView;
    public TurniejActivity contex;
    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mNazwa;
        public TextView mData;
        public TextView mZawodnicy;
        public TextView mKregielnia;

        public MyViewHolder(View pItem) {
            super(pItem);
            mNazwa = (TextView) pItem.findViewById(R.id.zawody_nazwa);
            mData = (TextView) pItem.findViewById(R.id.zawody_data);
            mKregielnia = (TextView) pItem.findViewById(R.id.zawody_kregielnia);
            mZawodnicy = (TextView) pItem.findViewById(R.id.zawody_zawodnicy);
        }
    }

    // konstruktor adaptera
    public TurniejAdapter(TurniejList pTurnieje, RecyclerView pRecyclerView, TurniejActivity cnt){
        mTurnieje = pTurnieje;
        originalList = pTurnieje;
        mRecyclerView = pRecyclerView;
        contex = cnt;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        // tworzymy layout artykułu oraz obiekt ViewHoldera
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.zawody_fragment, viewGroup, false);

        // dla elementu listy ustawiamy obiekt OnClickListener,
        // który usunie element z listy po kliknięciu na niego
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionToDelete = mRecyclerView.getChildAdapterPosition(v);
                Turniej art = mTurnieje.getLista().get(positionToDelete);
                //mArticles.remove(positionToDelete);
                seriotakielatwedonaprawienia(view, art);
                //notifyItemRemoved(positionToDelete);
            }
        });

        // tworzymy i zwracamy obiekt ViewHolder
        return new TurniejAdapter.MyViewHolder(view);
    }

    public void seriotakielatwedonaprawienia(View view, Turniej wynik) {
        //contex.openWynik(wynik);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        // uzupełniamy layout artykułu
        Turniej article = mTurnieje.get(i);
        String sData = article.getDateStart() + " - " + article.getDateEnd();
        String sZawodnicy = contex.getString(R.string.participated);
        sZawodnicy += Integer.toString(article.countZawodnicy());
        sZawodnicy +=" "+contex.getString(R.string.participants_d);
        ((TurniejAdapter.MyViewHolder) viewHolder).mNazwa.setText(article.getNazwa());
        ((TurniejAdapter.MyViewHolder) viewHolder).mData.setText(sData);
        ((TurniejAdapter.MyViewHolder) viewHolder).mZawodnicy.setText(sZawodnicy);
        ((TurniejAdapter.MyViewHolder) viewHolder).mKregielnia.setText(article.getKregielnia());
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
        return mTurnieje.size();
    }
}