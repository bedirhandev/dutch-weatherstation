package com.example.myapplication;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView nameView;
    private TextView tempView;
    private PlaceListAdapter.OnPlaceListener onPlaceListener;

    public RecyclerViewHolder(@NonNull View itemView, PlaceListAdapter.OnPlaceListener onPlaceListener) {
        super(itemView);
        nameView = itemView.findViewById(R.id.placeName);
        tempView = itemView.findViewById(R.id.tempView);

        this.onPlaceListener = onPlaceListener;
        itemView.setOnClickListener(this);
    }

    public TextView getNameView(){
        return nameView;
    }
    public TextView getTempView(){
        return tempView;
    }

    @Override
    public void onClick(View v) {
        onPlaceListener.onPlaceClick(getAdapterPosition());
    }
}