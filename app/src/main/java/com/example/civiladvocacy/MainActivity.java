package com.example.civiladvocacy;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    MenuItem place, info;

    String addressMain = "";

    private RCAdapter mAdapter;

    private RecyclerView recyclerView;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int LOCATION_REQUEST = 111;
    private RequestQueue queue;

    private static ArrayList<Officials> officialsArrayList = new ArrayList<>();
    private static String api ="https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyDfwXaoFaOMSLPNrCb3L_JAPu9u-OhKr1E&address=";


    ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);
        recyclerView = findViewById(R.id.rcView);

        mAdapter = new RCAdapter(officialsArrayList,this);
        recyclerView.setAdapter(mAdapter);

        ((TextView)findViewById(R.id.noInternet)).setVisibility(View.INVISIBLE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (savedInstanceState != null && !savedInstanceState.isEmpty()){

            ((TextView)findViewById(R.id.address)).setText(savedInstanceState.getString("Location"));
            fetchData(savedInstanceState.getString("Location"));
        }

        else{

            if (!hasNetworkConnection())
            {
                ((TextView)findViewById(R.id.address)).setText("No Data for Location");
               // String text = "<b>No Network Connection</b>" + "<br>Data cannot be accessed/loaded without an internet connection.";

                ((TextView)findViewById(R.id.noInternet)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.noInternet)).setText("No Network Connection \n Data cannot be accessed/loaded without an internet connection.");
                setTitle("Know Your Government");
                ActionBar actionBar;
                actionBar = getSupportActionBar();

                ColorDrawable colorDrawable
                        = new ColorDrawable(Color.parseColor("#FFBAA2BD"));

                assert actionBar != null;
                actionBar.setBackgroundDrawable(colorDrawable);
            }
            else
            {
                getLocation();
//                fetchData(addressMain);
            }

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this, "Location Access Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    public void handleResult(ActivityResult result)
    {
        if (result.getResultCode() == RESULT_OK)
        {
            Intent data = result.getData();
            if (data == null)
                return;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        place = menu.findItem(R.id.location);
        info = menu.findItem(R.id.help);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.help){
            Intent i = new Intent(this, About.class);
            startActivity(i);
        }

        else if (item.getItemId() == R.id.location){
            if(hasNetworkConnection()){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);


                final EditText inputAddress = new EditText(MainActivity.this);
                inputAddress.setGravity(Gravity.CENTER_HORIZONTAL);
                inputAddress.setInputType(InputType.TYPE_CLASS_TEXT);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String loc = inputAddress.getText().toString();
                        ((TextView)findViewById(R.id.address)).setText(loc);
                        fetchData(loc);
                    }
                });

                builder.setNegativeButton("CANCEL", (dialog,which)->dialog.dismiss());
                builder.setTitle("Enter Address");
                builder.setView(inputAddress);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }

        else{
            return super.onOptionsItemSelected(item);
        }
       return true;

    }

    private void  getLocation()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            // Get last known location. In some situations this can be null.

            if (location != null)
            {

                addressMain = getAddress(location);
                fetchData(addressMain);
                ((TextView)findViewById(R.id.address)).setText(addressMain.split("\n")[0]);
            }
        }).addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                e.getMessage(), Toast.LENGTH_LONG).show());

    }


    private String getAddress(Location loc)
    {
        // get the location by getting the list of address from geoder
        StringBuilder sb = new StringBuilder();
        String pinCode="";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try
        {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
             pinCode= addresses.get(0).getPostalCode();
            sb.append(city.isEmpty()? state + " " + pinCode : city + "," + state + " " + pinCode);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }


    private boolean hasNetworkConnection()
    {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }


    public void fetchData(String location){
        String urlApi = api + location;

        Log.d("APIKARIYODHU", "fetchData: " + urlApi);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{

                    officialsArrayList.clear();

                    String addressJson ="";
                    String name = "",role = "",address = "",party = "",phone = "",urls = "",email = "",photo = "",facebook = "",twitter = "",youtube = "";

                    if(response.getJSONObject("normalizedInput").getString("line1").length() !=0)
                        addressJson = response.getJSONObject("normalizedInput").getString("line1")+" " ;

                    addressJson = addressJson +response.getJSONObject("normalizedInput").getString("city") +" ,"+
                            response.getJSONObject("normalizedInput").getString("state") + " " +
                            response.getJSONObject("normalizedInput").getString("zip") ;

                    JSONArray listOfOffices =response.getJSONArray("offices");
                    JSONArray listOfOfficials = response.getJSONArray("officials");

                    for(int i = 0; i < listOfOffices.length(); i++){

                        name = "";
                        role = "";address = "";party = "";phone = "";urls = "";email = "";photo = "";facebook = "";twitter = "";youtube = "";

                        role = listOfOffices.getJSONObject(i).getString("name");

                        JSONArray officialIndex = listOfOffices.getJSONObject(i).getJSONArray("officialIndices");
                        for(int j =0; j<officialIndex.length();j++){

                            int index = officialIndex.getInt(j);
                            JSONObject officerObject = listOfOfficials.getJSONObject(index);
                            name = officerObject.getString("name");

                            address = "";
                            if(officerObject.has("address")){
                            if(officerObject.getJSONArray("address").getJSONObject(0).has("line1")){
                                address = officerObject.getJSONArray("address").getJSONObject(0).getString("line1");
                            }

                            if(officerObject.getJSONArray("address").getJSONObject(0).has("line2")){
                                address = address + " "+ officerObject.getJSONArray("address").getJSONObject(0).getString("line2");
                            }

                            if(officerObject.getJSONArray("address").getJSONObject(0).has("line3")){
                                address = address + " "+ officerObject.getJSONArray("address").getJSONObject(0).getString("line3");
                            }

                            address = address + ","+ officerObject.getJSONArray("address").getJSONObject(0).getString("city") +"," +
                                    officerObject.getJSONArray("address").getJSONObject(0).getString("state") + "," +
                                            officerObject.getJSONArray("address").getJSONObject(0).getString("zip");}

                            if (officerObject.has("party"))
                                party = officerObject.getString("party");
                            if (officerObject.has("phones"))
                                phone = officerObject.getJSONArray("phones").getString(0);
                            if (officerObject.has("urls"))
                                urls = officerObject.getJSONArray("urls").getString(0);
                            if (officerObject.has("emails"))
                                email = officerObject.getJSONArray("emails").getString(0);

                            if (officerObject.has("photoUrl"))
                                photo = officerObject.getString("photoUrl");


                            twitter = "";
                            youtube = "";
                            facebook = "";

                            if (officerObject.has("channels")) {
                                JSONArray channelArrayJson = officerObject.getJSONArray("channels");
                                for (int k = 0; k < channelArrayJson.length(); k++) {

                                    if (channelArrayJson.getJSONObject(k).getString("type").equals("Twitter")) {
                                        twitter = channelArrayJson.getJSONObject(k).getString("id");
                                    } else if (channelArrayJson.getJSONObject(k).getString("type").equals("Youtube")) {
                                        youtube = channelArrayJson.getJSONObject(k).getString("id");
                                    } else if (channelArrayJson.getJSONObject(k).getString("type").equals("Facebook")) {
                                        facebook = channelArrayJson.getJSONObject(k).getString("id");
                                    }

                                }
                            }

                            officialsArrayList.add(new Officials(name,role,address,party,phone,urls,email,photo,facebook,twitter,youtube));
                        }

                    }
                    //notifyAdapter();
                    mAdapter.notifyItemInserted(officialsArrayList.size());
                    mAdapter.notifyDataSetChanged();
                    ((TextView)findViewById(R.id.address)).setText(addressJson);
                    addressMain = addressJson;
                    setTitle("Civil Advocacy");
                    ActionBar actionBar;
                    actionBar = getSupportActionBar();

                    ColorDrawable colorDrawable
                            = new ColorDrawable(Color.parseColor("#4F1450"));

                    assert actionBar != null;
                    actionBar.setBackgroundDrawable(colorDrawable);



                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        };

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                try
                {
                    JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                    Log.d("hoge", "OnErrorResponse: " + jsonObject);
                    ((TextView)findViewById(R.id.address)).setText("No Data for the location");
                    officialsArrayList.clear();
                    mAdapter.notifyDataSetChanged();
                    setTitle("Know Your Government");
                    ActionBar actionBar;
                    actionBar = getSupportActionBar();

                    ColorDrawable colorDrawable
                            = new ColorDrawable(Color.parseColor("#FFBAA2BD"));

                    assert actionBar != null;
                    actionBar.setBackgroundDrawable(colorDrawable);



                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };

        Log.d("apiurl",urlApi);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlApi,
                null, listener, error);
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);


    }


    void notifyAdapter()
    {
        mAdapter.notifyItemInserted(officialsArrayList.size());
        mAdapter.notifyDataSetChanged();

    }

    public void onClick(View view)
    {
        // On click of the recycle get its position and get specific item
        int position = recyclerView.getChildLayoutPosition(view);
        Officials officialObject = officialsArrayList.get(position);

        // start the official activity of clicked item
        Intent intentObject = new Intent(this, DisplayInfo.class);
        intentObject.putExtra("OFFICIAL_DETAILS", officialObject);
        intentObject.putExtra(Intent.EXTRA_TEXT, addressMain);
        activityResultLauncher.launch(intentObject);
    }


    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putString("Location", ((TextView)findViewById(R.id.address)).getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // restoring the state as previous before there is change in orientation
        super.onRestoreInstanceState(savedInstanceState);

    }

}
