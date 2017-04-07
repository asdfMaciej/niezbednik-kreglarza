package maciej01.soft.niezbednikkreglarza;

import com.orm.SugarRecord;

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
public class Article extends SugarRecord implements Serializable, Cloneable {
    private String mDataString = "";

    private String mWynik; // i mean, you should use Int instead of String
    private String mPelne; // but that involves a lot of type conversion
    private String mZbierane; // and im too lazy to implement that
    private String mDziury; // despite it taking less space than comments in this declaration

    private String mZawodnik = "";
    private String mKregielnia = "";
    private String mKlub = "";

    private String mKomentarz = "";

    private String mTor11;  // so, I don't want to bother with implementing custom SQL database helper
    private String mTor12;  // as i said above, i'm lazy
    private String mTor13;  // so i'd rather this library i found - Sugar
    private String mTor14;  // and as you can see - it doesn't support arrays nor lists

    private String mTor21;  // wynik
    private String mTor22;  // pelne
    private String mTor23;  // zbierane
    private String mTor24;  // dziury

    private String mTor31;
    private String mTor32;
    private String mTor33;
    private String mTor34;

    private String mTor41;
    private String mTor42;
    private String mTor43;
    private String mTor44;

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
        mTor11 = tory[0][0];  // sigh
        mTor12 = tory[0][1];  // this code is painful to look at
        mTor13 = tory[0][2];
        mTor14 = tory[0][3];

        mTor21 = tory[1][0];
        mTor22 = tory[1][1];
        mTor23 = tory[1][2];
        mTor24 = tory[1][3];

        mTor31 = tory[2][0];
        mTor32 = tory[2][1];
        mTor33 = tory[2][2];
        mTor34 = tory[2][3];

        mTor41 = tory[3][0];
        mTor42 = tory[3][1];
        mTor43 = tory[3][2];
        mTor44 = tory[3][3];
        czyTory = true;
    }

    public void ustawData(int year, int month, int dayOfMonth) {
        String md = Integer.toString(year) + "-";
        String smt = Integer.toString(month+1);
        String sdy = Integer.toString(dayOfMonth);
        if (smt.length() == 1) { md += "0"+smt; } else { md += smt; } md += "-";
        if (sdy.length() == 1) { md += "0"+sdy; } else { md += sdy; }
        mDataString = md;
    }

    public void ustawKtoGdzie(String zawodnik, String klub, String kregielnia) {
        mZawodnik = zawodnik;
        mKlub = klub;
        mKregielnia = kregielnia;
    }

    public boolean contains(String cont) {
        boolean x = false;
        if (mZawodnik.toLowerCase().contains(cont)) {x = true;}
        if (mWynik.toLowerCase().contains(cont)) {x = true;}
        if (mDataString.toLowerCase().contains(cont)) {x = true;}
        if (mKregielnia.toLowerCase().contains(cont)) {x = true;}
        if (mKlub.toLowerCase().contains(cont)) {x = true;}
        if (mKomentarz.toLowerCase().contains(cont)) {x = true;}

        return x;
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
        if (!Arrays.deepEquals(d.getTory(), this.getTory())) {x = false;}

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

    public String[][] getTory() {
        return new String[][]{
                new String[]{mTor11, mTor12, mTor13, mTor14},
                new String[]{mTor21, mTor22, mTor23, mTor24},
                new String[]{mTor31, mTor32, mTor33, mTor34},
                new String[]{mTor41, mTor42, mTor43, mTor44},
        };
    }
    public boolean czyTory() { return czyTory; }
}
