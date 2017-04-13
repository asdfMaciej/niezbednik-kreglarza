package maciej01.soft.niezbednikkreglarza;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Maciej on 2017-04-03.
 */

public class StatystykiActivity  extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Serializable {

    public ArrayList<Article> articles = new ArrayList<>();
    public String spZawodnik;
    public String spKlub;
    public Spinner spinner;
    public ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.lapp1).setVisibility(GONE);
        findViewById(R.id.lapp2).setVisibility(VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        fab.setVisibility(GONE);

        Intent i = getIntent();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Wykresy"));
        tabLayout.addTab(tabLayout.newTab().setText("Tabele"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        RelativeLayout wykresy = (RelativeLayout) findViewById(R.id.include_wykres);
                        RelativeLayout tabele = (RelativeLayout) findViewById(R.id.include_tabele);
                        switch (tab.getPosition()) {
                            case 0:
                                Log.v("tab", "0");
                                wykresy.setVisibility(VISIBLE);
                                tabele.setVisibility(GONE);
                                break;
                            case 1:
                                Log.v("tab", "1");
                                wykresy.setVisibility(GONE);
                                tabele.setVisibility(VISIBLE);
                                break;
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {}
                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {}
                }
        );
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        spZawodnik = SP.getString("zawodnik","Domyślny Zawodnik");
        spKlub = SP.getString("klub","KK Dziewiątka-Amica Wronki");
        if (spKlub.equals("1")) {
            spKlub = "KK Ustaw Klub w Opcjach";
        }
        articles = (ArrayList<Article>)i.getSerializableExtra("articles");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        CircularImageView cimg = (CircularImageView) findViewById(R.id.imgTwarz);
        cimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 666);
            }
        }
        );
        Bitmap sup = new ImageSaver(getApplicationContext()).
                setFileName("twarz.png").
                setDirectoryName("images").load(2);
        if (sup != null) {
            cimg.setImageBitmap(sup);
        }
        sortArticlesByWynik(articles, 3);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        graphVisiblity(true);
        updateMeme(spZawodnik, spKlub);
    }

    public void graphVisiblity(boolean state) {
        // state false - invisible, true - visible
        if (state) {
            (findViewById(R.id.layIdk)).setVisibility(GONE);
            (findViewById(R.id.layProblemy)).setVisibility(VISIBLE);
        } else {
            (findViewById(R.id.layIdk)).setVisibility(VISIBLE);
            (findViewById(R.id.layProblemy)).setVisibility(GONE);
        }
    }
    public void updateMeme(final String spZawodnik, final String spKlub) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                graphVisiblity(true);
                tabeleVisiblity(true);
                updateNavBar();
                updateStats(spZawodnik, spKlub);
                updateTabele(spZawodnik);
                try {
                    updateGraph(R.id.graphWynik, 0, spZawodnik);
                    updateGraph(R.id.graphPelne, 1, spZawodnik);
                    updateGraph(R.id.graphZbierane, 2, spZawodnik);
                    updateGraph(R.id.graphDziury, 3, spZawodnik);
                } catch (Exception e) {
                    e.printStackTrace();
                    graphVisiblity(false);
                }
                if(articles.size() < 2) {
                    graphVisiblity(false);
                }
            }});
        thread.run();
    }
    public void sortArticlesByWynik(ArrayList<Article> art, final int tryb) {

        Collections.sort(art, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                try {
                    if (tryb == 1) {
                        return ((Integer) Integer.parseInt(o1.getWynik())).compareTo(Integer.parseInt(o2.getWynik()));
                    } else if (tryb == 2) {
                        return ((Integer) Integer.parseInt(o2.getWynik())).compareTo(Integer.parseInt(o1.getWynik()));
                    } else if (tryb == 3) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        return sdf.parse(proDate(o1.getData())).compareTo(sdf.parse(proDate(o2.getData())));
                    } else if (tryb == 4) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        return sdf.parse(proDate(o2.getData())).compareTo(sdf.parse(proDate(o1.getData())));
                    } else {
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

    }
    /*
    Yes, I have just copypasted 2 functions from MainActivity
    there is probably a better way such a creating a seperate class and making 2 instances of it
    (1 per activity)
    it's way more elegant etc
    but i'm simply too lazy to do it
     */

    public String proDate(String d1) {
        String[] a1 = d1.split("-");
        String rt = "";
        rt += a1[0] + "-";
        if (a1[1].length() == 1) { rt += "0"+a1[1]; } else { rt += a1[1]; } rt += "-";
        if (a1[2].length() == 1) { rt += "0"+a1[2]; } else { rt += a1[2]; }
        return rt;
    }
    public void updateGraph(final Integer grp, Integer sortBy, String zawodnik) throws ParseException, IndexOutOfBoundsException {
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean showNorma = SP.getBoolean("norma", true);
            ArrayList<DataPoint> grArr = new ArrayList<DataPoint>();
            ArrayList<DataPoint> grNorma = new ArrayList<DataPoint>();
            GraphView graph = (GraphView) findViewById(grp);

            graph.removeAllSeries();
            int minX1 = -1;
            int maxX1 = -1;
            int minY1 = -1;
            int maxY1 = -1;
            for (int i = 0; i < articles.size(); i++) {
                Article art = articles.get(i);
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-DD", Locale.ENGLISH);
                Date dat = sd.parse(art.getData());
                int coolstuff = 0;
                int norma = 0;
                if (sortBy == 0) { // wynik
                    coolstuff = Integer.parseInt(art.getWynik());
                    norma = Integer.parseInt(SP.getString("normaWynik", "9999"));
                } else if (sortBy == 1) {
                    coolstuff = Integer.parseInt(art.getPelne());
                    norma = Integer.parseInt(SP.getString("normaPelne", "9999"));
                } else if (sortBy == 2) {
                    coolstuff = Integer.parseInt(art.getZbierane());
                    norma = Integer.parseInt(SP.getString("normaZbierane", "9999"));
                } else if (sortBy == 3) {
                    coolstuff = Integer.parseInt(art.getDziury());
                }
                if (zawodnik.equals("Domyślny Zawodnik")) {
                    throw new IndexOutOfBoundsException();
                } else if (art.getZawodnik().equals(zawodnik)){
                    grArr.add(new DataPoint(i, coolstuff));
                    maxX1 = i;
                    if (minX1 == -1) {
                        minX1 = i;
                    }
                    if (coolstuff > maxY1) {
                        maxY1 = coolstuff;
                    }
                    if ((coolstuff < minY1) || (minY1 == -1)) {
                        minY1 = coolstuff;
                    }
                }
                if (art.getZawodnik().equals(zawodnik) && (norma != 0) && showNorma && (norma != 9999)) {
                    grNorma.add(new DataPoint(i, norma));
                }
                if ((norma != 0) && showNorma && (norma != 9999)) {
                    if (norma > maxY1) {
                        maxY1 = norma;
                    }
                    if ((norma < minY1) || (minY1 == -1)) {
                        minY1 = norma;
                    }
                }
            }


            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(((DataPoint[]) grArr.toArray(new DataPoint[grArr.size()])));
            graph.addSeries(series);
            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    int i = (int) dataPoint.getX();
                    Intent intent;
                    intent = new Intent(StatystykiActivity.this, WynikActivity.class);
                    intent.putExtra("wynik", articles.get(i));
                    intent.putExtra("disabled", true);
                    startActivityForResult(intent, 2);
                }
            });
            if ((!grNorma.isEmpty()) && showNorma) {
                LineGraphSeries<DataPoint> seriesNorma = new LineGraphSeries<>(((DataPoint[]) grNorma.toArray(new DataPoint[grNorma.size()])));
                seriesNorma.setColor(Color.RED);
                graph.addSeries(seriesNorma);
            }


            graph.getViewport().setScalable(true);
            graph.getViewport().setScrollable(true);
            graph.getViewport().setScalableY(true);
            graph.getViewport().setScrollableY(true);


            graph.getGridLabelRenderer().setHumanRounding(true);
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(minX1);
            graph.getViewport().setMaxX(maxX1);
            graph.getViewport().setMinY(minY1);
            graph.getViewport().setMaxY(maxY1);
            Log.v("minx", Integer.toString(minX1));
        Log.v("maxx", Integer.toString(maxX1));
        Log.v("miny", Integer.toString(minY1));
        Log.v("maxy", Integer.toString(maxY1));


            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    try {
                        if (isValueX) {
                            int val = (int) value;
                            String a = articles.get(val).getData();
                            super.formatLabel(value, isValueX);
                            return a;
                        } else {
                            return super.formatLabel(value, isValueX);
                        }
                    } catch (Exception e) {
                        ((GraphView) findViewById(grp)).setVisibility(GONE);
                    }
                    return "";
                }
            });

    }
    public void updateNavBar() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String spKlub = SP.getString("klub","KK Dziewiątka-Amica Wronki");
        String spZawodnik = SP.getString("zawodnik","Domyślny Zawodnik");

        if (spKlub.equals("1")) {
            spKlub = "KK Ustaw Klub w Opcjach";
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View hView =  navigationView.getHeaderView(0); // in case of null pointer reference
        TextView navZawodnik = (TextView)hView.findViewById(R.id.navZawodnik); // remember about
        TextView navKlub = (TextView)hView.findViewById(R.id.navKlub); //hView.findviewbyid
        navZawodnik.setText(spZawodnik);
        navKlub.setText(spKlub);

        //CircularImageView cimg = (CircularImageView) hView.findViewById(R.id.navTwarz);
       // File dir = getApplicationContext().getDir("images", Context.MODE_PRIVATE);
        Bitmap sup = new ImageSaver(getApplicationContext()).
                setFileName("twarz.png").
                setDirectoryName("images").load(8);
        if (sup != null) {
            CircularImageView twrz = (CircularImageView) hView.findViewById(R.id.navTwarz);
            twrz.setImageBitmap(sup);
        }
        sup = null;
        System.gc();
    }

    int dptopx(int px) {
        return ((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                px,
                getApplicationContext().getResources().getDisplayMetrics()
        ));}

    public void zmienLayout(boolean jestObrazek) {
        CircularImageView imgTwarz = (CircularImageView) findViewById(R.id.imgTwarz);
        View rect = (View) findViewById(R.id.rectangle_at_the_top);
        ViewGroup.MarginLayoutParams lRect = (ViewGroup.MarginLayoutParams) ((rect).getLayoutParams());
        if (jestObrazek) {
            imgTwarz.setVisibility(View.VISIBLE);
            lRect.bottomMargin = dptopx(72);
            lRect.height = dptopx(195);
        } else {
            imgTwarz.setVisibility(View.GONE);
            lRect.bottomMargin = dptopx(16);
            lRect.height = dptopx(130);
        }
    }

    public Integer[] articlesToStats(ArrayList<Article> arts,
                                              Integer npelne, Integer nzbierane, Integer nwynik) {
        Integer najW = 0;
        Integer najgW = 9999;
        Integer abvnWynik = 0;
        Integer abvnPelne = 0;
        Integer abvnZbierane = 0;
        Integer n = 0;
        Integer aWynik = 0;
        Integer aPelne = 0;
        Integer aZbierane = 0;
        Integer aDziury = 0;

        for (Article a : arts) {
            Integer tWynik = Integer.valueOf(a.getWynik());
            Integer tPelne = Integer.valueOf(a.getPelne());
            Integer tZbierane = Integer.valueOf(a.getZbierane());
            Integer tDziury = Integer.valueOf(a.getDziury());
            n += 1;
            if (tWynik > najW)  { najW = tWynik;  }
            if (tWynik < najgW) { najgW = tWynik; }
            aWynik += tWynik;
            aPelne += tPelne;
            aZbierane += tZbierane;
            aDziury += tDziury;
            if (tWynik >= nwynik)       {abvnWynik += 1;   }
            if (tPelne >= npelne)       {abvnPelne += 1;   }
            if (tZbierane >= nzbierane) {abvnZbierane += 1;}
        }
        aWynik = aWynik / n;
        aPelne = aPelne / n;
        aZbierane = aZbierane / n;
        aDziury = aDziury / n;

        Integer[] fun = new Integer[]{
                najW, najgW, n, abvnWynik, abvnPelne, abvnZbierane,
                aWynik, aPelne, aZbierane, aDziury
        };
        return fun;
    }

    public void updateTabele(String spZawodnik) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean showNorma = SP.getBoolean("norma", true);
        boolean showSezon = SP.getBoolean("sezon", true);
        ArrayList<Article> artykuly = new ArrayList<Article>();
        tabeleSezon(showSezon);
        if (showSezon) {
            for (Article x : articles) {
                if (x.obecnySezon()) {
                    artykuly.add(x);
                    Log.v("sezon", "obecny");
                }
            }
        } else {
            artykuly = articles;
        }
        if (!artykuly.isEmpty()) {
            if (artykuly.get(0).wynikiFromZawodnik(artykuly, spZawodnik).size() < 1) {
                tabeleVisiblity(false);
                return;
            }
        } else {tabeleVisiblity(false); return;}


        int normaW = Integer.parseInt(SP.getString("normaWynik", "9999"));
        int normaP = Integer.parseInt(SP.getString("normaPelne", "9999"));
        int normaZ = Integer.parseInt(SP.getString("normaZbierane", "9999"));
        Integer[] staty = articlesToStats(
                artykuly.get(0).wynikiFromZawodnik(artykuly, spZawodnik),
                normaP, normaZ, normaW
        );
        Integer[] odpowiedniki = new Integer[]{
                R.id.statNajlepszy, R.id.statNajgorszy, R.id.statIlosc, R.id.statNrmWynik,
                R.id.statNrmPelne, R.id.statNrmZbierane, R.id.statSrWynik,
                R.id.statSrPelne, R.id.statSrZbierane, R.id.statSrDziury
        };
        Integer a = 0;
        for (Integer address : odpowiedniki) {
            ((TextView) findViewById(address)).setText(Integer.toString(staty[a]));
            a += 1;
        }
        findViewById(R.id.statLayNorma).setVisibility(showNorma ? VISIBLE : GONE);
    }

    public void tabeleVisiblity(boolean state) {
        // state false - invisible, true - visible
        if (state) {
            (findViewById(R.id.statLayMain)).setVisibility(VISIBLE);
            (findViewById(R.id.statLayBez)).setVisibility(GONE);
        } else {
            (findViewById(R.id.statLayMain)).setVisibility(GONE);
            (findViewById(R.id.statLayBez)).setVisibility(VISIBLE);
        }
    }
    public void tabeleSezon(boolean state) {
        // state false - wszystko, true - tylko sezon
        TextView toset = (TextView) findViewById(R.id.txtPusteTabele);
        TextView statokres = (TextView) findViewById(R.id.statOkres);
        if (state) {
            int irlyear = Calendar.getInstance().get(Calendar.YEAR);
            int irlmonth = Calendar.getInstance().get(Calendar.MONTH)+1;
            toset.setText(
                    "Na razie nie ma tu żadnych statystyk - potrzebny jest minimum jeden wynik w obecnym sezonie! (aby widzieć wszystkie, zmień ustawienia)"
            );
            String elo = "Sezon ";
            if (irlmonth < 8) {
                elo += Integer.toString(irlyear-1)+"/"+Integer.toString(irlyear);
            } else {
                elo += Integer.toString(irlyear)+"/"+Integer.toString(irlyear+1);
            }
            statokres.setText(elo);
        } else {
            toset.setText(
                    "Na razie nie ma tu żadnych statystyk - potrzebny jest minimum jeden wynik!"
            );
            statokres.setText("Wszystkie wyniki");
        }
    }
    public void updateStats(String spZawodnik, String spKlub) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean toja = spZawodnik.equals(SP.getString("zawodnik", "Zawodnik Domyślny"));
        zmienLayout(toja);

        TextView statZawodnik = (TextView)findViewById(R.id.statZawodnik);
        TextView statKlub = (TextView)findViewById(R.id.statKlub);
        statZawodnik.setText(spZawodnik);
        if (toja) {
            String spK = SP.getString("klub","KK Dziewiątka-Amica Wronki");
            if (spK.equals("1")) {spK = "KK Ustaw Klub w Opcjach";}
            statKlub.setText(spK);
        } else {
            statKlub.setText(spKlub);
        }

        if (!articles.isEmpty()) {
            if (articles.get(0).wynikiFromZawodnik(articles, spZawodnik).size() < 2) {
                graphVisiblity(false);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == 3) { // aktualizacja ustawien
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            spZawodnik = SP.getString("zawodnik","Domyślny Zawodnik");
            spKlub = SP.getString("klub", "KK Dziewiątka-Amica Wronki");
            if (spKlub.equals("1")) {
                spKlub = "KK Ustaw Klub w Opcjach";
            }
            updateTabele(spZawodnik);
            if (!articles.isEmpty()) {
                ArrayList<Article> zawWyniki = new ArrayList<Article>();
                zawWyniki.addAll(articles.get(0).wynikiFromZawodnik(articles, spZawodnik));
                if (zawWyniki.size() > 1) {
                    updateMeme(spZawodnik, spKlub);
                    selectSpinnerValue(spinner, spZawodnik);
                }
            }


        }
        if (requestCode == 666 && resultCode == RESULT_OK && null != data) {
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    final Bitmap[] bmp = {null};
                    final Uri selectedImage = data.getData();

                    Glide.with(StatystykiActivity.this)
                            .load(selectedImage)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>(400,400) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                    bmp[0] = resource; // Possibly runOnUiThread()
                                    CircularImageView cimg = (CircularImageView) findViewById(R.id.imgTwarz);
                                    cimg.setImageBitmap(bmp[0]);
                                    new ImageSaver(getApplicationContext()).
                                            setFileName("twarz.png").
                                            setDirectoryName("images").
                                            save(bmp[0]);
                                    updateNavBar();
                                }
                            });


                    bmp[0] = null;
                    System.gc();
                }});
            thread.run();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    private void selectSpinnerValue(Spinner spinner, String myString)
    {
        int index = 0;
        for(int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equals(myString)){
                spinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Intent i = getIntent();
        ArrayList<Article> optArticles = (ArrayList<Article>)i.getSerializableExtra("articles");
        getMenuInflater().inflate(R.menu.stat_menu, menu);
        MenuItem item = menu.findItem(R.id.action_zawodnik);
        spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayList<String> zawodnicy = new ArrayList<String>();
        if (!optArticles.isEmpty()) {
            zawodnicy.addAll(optArticles.get(0).zawodnicyFromArray(optArticles));
        }
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, zawodnicy
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String zawodnik = parent.getItemAtPosition(pos).toString();
                spZawodnik = zawodnik;
                spKlub = articles.get(0).klubFromZawodnik(articles, spZawodnik);
                updateMeme(spZawodnik, spKlub);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        selectSpinnerValue(spinner, spZawodnik);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, MyPreferencesActivity.class);
            startActivityForResult(i, 3);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_wyniki) {
            // Handle the camera action
            Intent i = new Intent(StatystykiActivity.this, MainActivity.class);
            finish();  //Kill the activity from which you will go to next activity
            startActivity(i);
        } else if (id == R.id.nav_statystyki) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
