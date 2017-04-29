package io.maciej01.niezbednikkreglarza.turniej;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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

import io.maciej01.niezbednikkreglarza.R;


/**
 * Created by Maciej on 2017-04-29.
 */

public class TEditActivity extends AppCompatActivity {//implements DatePickerDialog.OnDateSetListener {
    public Turniej turniej;
    int year1,year2,month1,month2,day1,day2;
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

        setContentView(R.layout.zadd_layout);
        Intent i = getIntent();
        TurniejList parent = (TurniejList) i.getSerializableExtra("lista");
        String rodzaj = (String) i.getSerializableExtra("rodzaj");
        turniej = new Turniej(parent);

        Calendar myCalendar = Calendar.getInstance();
        year1 = myCalendar.get(Calendar.YEAR);
        month1 = myCalendar.get(Calendar.MONTH);
        day1 = myCalendar.get(Calendar.DAY_OF_MONTH);
        year2 = year1; // ints dont have pointers i think? i hope so
        month2 = month1; // theyre primitive types et cetera
        day2 = month1;
        if (rodzaj.equals("zmiana")) {
            turniej = (Turniej) i.getSerializableExtra("wynik");
            String sw = turniej.getDateStart();
            String sen = turniej.getDateEnd();
            String[] a1 = sw.split("-");
            String[] a2 = sen.split("-");
            year1 = Integer.valueOf(a1[0]);
            month1 = Math.abs(Integer.valueOf(a1[1])-1); // it's index
            day1 = Integer.valueOf(a1[2]);
            year2 = Integer.valueOf(a2[0]);
            month2 = Math.abs(Integer.valueOf(a2[1])-1); // it's index
            day2 = Integer.valueOf(a2[2]);
        }

        final ArrayAdapter<CharSequence> adapterKregielnia = ArrayAdapter.createFromResource(this,
                R.array.kregielnie, android.R.layout.simple_spinner_item);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String spKregielnia = SP.getString("kregielnia","Kręgielnia Dziewiątka-Amica Wronki");

        Spinner spinnerKregielnia = (Spinner) findViewById(R.id.zaddKregielnia);

        View.OnClickListener showDatePickerStart = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(TEditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        turniej.ustawDateStart(year, month, dayOfMonth);
                    }}, year1, month1, day1);
                datePickerDialog.show();}};

        View.OnClickListener showDatePickerEnd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(TEditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        turniej.ustawDateEnd(year, month, dayOfMonth);
                        Log.v("end", Integer.toString(year));
                    }}, year2, month2, day2);
                datePickerDialog.show();}};

        (findViewById(R.id.zaddPoczatek)).setOnClickListener(showDatePickerStart);
        (findViewById(R.id.zaddKoniec)).setOnClickListener(showDatePickerEnd);

        adapterKregielnia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKregielnia.setAdapter(adapterKregielnia);
        spinnerKregielnia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        spinnerKregielnia.setSelection(getIndex(spinnerKregielnia, spKregielnia));
        if (rodzaj.equals("zmiana")) {
            spinnerKregielnia.setSelection(adapterKregielnia.getPosition(turniej.getKregielnia()));
            ((EditText)findViewById(R.id.zaddNazwa)).setText(turniej.getNazwa());
        }
        Button btZap = (Button) findViewById(R.id.zaddZapisz);
        btZap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zapisz();
            }
        });
    }

    public void backAnimation() {
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.return_);
        builder.setMessage(R.string.dialog_return);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                zapisz();
            }
        });
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                TEditActivity.super.onBackPressed();
                backAnimation();
            }
        });
        builder.show();

    }

    public boolean zapisz() {
        boolean correct = true;

        EditText nazwa = (EditText) findViewById(R.id.zaddNazwa);
        Spinner krg = (Spinner) findViewById(R.id.zaddKregielnia);

        View torerr = null;
        EditText[] lista = {nazwa};
        for (EditText item : lista) {
            item.setError(null);
            if (TextUtils.isEmpty(item.getText().toString())) {
                item.setError(getString(R.string.error_field));
                torerr = item;
                correct = false;
            }
        }
        if (turniej.getDateStart() == "") {
            ((Button) findViewById(R.id.zaddPoczatek)).setError(getString(R.string.error_data));
            torerr = ((Button) findViewById(R.id.zaddPoczatek));
            correct = false;
        } else {((Button) findViewById(R.id.zaddPoczatek)).setError(null);}
        if (turniej.getDateEnd() == "") {
            ((Button) findViewById(R.id.zaddKoniec)).setError(getString(R.string.error_data));
            torerr = ((Button) findViewById(R.id.zaddKoniec));
            correct = false;
        } else {((Button) findViewById(R.id.zaddKoniec)).setError(null);}
        if (!correct) {
            return false;
        }

        turniej.ustawCoGdzie(nazwa.getText().toString(), krg.getSelectedItem().toString()); // data ustawiona w listenerze

        Intent returnIntent = new Intent();
        returnIntent.putExtra("wynik", turniej);
        setResult(RESULT_OK, returnIntent);
        finish();
        backAnimation();
        return true;
    }
}
