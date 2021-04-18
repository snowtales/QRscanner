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

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MVH> {
    private ArrayList<EventClass> rawData;
    private Context context;

    EventsAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public EventsAdapter.MVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.recycler_single_events, parent, false);
        MVH mvh = new MVH(v);
        //кликер можно и сюда
        mvh.single.setOnClickListener(v1 -> {
                    Intent open = new Intent(v.getContext(), ThirdActivity.class);
                    open.putExtra("event", rawData.get(mvh.getAdapterPosition()));
                    parent.getContext().startActivity(open);
                }
        );
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.MVH holder, int position) {
        holder.nameFilm.setText(rawData.get(position).getNameFilm());
        holder.date.setText(rawData.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return rawData == null ? 0 : rawData.size();
    }

    public void setData(ArrayList<EventClass> data) {
        if (data == null) {
            return;
        }
        this.rawData = data;
        notifyDataSetChanged();
    }

    public class MVH extends RecyclerView.ViewHolder {
        TextView nameFilm;
        TextView date;
        LinearLayout single;

        public MVH(@NonNull View itemView) {
            super(itemView);
            nameFilm = itemView.findViewById(R.id.nameFilm);
            single = itemView.findViewById(R.id.single_item_event);
            date = itemView.findViewById(R.id.date_start);

            //кликер сюда
        }
    }
}
