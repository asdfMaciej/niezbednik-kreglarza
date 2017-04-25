package io.maciej01.niezbednikkreglarza.turniej;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.maciej01.niezbednikkreglarza.Article;

/**
 * Created by Maciej on 2017-04-24.
 */

public class TurniejList {
    private ArrayList<Turniej> lista = new ArrayList<>();
    private ArrayList<Article> articles;

    public TurniejList() {
        articles = (ArrayList<Article>) Article.listAll(Article.class);
    }
    public void load() {
        ArrayList<TurniejHolder> temp = (ArrayList<TurniejHolder>) TurniejHolder.listAll(TurniejHolder.class);
        for (TurniejHolder t : temp) {lista.add(new Turniej(this, t));}
    }

    public void add(int ySt, int mSt, int dSt, // year month day of start
                    int yNd, int mNd, int dNd, // year month day of end
                    String _kregielnia, String _nazwa) {
        Turniej tur = new Turniej(this, ySt,mSt,dSt,yNd,mNd,dNd,_kregielnia,_nazwa);
        this.add(tur);
    }

    public void add(Turniej tur) {
        lista.add(tur);
        tur.getTurniej().save();
    }

    public int size() {return lista.size();}
    public void clear() {lista.clear();}

    public Integer findIndex(Turniej tur) {
        Integer index = null;
        for (Turniej t : lista) {
            if (index == null) {index = 0;}
            if (tur.equals(t)) {return index;}
            index += 1;
        }
        return null;
    }

    public Turniej get(int n) {return lista.get(n);}
    public ArrayList<Turniej> getLista() {return lista;}
    public ArrayList<Article> getArticles() {return articles;}
}
