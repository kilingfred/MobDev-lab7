package com.example.lab7;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Currency_Adapter extends ArrayAdapter<Currency> {
    private LayoutInflater layoutInflater;
    private int layout;
    private ArrayList<Currency> currencies;

    Currency_Adapter(Context context, int resource, ArrayList<Currency> currencies){
        super(context,resource,currencies);
        this.currencies = currencies;
        this.layout = resource;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Currency currency = currencies.get(position);
        viewHolder.nameView.setText(currency.getName());
        View finalConvertView = convertView;
        return finalConvertView;
    }
    private class ViewHolder {
        final TextView nameView;
        ViewHolder(View view){
            nameView = (TextView)
                    view.findViewById(R.id.currText);
        }
    }
}
