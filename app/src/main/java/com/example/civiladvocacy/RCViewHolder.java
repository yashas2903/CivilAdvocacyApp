package com.example.civiladvocacy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RCViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView position;
    ImageView dp;

    public RCViewHolder(@NonNull View view){

        super(view);
        name = view.findViewById(R.id.nameList);
        position = view.findViewById(R.id.positionList);
        dp = view.findViewById(R.id.listDp);
    }


}
