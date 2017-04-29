package io.maciej01.niezbednikkreglarza.turniej;

import android.content.res.Resources;
import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.maciej01.niezbednikkreglarza.Article;
import io.maciej01.niezbednikkreglarza.R;
import io.maciej01.niezbednikkreglarza.SugarOrmTestApplication;

/**
 * Created by Maciej on 2017-04-24.
 */

public class Turniej implements Serializable, Cloneable {
    private TurniejHolder turniej;
    private ArrayList<Article> articles;
    private ArrayList<Article> turniej_art = new ArrayList<>();
    private ArrayList<ArrayList<Article>> posortowane = new ArrayList<>();
    private TurniejList parent;

    public Turniej(TurniejList tParent,
            int ySt, int mSt, int dSt, // year month day of start
            int yNd, int mNd, int dNd, // year month day of end
            String _kregielnia, String _nazwa) {
        parent = tParent;
        turniej = new TurniejHolder(ySt,mSt,dSt,yNd,mNd,dNd,_kregielnia,_nazwa);
    }

    public Turniej(TurniejList tParent, TurniejHolder tHolder) {
        turniej = tHolder;
        parent = tParent;
    }

    public Turniej(TurniejList tParent) {
        turniej = new TurniejHolder();
        parent = tParent;
    }

    public Turniej(TurniejList tParent, boolean use_this_for_random_values) { // :^^^)
        turniej = new TurniejHolder(true);
        parent = tParent;
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

    public void save() {
        turniej.save();
    }

    private void initArticles() {
        articles = parent.getArticles();
        if (articles == null) {
            articles = (ArrayList<Article>) Article.listAll(Article.class);
            Log.v("turniej", "unable to init!");
        }
    }

    public Turniej clone() {
        try {
            return (Turniej) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new Turniej(parent);
    }

    public void coJaUczynilem() {
        posortowane = new ArrayList<>();
        Log.v("turniej", "posortowane na nowo!");
        Map<String, Integer> myMap = new HashMap<String, Integer>();
        String z; // im sleepy and wondering whether this algorithm will work or not
        Integer n = 0;
        for (Article a : turniej_art) {
            z = a.getZawodnik();
            if (!myMap.containsKey(z)) {
                myMap.put(z, n);
                posortowane.add(new ArrayList<Article>());
                posortowane.get(n).add(a);
                n += 1;
            } else {
                Integer x = myMap.get(z);
                posortowane.get(x).add(a);
            }
        }

        Collections.sort(posortowane, new Comparator<ArrayList<Article>>() {
            @Override // jak to zadziala to bede szczesliwy
            public int compare(ArrayList<Article> o1, ArrayList<Article> o2) {
                int int1 = 0; // a jak nie to czeka mnie dlugie debuggowanie
                int int2 = 0; // :V
                for (Article a : o1) {int1 += Integer.parseInt(a.getWynik());}                     // UPDATE - DZIALA ZA PIERWSZYM XD
                for (Article a : o2) {int2 += Integer.parseInt(a.getWynik());}
                return ((Integer) int2).compareTo(int1);
                //return ((Integer) Integer.parseInt(o2.getWynik())).compareTo(Integer.parseInt(o1.getWynik()));
            }
        });
        /*
        RESULT:
        tablica w ktorej [0] = Arraylist z wynikami jednej osoby z najwyzszym wynikiem
        [1] - arraylist z wynikami drugiej osoby
        etc..
         */
    }


    public int getDateDiffString(Date dateOne, Date dateTwo)
    {
        long timeOne = dateOne.getTime();
        long timeTwo = dateTwo.getTime();
        long oneDay = 1000 * 60 * 60 * 24;
        long delta = (timeTwo - timeOne) / oneDay;

        if (delta > 0) {
            return (int) delta;
        }
        else {
            delta *= -1;
            return (int) delta;
        }
    }

    public String desc_date() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String turniej_start = turniej.getDateStart();
            String turniej_end = turniej.getDateEnd();
            Date dStart = sdf.parse(turniej_start);
            Date dEnd = sdf.parse(turniej_end);
            Date dIrl = new Date();
            if (dStart.compareTo(dIrl) * dIrl.compareTo(dEnd) >= 0) {
                return SugarOrmTestApplication.getContext().getString(R.string.tournament_pending);
            } else if (dIrl.after(dEnd)){
                String ret = SugarOrmTestApplication.getContext().getString(R.string.tournament_happened);
                ret += Integer.toString(getDateDiffString(dIrl, dEnd))+" ";
                ret += SugarOrmTestApplication.getContext().getString(R.string.days_ago);
                return ret;
            } else if (dIrl.before(dStart)) {
                String ret = SugarOrmTestApplication.getContext().getString(R.string.tournament_in);
                ret += Integer.toString(getDateDiffString(dStart, dIrl))+" ";
                ret += SugarOrmTestApplication.getContext().getString(R.string.days);
                return ret;
            } else {
                return "To nie powinno się zdarzyć - jeśli się zdarzy, pisz do programisty.";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "PARSEEXCEPTION To nie powinno się zdarzyć - jeśli się zdarzy, pisz do programisty.";
        }
    }

    public void initTurniej() {
        try {
            initArticles();
            turniej_art = new ArrayList<>();
            Date start = dataStringToDate(turniej.getDateStart());
            Date end = dataStringToDate(turniej.getDateEnd());
            for (Article a : articles) {
                Date d = dataStringToDate(a.getData());
                if (isWithinRange(d, start, end)) {
                    if (a.getKregielnia().equals(turniej.getKregielnia())) {
                        turniej_art.add(a.clone());
                        Log.v("turniejclass", "date&kregielnia");
                    } else {
                        Log.v("turniejclass", "date, nie kregielnia");
                    }

                } else {
                    Log.v("turniejclass", "not!");
                }
            }
            Log.v("ilosc", Integer.toString(turniej_art.size()));
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

    public void ustawDateStart(int year, int month, int dayOfMonth) {turniej.ustawDateStart(year, month, dayOfMonth);}
    public void ustawDateEnd(int year, int month, int dayOfMonth) {turniej.ustawDateEnd(year, month, dayOfMonth);}
    public void ustawCoGdzie(String nzw, String kregielnia) {turniej.ustawCoGdzie(nzw, kregielnia);}

    public String getDateStart() { return turniej.getDateStart(); }
    public String getDateEnd() { return turniej.getDateEnd(); }
    public String getKregielnia() { return turniej.getKregielnia(); }
    public String getNazwa() { return turniej.getNazwa(); }
    public ArrayList<ArrayList<Article>> getPosortowane() { return posortowane; }
    public int countZawodnicy() {return new Article().zawodnicyFromArray(turniej_art).size();}
}
