package com.oelze.henrique.fifty_two_weeks_challenge;

import android.content.Context;
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

import com.oelze.henrique.fifty_two_weeks_challenge.adapter.DayListAdapter;
import com.oelze.henrique.fifty_two_weeks_challenge.domain.DayItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChallengeActivity extends AppCompatActivity {
    public static final int START_INDEX_OF_LIST = 1;

    private int positionToStart;
    private SharedPreferences preferences;
    private List<DayItem> allItems;
    private List<DayItem> selectedItems;
    private DayListAdapter adapter;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_challenge);
        super.onCreate(savedInstanceState);
        initVariables();
    }

    private void initVariables() {
        selectedItems = new ArrayList();
        preferences = getApplicationContext().getSharedPreferences(
                getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
        allItems = setupList();
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

                if(selectedItem.getPaid() && selectedItems.isEmpty()) {
                    mode.finish();
                } else {
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
                    new AlertDialog.Builder(ChallengeActivity.this)
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

        Calendar calendar = getFirstDayOfYear();
        Calendar today = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        float iterationValue = preferences.getFloat(getResources().getString(R.string.ITERATION_VALUE), 1);
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
                    new BigDecimal(iterationValue * iteration),
                    preferences.getBoolean(iterationString, false));
            days.add(day);

            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            iteration++;
        }

        return  days;
    }

    private Calendar getFirstDayOfYear() {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_YEAR, 1);

        Long dayToPay = preferences.getLong(getResources().getString(R.string.WEEK_DAY), 1);
        while((int) instance.get(Calendar.DAY_OF_WEEK) != dayToPay.intValue()) {
            instance.add(Calendar.DAY_OF_WEEK, 1);
        }

        return instance;
    }

    public void paidMultipleItems(ActionMode mode) {
        BigDecimal totalValue = calculateTotalValue();

        String message = getString(R.string.confirmMessageMultipleItens, totalValue.toString());
        new AlertDialog.Builder(ChallengeActivity.this)
                .setTitle(getString(R.string.confirmTitle))
                .setMessage(message)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItems.forEach(item -> payItem(item));
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
                    .map(DayItem::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
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
                new AlertDialog.Builder(ChallengeActivity.this)
                        .setTitle(getString(R.string.confirmTitle))
                        .setMessage(message)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
    }
}
