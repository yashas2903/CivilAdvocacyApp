package com.example.civiladvocacy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class Details extends AppCompatActivity {

    Officials officials;

    String partyName, imageUrl;

    ConstraintLayout detailsActivityConstraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        detailsActivityConstraintLayout = findViewById(R.id.detailsConstraintLayout);

        Intent intentObject = getIntent();

        if(intentObject.hasExtra("OFFICIAL DETAILS"))
        {
            officials = (Officials) intentObject.getSerializableExtra("OFFICIAL DETAILS");
        }

        partyName = officials.getParty();


        if(officials != null)
        {
            String imageLink = officials.getPhoto();
            if(imageLink != null && !imageLink.isEmpty())
            {
                imageUrl = imageLink;
            }
            else
            {
                imageUrl= "";
            }
        }

        ((TextView)findViewById(R.id.roles_details)).setText(officials.getRole());
        ((TextView)findViewById(R.id.name_details)).setText(officials.getName());

        if(intentObject.hasExtra("LOCATION"))
        {
            String location = intentObject.getStringExtra("LOCATION");
            ((TextView)findViewById(R.id.address_details)).setText(location);
        }

        if (partyName.equals("Democratic Party"))
        {
            ((ImageView)findViewById(R.id.partylogo_details)).setImageResource(R.drawable.dem_logo);
            detailsActivityConstraintLayout.setBackgroundColor(0xff0000ff);
        }

        else if(partyName.equals("Republican Party"))
        {
            ((ImageView)findViewById(R.id.partylogo_details)).setImageResource(R.drawable.rep_logo);
            detailsActivityConstraintLayout.setBackgroundColor(0xffff0000);
        }
        else
        {
            ((ImageView)findViewById(R.id.partylogo_details)).setVisibility(ImageView.GONE);
            detailsActivityConstraintLayout.setBackgroundColor(Color.BLACK);
        }

        downloadAndSetImage();


    }

    private void downloadAndSetImage()
    {
        if (hasInternetConnection())
        {
            // download the official's image using picasso

            if(!imageUrl.equals(""))
            {

                Picasso.get().load(imageUrl).error(R.drawable.brokenimage).into(((ImageView)findViewById(R.id.dodduDp)),
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("Details", "Successful load of the image");
                            }

                            @Override
                            public void onError(Exception exception) {
                                Log.d("Details", "Error: Inside loadImages() function:" + exception.getMessage());
                            }
                        });
            }
            else
            {
                Picasso.get().load(R.drawable.missing).error(R.drawable.missing).into(((ImageView)findViewById(R.id.dodduDp)),
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("Details", "Successful load of the image");
                            }

                            @Override
                            public void onError(Exception exception) {
                                Log.d("Details", "Error: Inside loadImages() function:" + exception.getMessage());
                            }
                        });
            }
        }
        else
        {
            ((ImageView)findViewById(R.id.dodduDp)).setImageResource(R.drawable.brokenimage);
            return;
        }
    }

    private boolean hasInternetConnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
        {
            Toast.makeText(this, "Cannot access Connectivity Manager", Toast.LENGTH_SHORT).show();
            return false;
        }
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork == null) ? false : activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void onClickPartyIcon(View view)
    {
        Intent intentObject = null;

        if(partyName.contains("Democratic"))
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse("https://democrats.org"));

        else if(partyName.contains("Republican"))
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gop.com"));

        else
            Log.d("Details", "clickPartyIcon: ERROR!!!");

        startActivity(intentObject);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

}