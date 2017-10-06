package com.bilal.mqtt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MQTTHistoryAdapter extends RecyclerView.Adapter<MQTTHistoryAdapter.ViewHolder>{

    private ArrayList<String> history;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.row_text);
        }
    }

    public MQTTHistoryAdapter(ArrayList<String> dataSet){
        history = dataSet;
    }

    @Override
    public MQTTHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Create View
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mqtt_history_adapter_row, parent, false);

        return new ViewHolder(v);
    }

    public void add(String data){
        history.add(data);
        this.notifyDataSetChanged();
    }

    public void clearLog(){
        history.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(history.get(position));
    }

    @Override
    public int getItemCount() {
        return history.size();
    }



}
