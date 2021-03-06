package io.maciej01.niezbednikkreglarza;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import static android.view.View.GONE;

/**
 * Created by Maciej on 2017-03-21.
 */

public class WynikActivity extends AppCompatActivity {
    public Article scores;
    public Article scores_old;
    private Fade mFade;
    static final int ZMIANA_WYNIKU = 1;
    public AccelerateInterpolator bi = new AccelerateInterpolator();
    public WynikSwipe ws = new WynikSwipe();

    public class WynikSwipe extends ActivitySwipeDetector {

        public WynikSwipe() {
            super();
        }

        @Override
        public final void onLeftToRightSwipe() {
            super.onLeftToRightSwipe();
            onBackPressed();
        }

        public final void onRightToLeftSwipe() {
            super.onRightToLeftSwipe();
            openZmiana(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wynik_main);
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                Intent i = getIntent();
                boolean disabled = (boolean) i.getSerializableExtra("disabled");
                if (scores == null) {
                    scores = (Article) i.getSerializableExtra("wynik");
                    scores_old = ((Article) i.getSerializableExtra("wynik")).clone();
                }
                setTextViews();

                Button zmiana = (Button) findViewById(R.id.bZmiana);
                Button usun = (Button) findViewById(R.id.bUsun);
                EditText komek = (EditText) findViewById(R.id.edKomentarz);
                zmiana.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openZmiana(false);
                    }
                });
                if (disabled) {
                    zmiana.setVisibility(GONE);
                    usun.setVisibility(GONE);
                    komek.setVisibility(GONE);
                } else {
                    zmiana.setVisibility(View.VISIBLE);
                    usun.setVisibility(View.VISIBLE);
                    komek.setVisibility(View.VISIBLE);
                }

                return;
                    }

        });
        thread.start();
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        seppuku();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //Do your No progress
                        break;
                }
            }
        };
        final AlertDialog.Builder ab = new AlertDialog.Builder(this);

        Button jedenzdrugim = (Button) findViewById(R.id.bUsun);
        jedenzdrugim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ab.setMessage(R.string.delete_sure).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
            }
        });

        //final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.wynik_layout, this);
    }

    public void seppuku() {
        Intent intent = new Intent();
        intent.putExtra("wynik", scores);
        intent.putExtra("wynik_old", scores_old);
        intent.putExtra("dead", true);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        super.dispatchTouchEvent(ev);
        return ws.onTouch(ev);
    }


    public void setTextViews() {
        String wynik = scores.getWynik();
        String pelne = scores.getPelne();
        String zbierane = scores.getZbierane();
        String dziury = scores.getDziury();
        String kregielnia = scores.getKregielnia();
        String zawodnik = scores.getZawodnik();
        String data = scores.getData();
        String komentarz = scores.getKomentarz();
        String kategoria = scores.getKategoria();
        Log.e("komentarz", komentarz);

        TextView tvWynik = (TextView) findViewById(R.id.tWynik);
        TextView tvPelne = (TextView) findViewById(R.id.tPelne);
        TextView tvZbierane = (TextView) findViewById(R.id.tZbierane);
        TextView tvDziury = (TextView) findViewById(R.id.tDziury);
        TextView tvKregielnia = (TextView) findViewById(R.id.tKregielnia);
        TextView tvZawodnik = (TextView) findViewById(R.id.tZawodnik);
        TextView tvData = (TextView) findViewById(R.id.tData);
        TextView tvKategoria = (TextView) findViewById(R.id.tKategoria);
        LinearLayout tor1 = (LinearLayout) findViewById(R.id.lTory1);
        LinearLayout tor2 = (LinearLayout) findViewById(R.id.lTory2);
        EditText tvKomentarz = (EditText) findViewById(R.id.edKomentarz);

        tvWynik.setText(getString(R.string.score_m)+": "+wynik);
        tvPelne.setText("P: "+pelne);
        tvZbierane.setText("Z: "+zbierane);
        tvDziury.setText("X: "+dziury);
        tvKregielnia.setText(kregielnia);
        tvZawodnik.setText(zawodnik);
        tvData.setText(data);
        tvKomentarz.setText(komentarz);
        tvKategoria.setText(kategoria);
        boolean toryustawione = scores.czyTory();
        Resources r = getApplicationContext().getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                70,
                r.getDisplayMetrics()
        );
        if (toryustawione) {
            tor1.setVisibility(View.VISIBLE);
            tor2.setVisibility(View.VISIBLE);
            setTory();
            ViewGroup.LayoutParams lp = (tvKomentarz).getLayoutParams();
            if( lp instanceof ViewGroup.MarginLayoutParams)
            {((ViewGroup.MarginLayoutParams) lp).topMargin = 0;}
        } else {
            tor1.setVisibility(GONE);
            tor2.setVisibility(GONE);
            ViewGroup.LayoutParams lp = (tvKomentarz).getLayoutParams();
            if( lp instanceof ViewGroup.MarginLayoutParams)
            {((ViewGroup.MarginLayoutParams) lp).topMargin = px;}
        }

    }

    public void setTory() {
        Integer[] torki = {
                R.id.article_wynik, R.id.article_pelne, R.id.article_zbierane, R.id.article_dziury,
                R.id.article_wynik2, R.id.article_pelne2, R.id.article_zbierane2, R.id.article_dziury2,
                R.id.article_wynik3, R.id.article_pelne3, R.id.article_zbierane3, R.id.article_dziury3,
                R.id.article_wynik4, R.id.article_pelne4,  R.id.article_zbierane4, R.id.article_dziury4
        };
        String[][] tory = scores.getTory();
        String[] tory2d = new String[16];
        int i = 0;
        for (String[] tor : tory) {
            for (String xD : tor) {
                tory2d[i] = xD;
                i += 1;
            }
        }
        i = 0;
        for (String tor : tory2d) {
            TextView element = (TextView) findViewById(torki[i]);
            String[] pythonowasztuczka = {"", "P: ", "Z: ", "X: "};
            element.setText(pythonowasztuczka[i%4]+tor);
            i += 1;
        }
    }
    public void openZmiana(boolean swiped) {
        Intent intent;
        scores.ustawKomentarz(((EditText) findViewById(R.id.edKomentarz)).getText().toString());
        intent = new Intent(this, ZmianaActivity.class);
        intent.putExtra("wynik", scores);
        intent.putExtra("rodzaj", "zmiana");
        intent.putExtra("swiped", swiped);
        //view.getContext().startActivityForResult(intent);
        //Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivityForResult(intent, ZMIANA_WYNIKU);//, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());//, bundle);
        if (swiped) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ZMIANA_WYNIKU) {
            if (resultCode == RESULT_OK) {
                scores = (Article) data.getSerializableExtra("wynik");
                setTextViews();
                scores.save();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        scores.ustawKomentarz(((EditText) findViewById(R.id.edKomentarz)).getText().toString());
        Log.e("komentarz", ((EditText) findViewById(R.id.edKomentarz)).getText().toString());
        Intent intent = new Intent();
        intent.putExtra("wynik", scores);
        intent.putExtra("wynik_old", scores_old);
        intent.putExtra("dead", false);
        setResult(RESULT_OK, intent);
        finish();
        //overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        //overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
