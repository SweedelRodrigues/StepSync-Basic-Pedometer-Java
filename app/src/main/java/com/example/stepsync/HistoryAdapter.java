package com.example.stepsync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    List<StepHistory> list = new ArrayList<>();

    public HistoryAdapter(List<StepHistory> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        StepHistory item = list.get(position);

        holder.dateText.setText(item.getDate());
        holder.stepsText.setText(item.getSteps() + " Steps");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView dateText, stepsText;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            dateText = itemView.findViewById(R.id.dateText);   // updated
            stepsText = itemView.findViewById(R.id.stepsText); // updated
        }
    }
}


