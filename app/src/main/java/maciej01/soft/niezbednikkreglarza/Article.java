package maciej01.soft.niezbednikkreglarza;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Created by Maciej on 2017-03-19.
 */

@SuppressWarnings("serial")
public class Article implements Serializable, Cloneable {
    private String mDataString = "";

    private String mWynik; // i mean, you should use Int instead of String
    private String mPelne; // but that involves a lot of type conversion
    private String mZbierane; // and im too lazy to implement that
    private String mDziury; // despite it taking less space than comments in this declaration

    private String mZawodnik = "";
    private String mKregielnia = "";
    private String mKlub = "";

    private String mKomentarz = "";

    private String[] mTor1 = new String[4]; // the same, int array should be better
    private String[] mTor2 = new String[4]; // but since we're doing this, here's the syntax
    private String[] mTor3 = new String[4]; // [0] - mWynik, [1] - mPelne
    private String[] mTor4 = new String[4]; // [2] - mZbierane, [3] - mDziury

    private boolean czyTory = false;

    private static String[] sWynik = {"456", "602", "553", "520", "344", "662"};
    private static String[] sPelne = {"350", "400", "320", "313", "411", "332"};
    private static String[] sZbierane = {"110", "200", "178", "216", "247", "155"};
    private static String[] sDziury = {"0", "9", "11", "23", "7", "3", "5", "31"};
    private static String[] sZawodnik = {
            "Joe Doe", "Will Smith", "Adam Kowalski", "Jan Kowalski",
            "Jan Majchrzak", "Ritobuff Ryzepls"
        };
    private static String [] sKregielnia = {
            "Kręgielnia Dziewiątka-Amica Wronki", "Kręgielnia Vector Tarnowo Podgórne",
            "Kręgielnia Ośrodka Sportu i Rekreacji w Gostyniu", "Kręgielnia Czarna Kula Poznań"
    };
    private static String[] sData = {
            "2014-12-15", "2016-1-13", "2012-11-1", "2000-5-1", "2016-11-3"
    };

    public Article(String wynik, String pelne, String zbierane, String dziury) {
        mWynik = wynik;
        mPelne = pelne;
        mZbierane = zbierane;
        mDziury = dziury;
    }

    public Article(String wynik) {
        mWynik = wynik;
        mPelne = "";
        mZbierane = "";
        mDziury = "";
    }

    public Article() {
        losoweWartosci();
    }

    public Article clone() {
        try {
            return (Article)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new Article();
    }

    private void losoweWartosci() {
        Random random = new Random();

        mWynik = sWynik[random.nextInt(sWynik.length)];
        mPelne = sPelne[random.nextInt(sPelne.length)];
        mZbierane = sZbierane[random.nextInt(sZbierane.length)];
        mDziury = sDziury[random.nextInt(sDziury.length)];
        mZawodnik = sZawodnik[random.nextInt(sZawodnik.length)];
        mKregielnia = sKregielnia[random.nextInt(sKregielnia.length)];
        mDataString = sData[random.nextInt(sData.length)];
        mKlub = "KS Start Gostyń";
    }

    public void ustawWynik(String wynik, String pelne, String zbierane, String dziury) {
        mWynik = wynik;
        mPelne = pelne;
        mZbierane = zbierane;
        mDziury = dziury;
    }

    public void ustawTory(String[][] tory) {
        mTor1 = tory[0];
        mTor2 = tory[1];
        mTor3 = tory[2];
        mTor4 = tory[3];
        czyTory = true;
    }

    public void ustawData(int year, int month, int dayOfMonth) {
        String md = Integer.toString(year) + "-";
        String smt = Integer.toString(month+1);
        String sdy = Integer.toString(dayOfMonth);
        if (smt.length() == 1) { md += "0"+smt; } else { md += smt; } md += "-";
        if (sdy.length() == 1) { md += "0"+sdy; } else { md += sdy; }
        //mDataString = Integer.toString(year) + "-" + Integer.toString(month+1); // month is index
        //mDataString += "-" + Integer.toString(dayOfMonth); // not the month picked
        mDataString = md;
    }

    public void ustawKtoGdzie(String zawodnik, String klub, String kregielnia) {
        mZawodnik = zawodnik;
        mKlub = klub;
        mKregielnia = kregielnia;
    }

    public void ustawKomentarz(String komentarz) {
        mKomentarz = komentarz;
    }

    public boolean equals(Article d) {
        boolean x = true;
        if (!d.getWynik().equals(mWynik)) {x = false;}  // CTRL+C + CTRL+V     B))))
        if (!d.getPelne().equals(mPelne)) {x = false;}  // and wtf is this java bullshit
        if (!d.getZbierane().equals(mZbierane)) {x = false;}  // can't use ==, need to use .equals
        if (!d.getDziury().equals(mDziury)) {x = false;} // like i spent a hour debugging this
        if (!d.getData().equals(mDataString)) {x = false;}  // [just not in this particular
        if (!d.getZawodnik().equals(mZawodnik)) {x = false;}  // part of code]
        if (!d.getKregielnia().equals(mKregielnia)) {x = false;}
        if (!d.getKlub().equals(mKlub)) {x = false;}
        if (!d.getKomentarz().equals(mKomentarz)) {x = false;}
        if (!Arrays.deepEquals(d.getTory(), new String[][]{mTor1, mTor2, mTor3, mTor4})) {x = false;}

        return x;
    }

    public String getWynik() {
        return mWynik;
    }
    public String getPelne() { return mPelne; }
    public String getZbierane() { return mZbierane; }
    public String getDziury() { return mDziury; }
    public String getData() { return mDataString; }
    public String getZawodnik() { return mZawodnik; }
    public String getKregielnia() { return mKregielnia; }
    public String getKlub() {return mKlub; }
    public String getKomentarz() {return mKomentarz;}

    public String[][] getTory() { return new String[][]{mTor1, mTor2, mTor3, mTor4};}
    public boolean czyTory() { return czyTory; }
}
