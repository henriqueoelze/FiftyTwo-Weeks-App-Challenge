package com.example.henrique.poc_listas;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.henrique.poc_listas.adapter.DayListAdapter;
import com.example.henrique.poc_listas.domain.DayItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final int START_INDEX_OF_LIST = 1;
    private int positionToStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<DayItem> items = setupList();
        //configureNotification(items);
    }

    private List<DayItem> setupList() {
        ListView list = (ListView) this.findViewById(R.id.dayList);
        final List<DayItem> itens = getItems();

        DayListAdapter adapter = new DayListAdapter(this, R.layout.day_list, itens);
        list.setAdapter(adapter);

        list.setSelection(positionToStart - START_INDEX_OF_LIST);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final DayItem clickedItem = itens.get(position);
                if(!clickedItem.getPaid()) {
                    String message = getString(R.string.confirmMessage, clickedItem.getValue().toString() ,clickedItem.getDay().toString(), clickedItem.getMonth().toString());
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.confirmTitle))
                            .setMessage(message)
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    view.setBackgroundColor(getResources().getColor(R.color.disabledBgColor));
                                    clickedItem.setPaid(Boolean.TRUE);
                                }
                            })
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });

        return itens;
    }

    private List<DayItem> getItems() {
        ArrayList<DayItem> days = new ArrayList<DayItem>();

        Calendar calendar = getFirstSundayOfYear();
        Calendar today = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int iteration = 1;

        boolean searchForDayToStart = true;
        while(year == calendar.get(Calendar.YEAR)) {
            if(searchForDayToStart && (calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) || calendar.after(today))) {
                positionToStart = iteration;
                searchForDayToStart = false;
            }

            DayItem day = new DayItem(
                    Integer.toString(iteration),
                    Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)),
                    calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH),
                    new BigDecimal(iteration));
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

//    private void configureNotification(List<DayItem> itens) {
//        android.support.v4.app.NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext())
//                .setSmallIcon(R.drawable.dollar)
//                .setContentTitle("Esta na hora de poupar!")
//                .setContentText("Chegou o momento de depositar na sua caixinha. Clique para conferir!")
//                .setVibrate(new long[]{1, 2})
//                .setWhen(System.currentTimeMillis());
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notification.build());
//    }
}
