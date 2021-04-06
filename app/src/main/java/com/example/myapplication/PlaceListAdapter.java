package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private List<Place> places;
    private OnPlaceListener mOnPlaceListener;

    public PlaceListAdapter(List<Place> places, OnPlaceListener onPlaceListener) {
        this.places = places;
        this.mOnPlaceListener = onPlaceListener;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.frame_textview;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new RecyclerViewHolder(view, mOnPlaceListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.getNameView().setText(places.get(position).getName());
        if(!places.get(position).getTemp().trim().equals("\u2103")) holder.getTempView().setText(places.get(position).getTemp());
    }

    public Place getPlace(int index) { return places.get(index); }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public interface OnPlaceListener {
        void onPlaceClick(int position);
    }
}