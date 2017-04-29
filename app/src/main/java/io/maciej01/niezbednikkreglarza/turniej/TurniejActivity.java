package io.maciej01.niezbednikkreglarza.turniej;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.maciej01.niezbednikkreglarza.Article;
import io.maciej01.niezbednikkreglarza.ImageSaver;
import io.maciej01.niezbednikkreglarza.KontaktActivity;
import io.maciej01.niezbednikkreglarza.MainActivity;
import io.maciej01.niezbednikkreglarza.MyPreferencesActivity;
import io.maciej01.niezbednikkreglarza.NarzedziaActivity;
import io.maciej01.niezbednikkreglarza.R;
import io.maciej01.niezbednikkreglarza.StatystykiActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Maciej on 2017-04-25.
 */

public class TurniejActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public TurniejList turnieje;
    public TurniejAdapter adapter;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.lapp1).setVisibility(GONE);
        findViewById(R.id.lapp2).setVisibility(GONE);
        findViewById(R.id.lapp3).setVisibility(GONE);
        findViewById(R.id.lapp4).setVisibility(GONE);
        findViewById(R.id.lapp5).setVisibility(VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TurniejActivity.this, TEditActivity.class);
                i.putExtra("lista", turnieje);
                i.putExtra("rodzaj", "nowy");
                startActivityForResult(i, 666);
            }
        });
        fab.setVisibility(VISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateNavBar();

        Intent i = getIntent();
        boolean mainact = (boolean) i.getSerializableExtra("main");
        if (mainact) {
            ArrayList<Article> arts = (ArrayList<Article>) i.getSerializableExtra("articles");
            turnieje = new TurniejList(arts);
        } else {
            turnieje = new TurniejList();
        }
        recyclerView = (RecyclerView) findViewById(R.id.zawodziarka);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        turnieje.load();
        adapter = new TurniejAdapter(turnieje, recyclerView, this);
        recyclerView.setAdapter(adapter);
        if (adapter.getItemCount() == 0) {
            ((TextView) findViewById(R.id.txtPustaZawodziarka)).setVisibility(VISIBLE);
        } else {
            ((TextView) findViewById(R.id.txtPustaZawodziarka)).setVisibility(GONE);
        }

    }

    public void updateNavBar() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String spKlub = SP.getString("klub",getString(R.string.team_default));
        String spZawodnik = SP.getString("zawodnik",getString(R.string.player_default));

        if (spKlub.equals("1")) {
            spKlub = getString(R.string.team_default);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View hView =  navigationView.getHeaderView(0); // in case of null pointer reference
        TextView navZawodnik = (TextView)hView.findViewById(R.id.navZawodnik); // remember about
        TextView navKlub = (TextView)hView.findViewById(R.id.navKlub); //hView.findviewbyid
        navZawodnik.setText(spZawodnik);
        navKlub.setText(spKlub);

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

    public void losoweWyniki() {
        for (int i = 0; i < 5; ++i) {
            turnieje.add(new Turniej(turnieje, true));
        }
    }

    public void openTurniej(Turniej t) {
        Intent i = new Intent(TurniejActivity.this, TElementActivity.class);
        i.putExtra("turniej", t);
        startActivityForResult(i, 123);
    }

    public int dajmicyferke(Turniej a) {
        for (int i = 0; i < turnieje.size(); i++) {
            if (turnieje.get(i).equals(a)) {
                return i;
            }
        }
        return 666;
    }
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == 3) { // aktualizacja ustawien
            updateNavBar();
        } else if (requestCode == 123) {
            Turniej scores = (Turniej) data.getSerializableExtra("wynik");
            Turniej scores_old = (Turniej) data.getSerializableExtra("wynik_old");
            boolean seppuku = (boolean) data.getSerializableExtra("dead");
            if (seppuku) {
                int index = dajmicyferke(scores_old);
                turnieje.remove(index);
                adapter.notifyItemRemoved(index);
                if (adapter.getItemCount() == 0) {
                    ((TextView) findViewById(R.id.txtPustaZawodziarka)).setVisibility(View.VISIBLE);
                } else {
                    ((TextView) findViewById(R.id.txtPustaZawodziarka)).setVisibility(View.GONE);
                }
                return;
            }
            if (scores.equals(scores_old)) {
                Log.e("komentarz", "taki sam");
            } else {
                // inne
                int index = dajmicyferke(scores_old);
                turnieje.set(index, scores);
                adapter.notifyItemChanged(index);
                Log.e("komentarz", "inny wynik");
                scores.save();
                recyclerView.smoothScrollToPosition(index);
            }
        } else if (requestCode == 666) { // nowe do dodania
            if (resultCode == RESULT_OK) {
                Turniej scores = (Turniej) data.getSerializableExtra("wynik");
                turnieje.add(0, scores);
                adapter.notifyItemInserted(0);
                recyclerView.smoothScrollToPosition(0);
            }
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
        getMenuInflater().inflate(R.menu.only_debug, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, MyPreferencesActivity.class);
            startActivityForResult(i, 3);
            return true;
        } else if (id == R.id.action_losowe) {
            losoweWyniki();
            adapter.notifyDataSetChanged();
            ((TextView) findViewById(R.id.txtPustaZawodziarka)).setVisibility(View.GONE);
        } else if (id == R.id.action_usun) {
            turnieje.clear();
            adapter.notifyDataSetChanged();
            ((TextView) findViewById(R.id.txtPustaZawodziarka)).setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TurniejHolder.deleteAll(TurniejHolder.class); // :^^^))))

        for (Turniej a : turnieje.getLista()) {
            a.save();
            Log.e("more", "savedturniej");
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_wyniki) {
            // Handle the camera action
            Intent i = new Intent(TurniejActivity.this, MainActivity.class);
            finish();  //Kill the activity from which you will go to next activity
            startActivity(i);
        } else if (id == R.id.nav_statystyki) {
            Intent i = new Intent(TurniejActivity.this, StatystykiActivity.class);
            ArrayList<Article> articles = (ArrayList<Article>) Article.listAll(Article.class);
            i.putExtra("articles", articles);
            finish();  //Kill the activity from which you will go to next activity
            startActivity(i);
        } else if (id == R.id.nav_ustawienia) {
            Intent i = new Intent(TurniejActivity.this, MyPreferencesActivity.class);
            startActivityForResult(i, 3);
            return true;
        } else if (id == R.id.nav_aplikacja) {
            Intent i = new Intent(TurniejActivity.this, KontaktActivity.class);
            finish();  //Kill the activity from which you will go to next activity
            startActivity(i);
        } else if (id == R.id.nav_turnieje) {
        } else if (id == R.id.nav_narzedzia) {
            Intent i = new Intent(TurniejActivity.this, NarzedziaActivity.class);
            finish();
            startActivityForResult(i, 2001);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}

