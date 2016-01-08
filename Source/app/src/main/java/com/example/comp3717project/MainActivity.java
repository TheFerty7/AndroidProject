package com.example.comp3717project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static String[] heroNames;
    public static String[] heroIDs;
    ArrayList<String> idList;
    private ActionBarDrawerToggle dToggle;
    private DrawerLayout dLayout;
    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), "Loading", Toast.LENGTH_SHORT).show();
        // Call to API and retrieve match data
        String url = "https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/?key=" + SplashActivity.API_KEY + "&language=en_us";

        new DownloadHeroesTask().execute(url);

        // Grant default value for ListView
        try {
            mainListView = (ListView) findViewById(R.id.navList);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            dLayout = (DrawerLayout) findViewById(R.id.home_layout);
            setUpDrawer();

            String[] items = new String[]{"Recent", "Heroes", "Items"};

            ArrayList<String> itemList = new ArrayList<>();

            itemList.addAll(Arrays.asList(items));

            listAdapter = new ArrayAdapter<>(this, R.layout.row, itemList);

            mainListView.setAdapter(listAdapter);

            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    if (position == 1) {
                        Intent intent = new Intent(MainActivity.this, Heroes.class);
                        startActivity(intent);
                    }
                    if (position == 2) {
                        Intent intent = new Intent(MainActivity.this, Items.class);
                        startActivity(intent);
                    }
                }
            });

        } catch (NullPointerException ex) {
            Log.e("List View problem", ex.getMessage());
        }

        // Call data from sqlite
        initRecentGames();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initRecentGames() {
        ListView lv = (ListView) findViewById(R.id.listView4);
        ArrayList<String> gameList = new ArrayList<>();
        ArrayList<String> dataList = new ArrayList<>();

        // Assign SQLite select parameters
        SQLiteDatabase db = new AppDatabaseHelper(this).getReadableDatabase();
        String[] tableColumns = new String[]{
                AppDatabaseHelper.MATCH_ID,
                AppDatabaseHelper.MATCH_SEQ_NUM,
                AppDatabaseHelper.MATCH_TIME,
                AppDatabaseHelper.MATCH_LOBBY
        };
        Cursor c = db.query(AppDatabaseHelper.MATCH_TABLE, tableColumns, null, null,
                null, null, AppDatabaseHelper.MATCH_ID + " DESC", "10");
        if (c != null) {
            if (c.moveToFirst())
                do {
                    gameList.add(c.getString(0));
                    Date date = new Date(Long.parseLong(c.getString(2)) * 1000L); // *1000 is to convert seconds to milliseconds
                    Date date1 = new java.util.Date();
                    long diff = date1.getTime() - date.getTime();
                    dataList.add("Match " + c.getString(0) + ": " + (diff / 1000 / 60) + " minutes ago");
                } while (c.moveToNext());
        }

        idList = gameList;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row, dataList);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MatchActivity.class);
                String cid = idList.get(position);
                intent.putExtra("id", cid);
                startActivity(intent);
            }
        });
    }

    private void setUpDrawer() {
        dToggle = new ActionBarDrawerToggle(this, dLayout,
                R.string.open, R.string.close) {
        };
        dToggle.setDrawerIndicatorEnabled(true);
        dLayout.setDrawerListener(dToggle);
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

    private class DownloadHeroesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (Exception e) {
                Log.e("Problem", e.getMessage());
                return "error404";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                if (result.equalsIgnoreCase("error404"))
                    Toast.makeText(getApplicationContext(), "Unable to retrieve from API", Toast.LENGTH_LONG).show();

                // Start json parser
                JSONObject json = new JSONObject(result);
                JSONArray matches = json.getJSONObject("result").getJSONArray("heroes");
                String[] name = new String[matches.length()];
                String[] id = new String[matches.length()];

                for (int i = 0; i < matches.length(); i++) {
                    name[i] = matches.getJSONObject(i).getString("localized_name");
                    int temp = matches.getJSONObject(i).getInt("id");
                    id[i] = String.valueOf(temp);
                }
                heroNames = name;
                heroIDs = id;
            } catch (Exception ex) {
                Log.d("Problem", ex.getMessage());
            }
        }
    }


}
