package io.maciej01.niezbednikkreglarza.turniej;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by Maciej on 2017-04-24.
 */

@SuppressWarnings("serial")
public class TurniejHolder extends SugarRecord implements Serializable, Cloneable {
    private String dateStart = ""; // nie przechowuje arraylist bo >>>sugarrecord
    private String dateEnd = ""; // to by mi duplikowalo rzeczy w bazie danych w dodatku

    private String kregielnia = ""; // troche wydluzy runtime, ale to szczegol
    private String nazwa = ""; // daty w stringu bo tez >>>sugarrecord

    public TurniejHolder(
            int ySt, int mSt, int dSt, // year month day of start
            int yNd, int mNd, int dNd, // year month day of end
            String _kregielnia, String _nazwa) {
        dateStart = ustawDate(ySt, mSt, dSt);
        dateEnd = ustawDate(yNd, mNd, dNd);
        kregielnia = _kregielnia;
        nazwa = _nazwa;
    }

    public TurniejHolder() {
        dateStart = "2000-03-01";
        dateEnd = "2015-10-21";
        kregielnia = "Kręgielnia Dziewiątka-Amica Wronki";
        nazwa = "Rychu Peja - Piętnastak";
    }

    public TurniejHolder clone() {
        try {
            return (TurniejHolder) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new TurniejHolder();
    }

    public String ustawDate(int year, int month, int dayOfMonth) {
        String md = Integer.toString(year) + "-";
        String smt = Integer.toString(month+1);
        String sdy = Integer.toString(dayOfMonth);
        if (smt.length() == 1) { md += "0"+smt; } else { md += smt; } md += "-";
        if (sdy.length() == 1) { md += "0"+sdy; } else { md += sdy; }
        return md;
    }

    public boolean equals(TurniejHolder d) {
        boolean x = true;
        if (!d.getDateStart().equals(dateStart)) {x = false;}
        if (!d.getDateEnd().equals(dateEnd)) {x = false;}
        if (!d.getKregielnia().equals(kregielnia)) {x = false;}
        if (!d.getNazwa().equals(nazwa)) {x = false;}
        return x;
    }

    public String getDateStart() { return dateStart; }
    public String getDateEnd() { return dateEnd; }
    public String getKregielnia() { return kregielnia; }
    public String getNazwa() { return nazwa; }
}
