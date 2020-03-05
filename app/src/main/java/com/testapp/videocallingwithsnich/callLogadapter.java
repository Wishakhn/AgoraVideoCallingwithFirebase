package com.testapp.videocallingwithsnich;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class callLogadapter extends RecyclerView.Adapter<callLogadapter.callViewholder> {

    List<callLogModel> getCalls;
    LayoutInflater inflater;
    public callLogadapter(List<callLogModel> getCalls) {
        this.getCalls = getCalls;
    }

    @NonNull
    @Override
    public callLogadapter.callViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.callitem, parent, false);
        return new callViewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull callLogadapter.callViewholder holder, int position) {
        callLogModel model = getCalls.get(position);
        holder.callername.setText(model.getCallername());
        holder.callertime.setText(model.getCalldur());

    }

    @Override
    public int getItemCount() {
        return getCalls.size();
    }

    public class callViewholder extends RecyclerView.ViewHolder {
        TextView callername;
        TextView callertime;
        public callViewholder(@NonNull View iv) {
            super(iv);
            callername = iv.findViewById(R.id.callername);
            callertime = iv.findViewById(R.id.callertime);
        }
    }
}
