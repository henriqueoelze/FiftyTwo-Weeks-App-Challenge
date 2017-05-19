package com.example.henrique.poc_listas.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.henrique.poc_listas.R;
import com.example.henrique.poc_listas.domain.DayItem;

import java.util.List;

/**
 * Created by Henrique on 16/03/2017.
 */

public class DayListAdapter extends ArrayAdapter {

    private Context context;
    private List<DayItem> values;

    public DayListAdapter(@NonNull Context context, @LayoutRes int resource, List values) {
        super(context, resource, values);
        this.context = context;
        this.values = values;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(this.context).inflate(R.layout.day_list, null);

        DayItem dayItem = values.get(position);

        TextView txtDay = (TextView) convertView.findViewById(R.id.txtDay);
        TextView txtMonth = (TextView) convertView.findViewById(R.id.txtMonth);
        TextView txtValue = (TextView) convertView.findViewById(R.id.txtValue);

        String dayNumber = Integer.parseInt(dayItem.getDay()) < 10 ? "0" + dayItem.getDay() : dayItem.getDay();
        txtDay.setText(dayNumber);
        txtMonth.setText(dayItem.getMonth());
        String valueText = "R$" + (dayItem.getValue().doubleValue() < 10 ? "0" + dayItem.getValue().toString() : dayItem.getValue().toString()) + ",00";
        txtValue.setText(valueText);

        if(dayItem.getPaid()) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.disabledBgColor));
        }

        return convertView;
    }
}
