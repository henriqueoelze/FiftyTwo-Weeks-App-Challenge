package com.example.henrique.fifty_two_weeks_challenge;

import android.content.SharedPreferences;
import android.support.annotation.FloatRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Long weekDay = 0L;
    private Double value = 0.0;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getPreferences(CONTEXT_RESTRICTED);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerIteration);
        Button btnStartChallenge = (Button) findViewById(R.id.btnStartChallenge);

        setupWeekDaysSpinner(spinner);

        btnStartChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView startedValue = (TextView) findViewById(R.id.txtStartedValue);
                String valueString = startedValue.getText().toString();
                if(!startedValue.getText().toString().isEmpty() && Double.parseDouble(valueString) > 0) {
                    Float value = Float.parseFloat(valueString);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong(getResources().getString(R.string.WEEK_DAY), weekDay);
                    editor.putFloat(getResources().getString(R.string.ITERATION_VALUE), value);
                    editor.commit();

                    Toast.makeText(MainActivity.this, "OK -> Day: " + weekDay + " | Value: " + value, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.pleaseSelectValue, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupWeekDaysSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weekDays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekDay = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, R.string.pleaseSelectValue, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
