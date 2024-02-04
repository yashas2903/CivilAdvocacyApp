package com.example.civiladvocacy;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class RCAdapter extends RecyclerView.Adapter<RCViewHolder>
{

    private MainActivity activity;
    private List<Officials> officialsList;

    private static RequestQueue requestQueue;


    public RCAdapter( List<Officials> oList, MainActivity activityMain){

        activity = activityMain;
        officialsList = oList;

        requestQueue = Volley.newRequestQueue(activity);


    }

    @NonNull
    @Override
    public RCViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_officials, parent, false);

        view.setOnClickListener(activity);
        return new RCViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RCViewHolder holder, int position) {

        Officials officials = officialsList.get(position);

        holder.position.setText(officials.getRole());

        String name ="";

        name = officials.getName() + "(" + officials.getParty() + ")";

        holder.name.setText(name);


        Response.Listener<Bitmap> listener = response ->
        {
            holder.dp.setImageBitmap(response);
        };

        Response.ErrorListener error = error1 ->
                Log.d("Adapter", "OnBindViewHolder:" + error1.getMessage());

        holder.dp.setImageResource(R.drawable.brokenimage);

        String imageURL = officials.getPhoto();

        if(imageURL.isEmpty())
        {
            holder.dp.setImageResource(R.drawable.missing);
        }

        imageURL = imageURL.replace("http", "https");
        ImageRequest imageRequest = new ImageRequest(imageURL, listener, 0, 0,
                null, Bitmap.Config.RGB_565, error);

        requestQueue.add(imageRequest);




    }

    @Override
    public int getItemCount() {
        return officialsList.size();
    }
}
