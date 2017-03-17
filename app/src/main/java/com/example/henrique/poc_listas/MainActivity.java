package com.example.henrique.poc_listas;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.henrique.poc_listas.adapter.DayListAdapter;
import com.example.henrique.poc_listas.domain.DayItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Calendar today;
    private int positionToStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        today = Calendar.getInstance();
        ListView list = (ListView) this.findViewById(R.id.dayList);

        DayListAdapter adapter = new DayListAdapter(this, R.layout.day_list, getItens());
        list.setAdapter(adapter);
        list.setSelection(positionToStart - 1);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(Color.GREEN);
                Toast.makeText(getApplicationContext(), "Teste", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<DayItem> getItens() {
        ArrayList days = new ArrayList<DayItem>();

        Calendar calendar = getFirstSundayOfYear();
        int year = calendar.get(Calendar.YEAR);
        int iteration = 1;

        boolean searchForDayToStart = true;
        while(year == calendar.get(Calendar.YEAR)) {

            if(searchForDayToStart && calendar.after(today)) {
                positionToStart = iteration;
                searchForDayToStart = false;
            }

            int dayVaue = calendar.get(Calendar.DAY_OF_MONTH);
            int monthValue = calendar.get(Calendar.MONTH);

            DayItem day = new DayItem(
                    Integer.toString(iteration),
                    Integer.toString(dayVaue),
                    calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH),
                    new BigDecimal(1 * iteration));
            days.add(day);

            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            iteration++;
        }

        return  days;
    }

    private Calendar getFirstSundayOfYear() {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_YEAR, 1);

        while(instance.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            instance.add(Calendar.DAY_OF_WEEK, 1);
        }

        return instance;
    }
}
