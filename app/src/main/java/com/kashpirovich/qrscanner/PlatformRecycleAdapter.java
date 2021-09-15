package com.kashpirovich.qrscanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlatformRecycleAdapter extends RecyclerView.Adapter<PlatformRecycleAdapter.MyViewHolder> {
    Context context;
    private ArrayList<CinemasClass> rawData;

    public PlatformRecycleAdapter(Context context, ArrayList<CinemasClass> rawData) {
        this.context = context;
        this.rawData = rawData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_single_item, parent, false);
        MyViewHolder mVH = new MyViewHolder(view);
        mVH.single.setOnClickListener(v ->
        {
            Intent open = new Intent(v.getContext(), SecondActivity.class);
            open.putExtra("id", rawData.get(mVH.getAdapterPosition()));
            parent.getContext().startActivity(open);

        });
        return mVH;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(rawData.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return rawData.size();
    }

    public void setData(ArrayList<CinemasClass> data) {
        this.rawData = data;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        LinearLayout single;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            single = itemView.findViewById(R.id.single_item);
        }
    }
}
