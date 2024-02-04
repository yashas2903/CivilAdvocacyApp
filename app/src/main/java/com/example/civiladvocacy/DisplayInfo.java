package com.example.civiladvocacy;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class DisplayInfo extends AppCompatActivity {

    Officials officials;
    Picasso picasso;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    String image="";

    String fbLinkValue;
    String twLinkValue;
    String ytLinkValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_info);

        picasso = Picasso.get();
        picasso.setLoggingEnabled(true);

        Intent intent = getIntent();

        if(intent.hasExtra("OFFICIAL_DETAILS"))
        {
            officials = (Officials) intent.getSerializableExtra("OFFICIAL_DETAILS");

            if(officials != null) {
                String imageLink = officials.getPhoto();
                if(imageLink != null && !imageLink.isEmpty())
                {
                    image = imageLink;
                }
                else
                {
                    image = "";
                }
            }
        }


        if(intent.hasExtra(Intent.EXTRA_TEXT))
        {
            String strLocation = intent.getStringExtra(Intent.EXTRA_TEXT);
            ((TextView)findViewById(R.id.address_display_info)).setText(strLocation);
        }

        loadData();

        downloadAndSetImage();

        Linkify.addLinks((TextView)findViewById(R.id.pAddressDetail), Linkify.MAP_ADDRESSES );
        Linkify.addLinks((TextView)findViewById(R.id.websiteDetails), Linkify.ALL );
        Linkify.addLinks((TextView)findViewById(R.id.phoneDetail), Linkify.ALL );
        Linkify.addLinks((TextView)findViewById(R.id.emailDetail), Linkify.ALL );

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);




    }

    private void handleResult(ActivityResult result) {
    }

    void loadData(){


        ((TextView)findViewById(R.id.detailName)).setText(officials.getName());
        ((TextView)findViewById(R.id.detailRole)).setText(officials.getRole());

        if(officials.getParty() != null  && !officials.getParty().isEmpty()) {
            ((TextView) findViewById(R.id.detailParty)).setText("(" + officials.getParty() + ")");

            if (officials.getParty().equals("Republican Party")) {

                ((ConstraintLayout) findViewById(R.id.websit)).setBackgroundColor(0xffff0000);
                ((ImageView) findViewById(R.id.partyLogoDetail)).setImageResource(R.drawable.rep_logo);


            } else if (officials.getParty().equals("Democratic Party")) {

                ((ConstraintLayout) findViewById(R.id.websit)).setBackgroundColor(0xff0000ff);
                ((ImageView) findViewById(R.id.partyLogoDetail)).setImageResource(R.drawable.dem_logo);

            } else {

                ((ConstraintLayout) findViewById(R.id.websit)).setBackgroundColor(Color.BLACK);
                ((ImageView) findViewById(R.id.partyLogoDetail)).setVisibility(ImageView.GONE);

            }
        }

        if(!officials.getAddress().isEmpty() && officials.getAddress() != null ){
            ((TextView)findViewById(R.id.pAddressDetail)).setText(officials.getAddress());
        }
        else {
            ((TextView)findViewById(R.id.pAddressDetail)).setVisibility(TextView.GONE);
            ((TextView)findViewById(R.id.personAddress)).setVisibility(TextView.GONE);
        }

        if(!officials.getUrls().isEmpty() && officials.getUrls() != null)
        {
            ((TextView)findViewById(R.id.website)).setVisibility(TextView.VISIBLE);
            ((TextView)findViewById(R.id.websiteDetails)).setText(officials.getUrls());
        }
        else
        {
            ((TextView)findViewById(R.id.websiteDetails)).setVisibility(TextView.GONE);
            ((TextView)findViewById(R.id.website)).setVisibility(TextView.GONE);
        }

        if(!officials.getEmail().isEmpty() && officials.getEmail() != null)
        {
            ((TextView)findViewById(R.id.emailDetail)).setText(officials.getEmail());
        }
        else
        {
            ((TextView)findViewById(R.id.emailDetail)).setVisibility(TextView.GONE);
            ((TextView)findViewById(R.id.email)).setVisibility(TextView.GONE);
        }

        if(!officials.getPhone().isEmpty() && officials.getPhone() != null)
        {
            ((TextView)findViewById(R.id.phoneDetail)).setText(officials.getPhone());
        }
        else
        {
            ((TextView)findViewById(R.id.phoneDetail)).setVisibility(TextView.GONE);
            ((TextView)findViewById(R.id.phone)).setVisibility(TextView.GONE);
        }

        String fbID = officials.getFacebook();
        if(fbID != null && !fbID.isEmpty())
            fbLinkValue = fbID;
        else
        {
            ImageView fbIcon = findViewById(R.id.facebook);
            fbIcon.setVisibility(ImageView.GONE);
        }

        String twID = officials.getTwitter();
        if(twID != null && !twID.isEmpty())
            twLinkValue = twID;
        else
        {
            ImageView tIcon = findViewById(R.id.twitter);
            tIcon.setVisibility(ImageView.GONE);
        }

        String ytID = officials.getYoutube();
        if(ytID != null && !ytID.isEmpty())
            ytLinkValue = ytID;
        else
        {
            ImageView tIcon = findViewById(R.id.youtube);
            tIcon.setVisibility(ImageView.GONE);
        }


    }

    private boolean checkInternetConnection() {
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

    public void OnClickTwitter(View v) {
        // get twitter package manager
        String twitterAppUrl = "twitter://user?screen_name=" + twLinkValue;
        String twitterWebUrl = "https://twitter.com/" + twLinkValue;

        Intent intentObject;

        try
        {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        }
        catch (Exception e)
        {
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }

        startActivity(intentObject);
    }

    public void onClickYoutube(View v)
    {
        Intent intentObject = null;
        try
        {
            // intent setting the link's URL
            intentObject = new Intent(Intent.ACTION_VIEW);
            intentObject.setPackage("com.google.android.youtube");
            intentObject.setData(Uri.parse("https://www.youtube.com/" + ytLinkValue));
            startActivity(intentObject);
        }
        catch (ActivityNotFoundException e)
        {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + ytLinkValue)));
        }
    }

    public void onClickFacebook(View v) {
        // get the facebook url
        String fbURL = "https://www.facebook.com/" + fbLinkValue;

        Intent intentObject;
        String urlToUse;
        try {
            // get fb package manager
            getPackageManager().getPackageInfo("com.facebook.katana",0);
            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;

            if(versionCode >= 3002850) {
                // newer version of Facebook app
                urlToUse = "fb://facewebmodal/f?href=" + fbURL;
            }
            else
            { // older
                urlToUse = "fb://page/" + fbLinkValue;
            }
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        }
        catch (Exception e)
        {
            // if no app, open through web browser
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse(fbURL));
        }

        startActivity(intentObject);
    }

    public void onPhotoClick(View view)
    {
        if(!officials.getPhoto().equals(""))
        {
            Intent intentObject = new Intent(this, Details.class);
            String location = ((TextView)findViewById(R.id.address_display_info)).getText().toString();

            // save the data into the intent
            intentObject.putExtra("OFFICIAL DETAILS", officials);
            intentObject.putExtra("LOCATION", location);
            activityResultLauncher.launch(intentObject);
        }
    }

    public void onClickPartyImage(View view)
    {
        Intent intentObject = null;

        if(officials.getParty().contains("Democratic"))
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse("https://democrats.org"));

        else if(officials.getParty().contains("Republican"))
            intentObject = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gop.com"));

        else
            Log.d("DisplayInfo", "clickPartyIcon: ERROR!");

        startActivity(intentObject);
    }

    private void downloadAndSetImage()
    {
        if (checkInternetConnection())
        {
            // download the official's image using picasso
            if(!image.equals(""))
            {

                Picasso.get().load(image).error(R.drawable.brokenimage).into(((ImageView)findViewById(R.id.listDp)),
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("DisplayInfo", "Successful load of the image");
                            }

                            @Override
                            public void onError(Exception exception) {
                                Log.d("DisplayInfo", "Error: Inside loadImages() function:" + exception.getMessage());
                            }
                        });
            }
            else
            {
                Picasso.get().load(R.drawable.missing).error(R.drawable.missing).into(((ImageView)findViewById(R.id.listDp)),
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("DisplayInfo", "Successful load of the image");
                            }

                            @Override
                            public void onError(Exception exception) {
                                Log.d("DisplayInfo", "Error: Inside loadImages() function:" + exception.getMessage());
                            }
                        });
            }
        }
        else
        {
            ((ImageView)findViewById(R.id.listDp)).setImageResource(R.drawable.brokenimage);
            return;
        }
    }

}