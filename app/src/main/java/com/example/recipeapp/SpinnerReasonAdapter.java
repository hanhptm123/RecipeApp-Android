package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerReasonAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> reasons;

    public SpinnerReasonAdapter(Context context, List<String> reasons) {
        super(context, android.R.layout.simple_spinner_item, reasons);
        this.context = context;
        this.reasons = reasons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            return LayoutInflater.from(context).inflate(R.layout.spinner_icon_only, parent, false);
        }
        TextView textView = (TextView) LayoutInflater.from(context)
                .inflate(android.R.layout.simple_spinner_item, parent, false);
        textView.setText(reasons.get(position));
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) LayoutInflater.from(context)
                .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        textView.setText(reasons.get(position));
        return textView;
    }

    @Override
    public int getCount() {
        return reasons.size();
    }
}

