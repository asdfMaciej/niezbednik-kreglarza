package maciej01.soft.niezbednikkreglarza;

import android.content.Context;

import com.orm.SugarRecord;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Maciej on 2017-03-27.
 */

public class Zapisane implements Serializable {
    private ArrayList<Article> artykuly;
    private String filename = "tajneinformacje";

    public void zapisz(Context context) throws IOException {
        FileOutputStream fos;
        ObjectOutputStream os;
        fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
        os = new ObjectOutputStream(fos);
        os.writeObject(Zapisane.this);
        os.close();
        fos.close();
    }

    public Zapisane wczytaj(Context context) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(filename);
        ObjectInputStream is = new ObjectInputStream(fis);
        Zapisane simpleClass = (Zapisane) is.readObject();
        is.close();
        fis.close();
        return simpleClass;
    }

    public ArrayList<Article> dajarticles() {return artykuly;}
    public void ustawarticles(ArrayList<Article> a) {artykuly = a;}
}
