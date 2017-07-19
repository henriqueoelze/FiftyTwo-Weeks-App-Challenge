package com.oelze.henrique.fifty_two_weeks_challenge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.oelze.henrique.fifty_two_weeks_challenge.watcher.NumberTextWatcher;

public class MainActivity extends AppCompatActivity {

    private Long weekDay = 0L;

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                getString(R.string.shared_pref_name), Context.MODE_PRIVATE);

        if(!preferences.getAll().isEmpty()) {
            startChallenge();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                getString(R.string.shared_pref_name), Context.MODE_PRIVATE);

        if(!preferences.getAll().isEmpty()) {
            startChallenge();
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinnerIteration);
        Button btnStartChallenge = (Button) findViewById(R.id.btnStartChallenge);

        EditText startedValue = (EditText) findViewById(R.id.txtStartedValue);
        //setupStartedValueTest(startedValue);
        setupWeekDaysSpinner(spinner);

        btnStartChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueString = startedValue.getText().toString();
                if(!startedValue.getText().toString().isEmpty() && Double.parseDouble(valueString) > 0) {
                    Float value = Float.parseFloat(valueString);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong(getResources().getString(R.string.WEEK_DAY), weekDay);
                    editor.putFloat(getResources().getString(R.string.ITERATION_VALUE), value);
                    editor.commit();

                    startChallenge();
                } else {
                    Toast.makeText(MainActivity.this, R.string.pleaseSelectValue, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startChallenge() {
        Intent challengeIntent = new Intent(getApplicationContext(), ChallengeActivity.class);
        startActivity(challengeIntent);
    }

    private void setupStartedValueTest(EditText startedValue) {
        startedValue.addTextChangedListener(new NumberTextWatcher(startedValue, "#,###"));
    }

    private void setupWeekDaysSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weekDays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekDay = id + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, R.string.pleaseSelectValue, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
