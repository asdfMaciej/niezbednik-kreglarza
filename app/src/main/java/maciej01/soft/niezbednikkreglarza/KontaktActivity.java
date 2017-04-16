package maciej01.soft.niezbednikkreglarza;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Maciej on 2017-04-14.
 */

public class KontaktActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.lapp1).setVisibility(GONE);
        findViewById(R.id.lapp2).setVisibility(GONE);
        findViewById(R.id.lapp3).setVisibility(VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        fab.setVisibility(GONE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView t2 = (TextView) findViewById(R.id.kontLink1);
        t2.setMovementMethod(LinkMovementMethod.getInstance());
        t2 = (TextView) findViewById(R.id.kontLink2);
        t2.setMovementMethod(LinkMovementMethod.getInstance());

        ImageView paypal = (ImageView) findViewById(R.id.kontPaypal);
        paypal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://paypal.me/Kaszkowiak"));
                startActivity(browserIntent);
            }
        });
        updateNavBar();
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


    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == 3) { // aktualizacja ustawien
            updateNavBar();
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
        getMenuInflater().inflate(R.menu.only_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
            Intent i = new Intent(KontaktActivity.this, MainActivity.class);
            finish();  //Kill the activity from which you will go to next activity
            startActivity(i);
        } else if (id == R.id.nav_statystyki) {
            Intent i = new Intent(KontaktActivity.this, StatystykiActivity.class);
            finish();  //Kill the activity from which you will go to next activity
            startActivity(i);
        } else if (id == R.id.nav_ustawienia) {
            Intent i = new Intent(KontaktActivity.this, MyPreferencesActivity.class);
            startActivityForResult(i, 3);
            return true;
        } else if (id == R.id.nav_aplikacja) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
