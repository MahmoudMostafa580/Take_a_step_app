package com.example.takeastep.activities.user.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import com.example.takeastep.models.Country;

import java.util.ArrayList;
import java.util.List;

public class TakeStepAdapter extends ArrayAdapter<Country> {
    ArrayList<Country> countries;
    private Context mContext;

    public TakeStepAdapter(@NonNull Context context, int resource, @NonNull List<Country> objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Country getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
