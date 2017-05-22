package com.example.henrique.poc_listas;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
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
    private SharedPreferences preferences;
    private List<DayItem> allItems;
    private List<DayItem> selectedItems;
    private DayListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getPreferences(CONTEXT_RESTRICTED);
        selectedItems = new ArrayList();
        allItems = setupList();
        //configureNotification(items);
    }

    private List<DayItem> setupList() {
        ListView list = (ListView) this.findViewById(R.id.dayList);
        final List<DayItem> itens = getItems();

        setupAdapter(list, itens);
        setupInitialPosition(list);
        setupOnClick(list, itens);
        setupOnLongClick(list);

        return itens;
    }

    private void setupOnLongClick(ListView list) {
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                DayItem selectedItem = allItems.get(position);

                if(!selectedItem.getPaid()) {
                    adapter.changeItemState(position, checked);
                    if(checked) {
                        selectedItems.add(selectedItem);
                    } else {
                        selectedItems.remove(selectedItem);
                    }

                    mode.setTitle(getString(R.string.contextMenuTitle, calculateTotalValue().toString()));
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.items, menu);

                mode.setTitle(getString(R.string.contextMenuTitle, calculateTotalValue().toString()));
                mode.setSubtitle(getString(R.string.contextMenuSubTitle));

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.btnMenuPagar:
                        paidMultipleItems(mode);
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selectedItems.stream().forEach(item -> {
                    adapter.changeItemState(Integer.parseInt(item.getIteration()) - 1, false);
                });

                selectedItems.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setupInitialPosition(ListView list) {
        list.setSelection(positionToStart - START_INDEX_OF_LIST);
    }

    private void setupOnClick(ListView list, final List<DayItem> itens) {
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
                                    payItem(clickedItem);
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
    }

    private void setupAdapter(ListView list, List<DayItem> itens) {
        adapter = new DayListAdapter(this, R.layout.day_list, itens);
        list.setAdapter(adapter);
    }

    private void payItem(DayItem clickedItem) {
        clickedItem.setPaid(Boolean.TRUE);
        adapter.notifyDataSetChanged();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(clickedItem.getIteration(), Boolean.TRUE);
        editor.commit();
    }

    private List<DayItem> getItems() {
        ArrayList<DayItem> days = new ArrayList<DayItem>();
        final Locale myLocale = new Locale("pt", "BR");

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

            String iterationString = Integer.toString(iteration);
            DayItem day = new DayItem(
                    iterationString,
                    Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)),
                    calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, myLocale),
                    new BigDecimal(iteration),
                    preferences.getBoolean(iterationString, false));
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

    public void paidMultipleItems(ActionMode mode) {
        BigDecimal totalValue = calculateTotalValue();

        String message = getString(R.string.confirmMessageMultipleItens, totalValue.toString());
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.confirmTitle))
                .setMessage(message)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItems.stream().forEach(item -> payItem(item));
                        mode.finish();
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

    private BigDecimal calculateTotalValue() {
        return selectedItems
                    .stream()
                    .map(item -> item.getValue())
                    .reduce(BigDecimal.ZERO, (v1, v2) -> v1.add(v2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.btnLimparDados:
                String message = getString(R.string.msgConfirmaLimparDados);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.confirmTitle))
                        .setMessage(message)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.commit();
                                restartApp();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void restartApp() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
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
