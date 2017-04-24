package io.maciej01.niezbednikkreglarza.turniej;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import io.maciej01.niezbednikkreglarza.Article;

/**
 * Created by Maciej on 2017-04-24.
 */

public class Turniej {
    private TurniejHolder turniej;
    private ArrayList<Article> articles;
    private ArrayList<Article> turniej_art = new ArrayList<>();

    public Turniej(
            int ySt, int mSt, int dSt, // year month day of start
            int yNd, int mNd, int dNd, // year month day of end
            String _kregielnia, String _nazwa) {
        turniej = new TurniejHolder(ySt,mSt,dSt,yNd,mNd,dNd,_kregielnia,_nazwa);
        initArticles();
    }

    public Turniej(TurniejHolder tHolder) {
        turniej = tHolder;
    }

    public Turniej() {
        turniej = new TurniejHolder();
        initArticles();
    }

    private Date dataStringToDate(String datastring) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = sdf.parse(datastring);
        return d;
    }

    private boolean isWithinRange(Date testDate, Date startDate, Date endDate) {
        return !(testDate.before(startDate) || testDate.after(endDate));
    }

    private void sortArticles() {
        Collections.sort(turniej_art, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return ((Integer) Integer.parseInt(o2.getWynik())).compareTo(Integer.parseInt(o1.getWynik()));
            }
        });
    }

    private void initArticles() {
        try {
            articles = (ArrayList<Article>) Article.listAll(Article.class);
            Date start = dataStringToDate(turniej.getDateStart());
            Date end = dataStringToDate(turniej.getDateEnd());
            for (Article a : articles) {
                Date d = dataStringToDate(a.getData());
                if (isWithinRange(d, start, end)) {
                    turniej_art.add(a.clone());
                    Log.v("turniejclass", "within!");
                } else {
                    Log.v("turniejclass", "not!");
                }
            }
            sortArticles();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean equals(Turniej t) {
        return (t.getTurniej().equals(turniej));
    }

    public TurniejHolder getTurniej() {return turniej;}
    public ArrayList<Article> getArticles() {return turniej_art;}

    public String getDateStart() { return turniej.getDateStart(); }
    public String getDateEnd() { return turniej.getDateEnd(); }
    public String getKregielnia() { return turniej.getKregielnia(); }
    public String getNazwa() { return turniej.getNazwa(); }
}
