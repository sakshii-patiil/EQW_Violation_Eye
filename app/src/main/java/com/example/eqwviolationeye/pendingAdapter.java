package com.example.eqwviolationeye;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class pendingAdapter extends ArrayAdapter<pending> {

    // invoke the suitable constructor of the ArrayAdapter class
    public pendingAdapter(@NonNull Context context, ArrayList<pending> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.pending_item, parent, false);
        }
        else {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.pending_item, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        pending currentNumberPosition = getItem(position);
        // then according to the position of the view assign the desired image for the same
        TextView fullDate = currentItemView.findViewById(R.id.fullDate);
        TextView time = currentItemView.findViewById(R.id.time);
        fullDate.setText(currentNumberPosition.getDate());
        time.setText(currentNumberPosition.getTime());
        // then according to the position of the view assign the desired TextView 1 for the same
        // then return the recyclable view
        return currentItemView;
    }
}