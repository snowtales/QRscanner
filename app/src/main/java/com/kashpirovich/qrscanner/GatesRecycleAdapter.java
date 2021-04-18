package com.kashpirovich.qrscanner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GatesRecycleAdapter extends RecyclerView.Adapter<GatesRecycleAdapter.MVH> {
    private ArrayList<GatesClass> rawData;
    private Context context;

    GatesRecycleAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public GatesRecycleAdapter.MVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.recycler_view_single_item, parent, false);
        MVH mvh = new MVH(v);
        //кликер можно и сюда
        mvh.single.setOnClickListener(v1 -> {
                    Intent open = new Intent(v.getContext(), EventActivity.class);
                    open.putExtra("gates", rawData.get(mvh.getAdapterPosition()));
                    parent.getContext().startActivity(open);
                }
        );
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull GatesRecycleAdapter.MVH holder, int position) {
        holder.name.setText(rawData.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return rawData == null ? 0 : rawData.size();
    }

    public void setData(ArrayList<GatesClass> data) {
        if (data == null) {
            return;
        }
        this.rawData = data;
        notifyDataSetChanged();
    }

    public class MVH extends RecyclerView.ViewHolder {
        TextView name;
        LinearLayout single;

        public MVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            single = itemView.findViewById(R.id.single_item);

            //кликер сюда
        }
    }
}
