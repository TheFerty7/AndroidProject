package com.example.comp3717project;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class PlayerDetail extends Activity {

    public WebView wv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        Toast.makeText(getApplicationContext(), "EXTRA LOADING TIME", Toast.LENGTH_LONG).show();

        String urlStart = "http://dota2.gamepedia.com/";

        Intent intent = getIntent();
        String urlEnd = intent.getStringExtra("id");

        String url = urlStart + urlEnd;
        wv3 = (WebView) findViewById(R.id.webView3);
        wv3.setWebViewClient(new WebViewClient());
        wv3.getSettings().setJavaScriptEnabled(true);
        wv3.getSettings().setLoadsImagesAutomatically(true);
        wv3.loadUrl(url);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wv3 = (WebView) findViewById(R.id.webView3);
        if (wv3 != null) {
            wv3.removeAllViews();
        }
        wv3 = null;
    }

}
