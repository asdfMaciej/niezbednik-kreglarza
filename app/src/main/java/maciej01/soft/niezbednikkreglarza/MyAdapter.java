package maciej01.soft.niezbednikkreglarza;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Maciej on 2017-03-19.
 */

public class MyAdapter extends RecyclerView.Adapter implements Filterable {

    // źródło danych
    private ArrayList<Article> mArticles = new ArrayList<>();
    private ArrayList<Article> originalList;
    // obiekt listy artykułów
    private RecyclerView mRecyclerView;
    public MainActivity contex;
    // implementacja wzorca ViewHolder
    // każdy obiekt tej klasy przechowuje odniesienie do layoutu elementu listy
    // dzięki temu wywołujemy findViewById() tylko raz dla każdego elementu
    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mWynik;
        public TextView mPelne;
        public TextView mZbierane;
        public TextView mDziury;
        public TextView mData;
        public TextView mZawodnik;
        public TextView mKregielnia;

        public MyViewHolder(View pItem) {
            super(pItem);
            mWynik = (TextView) pItem.findViewById(R.id.article_wynik);
            mPelne = (TextView) pItem.findViewById(R.id.article_pelne);
            mZbierane = (TextView) pItem.findViewById(R.id.article_zbierane);
            mDziury = (TextView) pItem.findViewById(R.id.article_dziury);
            mData = (TextView) pItem.findViewById(R.id.article_data);
            mZawodnik = (TextView) pItem.findViewById(R.id.article_zawodnik);
            mKregielnia = (TextView) pItem.findViewById(R.id.article_kregielnia);
        }
    }

    // konstruktor adaptera
    public MyAdapter(ArrayList<Article> pArticles, RecyclerView pRecyclerView, MainActivity cnt){
        mArticles = pArticles;
        originalList = pArticles;
        mRecyclerView = pRecyclerView;
        contex = cnt;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        // tworzymy layout artykułu oraz obiekt ViewHoldera
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.wynik_layout, viewGroup, false);

        // dla elementu listy ustawiamy obiekt OnClickListener,
        // który usunie element z listy po kliknięciu na niego
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionToDelete = mRecyclerView.getChildAdapterPosition(v);
                Article art = mArticles.get(positionToDelete);
                //mArticles.remove(positionToDelete);
                seriotakielatwedonaprawienia(view, art);
                //notifyItemRemoved(positionToDelete);
            }
        });

        // tworzymy i zwracamy obiekt ViewHolder
        return new MyViewHolder(view);
    }

    public void seriotakielatwedonaprawienia(View view, Article wynik) {
        contex.openWynik(wynik);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        // uzupełniamy layout artykułu
        Article article = mArticles.get(i);
        ((MyViewHolder) viewHolder).mWynik.setText(article.getWynik());
        ((MyViewHolder) viewHolder).mPelne.setText("P: "+article.getPelne());
        ((MyViewHolder) viewHolder).mZbierane.setText("Z: "+article.getZbierane());
        ((MyViewHolder) viewHolder).mDziury.setText("X: "+article.getDziury());
        ((MyViewHolder) viewHolder).mData.setText(article.getData());
        ((MyViewHolder) viewHolder).mZawodnik.setText(article.getZawodnik());
        ((MyViewHolder) viewHolder).mKregielnia.setText(article.getKregielnia());
    }
    @Override
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

    @Override
    public int getItemCount() {
        return mArticles.size();
    }
}
