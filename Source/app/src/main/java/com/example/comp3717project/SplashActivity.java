package com.example.comp3717project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private final int LOADING_TIME = 10;

    TextView status;

    ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView image = (ImageView) findViewById(R.id.imageView);
        image.setImageResource(R.drawable.photo);

        Context context = this.getApplicationContext();
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        status = (TextView) findViewById(R.id.txtStatus);

        new LoadingTask().execute();
    }

    private class LoadingTask extends AsyncTask<Integer, String, Void> {
        int i = LOADING_TIME;

        @Override
        protected Void doInBackground(final Integer... params) {
            while (cm.getActiveNetworkInfo() == null
                    || !cm.getActiveNetworkInfo().isAvailable()
                    || !cm.getActiveNetworkInfo().isConnected()) {
                publishProgress("Please enable internet connection!");
            }

            new Thread() {
                @Override
                public void run() {
                    try {
                        while (i != 0) {
                            publishProgress("System loading time: " + i);
                            i--;
                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {
                    }
                }
            }.run();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            status.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(SplashActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
