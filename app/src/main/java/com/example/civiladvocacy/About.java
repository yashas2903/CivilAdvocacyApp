package com.example.civiladvocacy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView linkText = this.findViewById(R.id.textView3);
        SpannableString content = new SpannableString("Google Civic Information API");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        linkText.setText(content);
    }

    public void clickCivicApi(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://developers.google.com/civic-information/"));
        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}