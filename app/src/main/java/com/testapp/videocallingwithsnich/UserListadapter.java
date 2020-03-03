package com.testapp.videocallingwithsnich;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserListadapter extends RecyclerView.Adapter<UserListadapter.userViewholder> {
List<UserModel> getUsers;
LayoutInflater inflater;
Context context;

    public UserListadapter(List<UserModel> getUsers, Context context) {
        this.getUsers = getUsers;
        this.context = context;
    }

    @NonNull
    @Override
    public userViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.useritem, parent, false);
        return new userViewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull userViewholder holder, int position) {
        final UserModel model = getUsers.get(position);
        holder.nametext.setText(model.getUsername());
        if (!model.getUserStatus().equalsIgnoreCase("live")){
            holder.userstatusthumb.setImageResource(R.drawable.ic_userdeactive);
            holder.userstatus.setTextColor(context.getResources().getColor(R.color.grey));
        }
        else {
            holder.userstatusthumb.setImageResource(R.drawable.ic_useractive);
            holder.userstatus.setTextColor(context.getResources().getColor(R.color.purple));
        }
        holder.userstatus.setText(model.getUserStatus());
        holder.userlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
         if (model.getUserStatus().equalsIgnoreCase("live")){
             operationDialog dialog = new operationDialog(context,model.getUsername());
             dialog.show();
         }
         else {
             noteDialog dialog1 = new noteDialog(context);
             dialog1.show();
         }
            }
        });

    }
    @Override
    public int getItemCount() {
        return getUsers.size();
    }

    public class userViewholder extends RecyclerView.ViewHolder {
        TextView nametext;
        TextView userstatus;
        ImageView userstatusthumb;
        LinearLayout userlay;
        public userViewholder(@NonNull View iv) {
            super(iv);
            userlay = iv.findViewById(R.id.userlay);
            nametext = iv.findViewById(R.id.nametext);
            userstatus = iv.findViewById(R.id.userstatus);
            userstatusthumb = iv.findViewById(R.id.userstatusthumb);

        }
    }
}
