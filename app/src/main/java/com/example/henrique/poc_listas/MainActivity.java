package com.example.henrique.poc_listas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.henrique.poc_listas.adapter.DayListAdapter;
import com.example.henrique.poc_listas.domain.DayItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView list = (ListView) this.findViewById(R.id.dayList);

        DayListAdapter adapter = new DayListAdapter(this, R.layout.day_list, getItens());
        list.setAdapter(adapter);
    }

    private List<DayItem> getItens() {
        ArrayList days = new ArrayList<DayItem>();

        Calendar calendar = getFirstSundayOfYear();
        int year = calendar.get(Calendar.YEAR);
        int iteration = 1;
        while(year == calendar.get(Calendar.YEAR)) {
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

        Log.i("TESTE", instance.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH));

        while(instance.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            instance.add(Calendar.DAY_OF_WEEK, 1);
        }

        return instance;
    }
}
