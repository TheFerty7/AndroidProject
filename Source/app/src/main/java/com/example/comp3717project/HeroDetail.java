package com.example.comp3717project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HeroDetail extends AppCompatActivity {

    public WebView wv;
    private boolean flag = false;
    private String match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero_detail);

        String urlStart = "http://dota2.gamepedia.com/";

        Intent intent = getIntent();
        String urlEnd = intent.getStringExtra("id");

        if (intent.hasExtra("2step")) {
            flag = intent.getBooleanExtra("2step", false);
            match = intent.getStringExtra("match");
        }

        String url = urlStart + urlEnd;
        wv = (WebView) findViewById(R.id.webView);
        wv.setWebViewClient(new WebViewClient());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadsImagesAutomatically(true);
        wv.loadUrl(url);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wv = (WebView) findViewById(R.id.webView);
        if (wv != null) {
            wv.removeAllViews();
        }
        wv = null;
    }

}
