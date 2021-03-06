package io.maciej01.niezbednikkreglarza;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by Maciej on 2017-03-22.
 */

public class ZmianaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public Article scores = new Article("");

    public ZmianaSwipe zs = new ZmianaSwipe();
    public class ZmianaSwipe extends ActivitySwipeDetector {

        public ZmianaSwipe() {
            super();
        }

        @Override
        public final void onLeftToRightSwipe() {
            super.onLeftToRightSwipe();
            onBackPressed();
        }
    }

    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        //getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        //getWindow().setEnterTransition(new Explode());
        //getWindow().setExitTransition(new Explode());
        setContentView(R.layout.zmiana_layout);
        Intent i = getIntent();
        String rodzaj = (String)i.getSerializableExtra("rodzaj");

        Calendar myCalendar = Calendar.getInstance();
        int year = myCalendar.get(Calendar.YEAR);
        int month = myCalendar.get(Calendar.MONTH);
        int day = myCalendar.get(Calendar.DAY_OF_MONTH);
        if (rodzaj.equals("zmiana")) {
            scores = (Article) i.getSerializableExtra("wynik");
            String sw = scores.getData();
            String[] a1 = sw.split("-");
            year = Integer.valueOf(a1[0]);
            month = Math.abs(Integer.valueOf(a1[1])-1); // it's index
            day = Integer.valueOf(a1[2]);
        }
        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, ZmianaActivity.this, year, month, day);

        final ArrayAdapter<CharSequence> adapterKlub = ArrayAdapter.createFromResource(this,
                R.array.kluby, android.R.layout.simple_spinner_item);

        final ArrayAdapter<CharSequence> adapterKregielnia = ArrayAdapter.createFromResource(this,
                R.array.kregielnie, android.R.layout.simple_spinner_item);

        final ArrayAdapter<CharSequence> adapterKategoria = ArrayAdapter.createFromResource(this,
                R.array.kategorie, android.R.layout.simple_spinner_item);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String spKlub = SP.getString("klub","KK Dziewiątka-Amica Wronki");
        String spZawodnik = SP.getString("zawodnik","");
        String spKregielnia = SP.getString("kregielnia","Kręgielnia Dziewiątka-Amica Wronki");
        String spKategoria = SP.getString("kategoria","Chłopcy - U10 M");


                ((Button) findViewById(R.id.btData))
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                datePickerDialog.show();
                            }
                        });

                Spinner spinnerKlub = (Spinner) findViewById(R.id.spnKlub);

                adapterKlub.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerKlub.setAdapter(adapterKlub);
                /*spinnerKlub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String item = parent.getItemAtPosition(position).toString();
                        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {}
                });*/

                Spinner spinnerKregielnia = (Spinner) findViewById(R.id.spnKregielnia);


                adapterKregielnia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerKregielnia.setAdapter(adapterKregielnia);

                /*spinnerKregielnia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String item = parent.getItemAtPosition(position).toString();
                        //Toast.makeText(parent.getContext(), "Kregielnia: " + item, Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {}
                });*/

        Spinner spinnerKategoria = (Spinner) findViewById(R.id.spnKategoria);


        adapterKategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKategoria.setAdapter(adapterKategoria);

        spinnerKlub.setSelection(getIndex(spinnerKlub, spKlub));
        spinnerKregielnia.setSelection(getIndex(spinnerKregielnia, spKregielnia));
        spinnerKategoria.setSelection(getIndex(spinnerKategoria, spKategoria));
        ((CheckBox) findViewById(R.id.button2)).setChecked(false);
        ((EditText)findViewById(R.id.edZawodnik)).setText(spZawodnik);
                if (rodzaj.equals("zmiana")) {
                    scores = (Article) i.getSerializableExtra("wynik");
                    spinnerKlub.setSelection(adapterKlub.getPosition(scores.getKlub()));
                    spinnerKregielnia.setSelection(adapterKregielnia.getPosition(scores.getKregielnia()));
                    spinnerKategoria.setSelection(adapterKategoria.getPosition(scores.getKategoria()));
                    ((EditText)findViewById(R.id.edWynik)).setText(scores.getWynik());
                    ((EditText)findViewById(R.id.edPelne)).setText(scores.getPelne());
                    ((EditText)findViewById(R.id.edZbierane)).setText(scores.getZbierane());
                    ((EditText)findViewById(R.id.edDziury)).setText(scores.getDziury());
                    ((EditText)findViewById(R.id.edZawodnik)).setText(scores.getZawodnik());
                    ((CheckBox) findViewById(R.id.button2)).setChecked(scores.czyTory());

                    if (scores.czyTory()) {
                        Integer[] torki = {
                                R.id.edWynik1, R.id.edPelne1, R.id.edZbierane1, R.id.edDziury1,
                                R.id.edWynik2, R.id.edPelne2, R.id.edZbierane2, R.id.edDziury2,
                                R.id.edWynik3, R.id.edPelne3, R.id.edZbierane3, R.id.edDziury3,
                                R.id.edWynik4, R.id.edPelne4,  R.id.edZbierane4, R.id.edDziury4
                        };
                        String[][] tory = scores.getTory();
                        String[] tory2d = new String[16];
                        int ff = 0;
                        for (String[] tor : tory) {
                            for (String xD : tor) {
                                tory2d[ff] = xD;
                                ff += 1;
                            }
                        }
                        ff = 0;
                        for (String tor : tory2d) {
                            EditText element = (EditText) findViewById(torki[ff]);
                            //String[] pythonowasztuczka = {"", "P: ", "Z: ", "X: "};
                            element.setText(tor);//pythonowasztuczka[ff%4]+
                            ff += 1;
                        }
                    } else {

                    }

                } else {
                    //scores = new Article("");
                    //Toast.makeText(getApplicationContext(), rodzaj, Toast.LENGTH_LONG).show();
                }
                //return;
            //}

        //});
        //thread.start();
        Button btZap = (Button) findViewById(R.id.btZapisz);
        btZap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zapisz();
            }
        });

        TextWatcher textlistener = new TextWatcher() {
            EditText wynik = ((EditText)findViewById(R.id.edWynik));
            EditText pelne = ((EditText)findViewById(R.id.edPelne));
            EditText zbierane = ((EditText)findViewById(R.id.edZbierane));

            @Override
            public void afterTextChanged(Editable s) {
                String val1 = pelne.getText().toString();
                String val2 = zbierane.getText().toString();
                Integer value1, value2;
                if (val1.isEmpty()){value1=0;}else{value1=Integer.valueOf(val1);}
                if (val2.isEmpty()){value2=0;}else{value2=Integer.valueOf(val2);}
                String sum = Integer.toString(value1 + value2);
                wynik.setText(sum);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(final CharSequence s, int start, int before, int count){}
        };
        ((EditText)findViewById(R.id.edPelne)).addTextChangedListener(textlistener);
        ((EditText)findViewById(R.id.edZbierane)).addTextChangedListener(textlistener);

    }

    //@Override
    //public boolean dispatchTouchEvent(MotionEvent ev){
    //    super.dispatchTouchEvent(ev);
    //    return zs.onTouch(ev);
    //}
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        scores.ustawData(year, month, dayOfMonth);
    }

    public void backAnimation() {
        boolean b = (boolean) getIntent().getSerializableExtra("swiped");
        if (b) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.return_);
        builder.setMessage(R.string.dialog_return);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //ZmianaActivity.super.onBackPressed();
                zapisz();
            }
        });
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ZmianaActivity.super.onBackPressed();
                backAnimation();
            }
        });
        builder.show();

    }

    public String[][] ttd(Integer[] torki) {
        Integer[] tor1 = Arrays.copyOfRange(torki, 0, 4);
        Integer[] tor2 = Arrays.copyOfRange(torki, 4, 8);
        Integer[] tor3 = Arrays.copyOfRange(torki, 8, 12);
        Integer[] tor4 = Arrays.copyOfRange(torki, 12, 16);

        String[][] tory = new String[4][4];
        Integer[][] toryInt = {tor1, tor2, tor3, tor4};

        Integer z = 0;
        for (Integer[] x : toryInt) {
            String[] temp = new String[4];
            Integer i = 0;
            for (Integer tort : x) {
                EditText tortemp = (EditText) findViewById(tort);
                temp[i] = tortemp.getText().toString();
                i += 1;
            }
            tory[z] = temp;
            z += 1;
        }

        return tory;

    }

    public boolean zapisz() {
        boolean correct = true;

        EditText zwd = (EditText) findViewById(R.id.edZawodnik);
        EditText wnk = (EditText) findViewById(R.id.edWynik);
        EditText pln = (EditText) findViewById(R.id.edPelne);
        EditText zbr = (EditText) findViewById(R.id.edZbierane);
        EditText dzi = (EditText) findViewById(R.id.edDziury);

        Spinner klb = (Spinner) findViewById(R.id.spnKlub);
        Spinner krg = (Spinner) findViewById(R.id.spnKregielnia);
        Spinner ktg = (Spinner) findViewById(R.id.spnKategoria);

        CheckBox tor = (CheckBox) findViewById(R.id.button2);
        View torerr = null;
        EditText[] lista = {zwd, wnk, pln, zbr, dzi};
        for (EditText item : lista) {
            if (TextUtils.isEmpty(item.getText().toString())) {
                item.setError(getString(R.string.error_field));
                torerr = item;
                correct = false;
            }
        }
        if (scores.getData() == "") {
            ((Button) findViewById(R.id.btData)).setError(getString(R.string.error_data));
            torerr = ((Button) findViewById(R.id.btData));
            correct = false;
        }
        if (tor.isChecked()) {
            Integer[] torki = {
                    R.id.edWynik1, R.id.edPelne1, R.id.edZbierane1, R.id.edDziury1,
                    R.id.edWynik2, R.id.edPelne2, R.id.edZbierane2, R.id.edDziury2,
                    R.id.edWynik3, R.id.edPelne3, R.id.edZbierane3, R.id.edDziury3,
                    R.id.edWynik4, R.id.edPelne4, R.id.edZbierane4, R.id.edDziury4
            };
            for (Integer item : torki) {
                EditText tortemp = (EditText) findViewById(item);
                tortemp.setError(null);
                tortemp.clearFocus();
                if (TextUtils.isEmpty(tortemp.getText().toString())) {
                    tortemp.setError(getString(R.string.error_field));
                    torerr = tortemp;
                    correct = false;
                }
            }
            if (!correct) {
                final ScrollView sc = (ScrollView) findViewById(R.id.scrollZmiana);
                final View finalTorerr = torerr;
                sc.post(new Runnable() {
                    @Override
                    public void run() {
                        sc.smoothScrollTo(0, finalTorerr.getTop());
                    }
                });
                return false;
            }
            scores.ustawTory(ttd(torki));

        }
        if (!correct) {
            return false;
        }

        scores.ustawWynik(wnk.getText().toString(), pln.getText().toString(),
                zbr.getText().toString(), dzi.getText().toString()
        );
        scores.ustawKtoGdzie(zwd.getText().toString(), klb.getSelectedItem().toString(),
                krg.getSelectedItem().toString()
        );
        scores.ustawKategoria(ktg.getSelectedItem().toString());

        Intent returnIntent = new Intent();
        returnIntent.putExtra("wynik", scores);
        returnIntent.putExtra("disabled", false);
        setResult(RESULT_OK, returnIntent);
        finish();
        backAnimation();
        return true;


    }

}
