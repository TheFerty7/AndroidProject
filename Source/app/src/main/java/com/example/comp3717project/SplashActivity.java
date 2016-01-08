package com.example.comp3717project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends Activity {

    public static final String API_KEY = "12C01D8A57DFF90DB5C355DC37FDAB56";
    public static final int MATCH_REQUEST = 100;
    public static final int MIN_PLAYER = 10;
    private static final int APP_WAIT_TIME = 3;
    private static final int API_RETRY_NUMBER = 3;

    private SQLiteOpenHelper helper;
    private TextView status;
    private ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView image = (ImageView) findViewById(R.id.imageView);
        image.setImageResource(R.drawable.photo);

        Context context = this.getApplicationContext();
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        status = (TextView) findViewById(R.id.txtStatus);

        helper = new AppDatabaseHelper(this);

        String stringUrl = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?" +
                "key=" + API_KEY +
                "&matches_requested=" + MATCH_REQUEST +
                "&min_players=" + MIN_PLAYER;

        new LoadingTask().execute(stringUrl);
    }

    private String downloadUrl(String myurl) throws Exception {
        InputStream in = null;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Start connection
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("HTTP-URL-CONNECTION", "The response is: " + response);
            in = conn.getInputStream();

            return readIt(in);
        } catch (Exception ex) {
            Log.e("Problem", ex.getMessage());
            return "error404";
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private String readIt(InputStream stream) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader bufReader = new BufferedReader(reader);

        String line;
        String result = "";
        while ((line = bufReader.readLine()) != null) {
            result += line.replaceAll("\\s+", "");
        }

        return result;
    }

    private class LoadingTask extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(final String... params) {
            while (cm.getActiveNetworkInfo() == null
                    || !cm.getActiveNetworkInfo().isAvailable()
                    || !cm.getActiveNetworkInfo().isConnected()) {
                publishProgress("Please enable internet connection!");
            }

            new Thread() {
                @Override
                public void run() {
                    try {
                        publishProgress("Calling to API");
                        String result = "";
                        boolean flag = false;

                        for (int i = 0; i < API_RETRY_NUMBER; i++) {
                            result = downloadUrl(params[0]);
                            if (result.equalsIgnoreCase("error404") || result.length() < 100) {
                                flag = true;
                                for (int j = 5; j > 0; j--) {
                                    publishProgress("Unable to retrieve data. Retry in " + j + " seconds");
                                    Thread.sleep(1000);
                                }
                            } else {
                                flag = false;
                                break;
                            }
                        }

                        if (flag) {
                            publishProgress("Unable to connect to Valve API.");
                            Thread.sleep(1500);
                        } else {
                            publishProgress("Loading data");
                            Thread.sleep(1500);

                            // Start json parser
                            JSONObject json = new JSONObject(result);
                            JSONArray matches = json.getJSONObject("result").getJSONArray("matches");

                            for (int i = 0; i < matches.length(); i++) {
                                String id = matches.getJSONObject(i).getString("match_id");
                                long seq_num = matches.getJSONObject(i).getLong("match_seq_num");
                                String start_time = matches.getJSONObject(i).getString("start_time");
                                String lobby_type = matches.getJSONObject(i).getString("lobby_type");
                                ((AppDatabaseHelper) helper).insertMatch(helper.getWritableDatabase(), id, seq_num, start_time, lobby_type);
                            }
                            Log.d("SQLite", "Added " + matches.length() + " records into the database");

                            for (int i = APP_WAIT_TIME; i > 0; i--) {
                                publishProgress("Application starts in " + i + " sec");
                                Thread.sleep(1000);
                            }
                        }
                    } catch (Exception ex) {
                        Log.e("Problem", ex.getMessage());
                        publishProgress("Problem occurs. Please restart the app!");
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
