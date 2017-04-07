package maciej01.soft.niezbednikkreglarza;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import static android.view.View.GONE;

/**
 * Created by Maciej on 2017-04-03.
 */

public class StatystykiActivity  extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Serializable {

    public ArrayList<Article> articles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        //getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        //Slide s = new Slide();
        //s.setInterpolator(bi);
        //getWindow().setEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.activity_slide));//new Explode());
        //getWindow().setExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.activity_slide));//(new Explode());
        //setupWindowAnimations();
        setContentView(R.layout.activity_main);
        findViewById(R.id.lapp1).setVisibility(GONE);
        findViewById(R.id.lapp2).setVisibility(View.VISIBLE);

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
        updateMeme();
    }

    public void updateMeme() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                updateNavBar();
                ((GraphView) findViewById(R.id.graphWynik)).setVisibility(View.VISIBLE);
                ((GraphView) findViewById(R.id.graphPelne)).setVisibility(View.VISIBLE);
                ((GraphView) findViewById(R.id.graphZbierane)).setVisibility(View.VISIBLE);
                ((GraphView) findViewById(R.id.graphDziury)).setVisibility(View.VISIBLE);
                try {
                    updateGraph(R.id.graphWynik, 0);
                    updateGraph(R.id.graphPelne, 1);
                    updateGraph(R.id.graphZbierane, 2);
                    updateGraph(R.id.graphDziury, 3);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof IndexOutOfBoundsException) {
                        ((GraphView) findViewById(R.id.graphWynik)).setVisibility(GONE);
                        ((GraphView) findViewById(R.id.graphPelne)).setVisibility(GONE);
                        ((GraphView) findViewById(R.id.graphZbierane)).setVisibility(GONE);
                        ((GraphView) findViewById(R.id.graphDziury)).setVisibility(GONE);
                    }
                }
                updateStats();
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
    public void updateGraph(final Integer grp, Integer sortBy) throws ParseException, IndexOutOfBoundsException {
        try {
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean showNorma = SP.getBoolean("norma", true);
            ArrayList<DataPoint> grArr = new ArrayList<DataPoint>();
            ArrayList<DataPoint> grNorma = new ArrayList<DataPoint>();
            GraphView graph = (GraphView) findViewById(grp);

            graph.removeAllSeries();
            for (int i = 0; i < articles.size(); i++) {
                Article art = articles.get(i);
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-DD", Locale.ENGLISH);
                Date dat = sd.parse(art.getData());
                int coolstuff = 0;
                int norma = 0;
                if (sortBy == 0) { // wynik
                    coolstuff = Integer.parseInt(art.getWynik());
                    norma = Integer.parseInt(SP.getString("normaWynik", "666"));
                } else if (sortBy == 1) {
                    coolstuff = Integer.parseInt(art.getPelne());
                    norma = Integer.parseInt(SP.getString("normaPelne", "66"));
                } else if (sortBy == 2) {
                    coolstuff = Integer.parseInt(art.getZbierane());
                    norma = Integer.parseInt(SP.getString("normaZbierane", "6"));
                } else if (sortBy == 3) {
                    coolstuff = Integer.parseInt(art.getDziury());
                }
                grArr.add(new DataPoint(i, coolstuff));
                if ((norma != 0) && showNorma) {
                    grNorma.add(new DataPoint(i, norma));
                }
                Log.v("data", Integer.toString((int) dat.getTime()));
                Log.v("test", art.getWynik());
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


            graph.getGridLabelRenderer().setHumanRounding(false);
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);

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
        } catch (IndexOutOfBoundsException e) {
            ((GraphView) findViewById(grp)).setVisibility(GONE);
        }
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
    public void updateStats() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String spKlub = SP.getString("klub","KK Dziewiątka-Amica Wronki");
        String spZawodnik = SP.getString("zawodnik","Domyślny Zawodnik");

        if (spKlub.equals("1")) {
            spKlub = "KK Ustaw Klub w Opcjach";
        }
        TextView statZawodnik = (TextView)findViewById(R.id.statZawodnik);
        TextView statKlub = (TextView)findViewById(R.id.statKlub);

        statZawodnik.setText(spZawodnik);
        statKlub.setText(spKlub);
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == 3) { // aktualizacja ustawien
            updateMeme();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stat_menu, menu);
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
