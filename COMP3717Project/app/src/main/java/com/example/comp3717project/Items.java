package com.example.comp3717project;

import android.content.Intent;
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
import android.widget.SimpleAdapter;
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
import java.util.HashMap;

public class Items extends AppCompatActivity {

    public String[] itemsArray;
    private ActionBarDrawerToggle dToggle;
    private DrawerLayout dLayout;
    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        Toast.makeText(getApplicationContext(), "IT'S LOADING", Toast.LENGTH_LONG).show();

        // Call to API and retrieve match data
        String stringUrl = "https://api.steampowered.com/IEconDOTA2_570/GetGameItems/V001/?key=F7EB0CA4154233ABB155C2C98DEF9D02&language=en_us";

        new DownloadWebpageTask().execute(stringUrl);

        mainListView = (ListView) findViewById(R.id.navList2);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        dLayout = (DrawerLayout) findViewById(R.id.item_layout);
        setUpDrawer();

        String[] items = new String[]{"Recent", "Heroes", "Items"};

        ArrayList<String> itemList = new ArrayList<String>();

        itemList.addAll(Arrays.asList(items));

        listAdapter = new ArrayAdapter<String>(this, R.layout.row, itemList);

        mainListView.setAdapter(listAdapter);

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(Items.this, MainActivity.class);
                    startActivity(intent);
                }
                if (position == 1) {
                    Intent intent = new Intent(Items.this, Heroes.class);
                    startActivity(intent);
                }
                if (position == 2) {
                    Intent intent = new Intent(Items.this, Items.class);
                    startActivity(intent);
                }

            }
        });


    }

    private void initializeItemList(String[] itemArray, String[] idArray){
        ListView heroes = (ListView) findViewById(R.id.listView2);

        itemsArray = itemArray;

        ArrayList<HashMap<String,String>> heroList = new ArrayList<HashMap<String,String>>();
        for(int i =0; i < itemArray.length; i++){
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("HeroName", itemArray[i]);
            map.put("HeroID", idArray[i]);
            heroList.add(map);

        }
        SimpleAdapter adapter = new SimpleAdapter(this, heroList, android.R.layout.simple_list_item_2, new String[]{"HeroName", "HeroID"}, new int[] {android.R.id.text1, android.R.id.text2});
        heroes.setAdapter(adapter);

        heroes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Items.this, HeroDetail.class);
                intent.putExtra("id", itemsArray[position]);
                startActivity(intent);
            }
        });
//        heroList.addAll(Arrays.asList(itemArray));
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row, heroList);
//
//        heroes.setAdapter(adapter);
    }

    private void setUpDrawer(){
        dToggle = new ActionBarDrawerToggle(this, dLayout,
                R.string.open, R.string.close) {
        };
        dToggle.setDrawerIndicatorEnabled(true);
        dLayout.setDrawerListener(dToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items, menu);
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

            String result = readIt(in);
            return result;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private String readIt(InputStream stream) throws IOException {
        Reader reader = null;
        BufferedReader bufReader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        bufReader = new BufferedReader(reader);

        String line;
        String result = "";
        while ((line = bufReader.readLine()) != null) {
            result += line;
        }

        return result;
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
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
                    Toast.makeText(getApplicationContext(), "Unable to retrieve web page at the moment", Toast.LENGTH_LONG).show();

                // Start json parser
                JSONObject json = new JSONObject(result);
                JSONArray matches = json.getJSONObject("result").getJSONArray("items");
                String[] name = new String[matches.length()];
                String[] id = new String[matches.length()];

                for (int i = 0; i < matches.length(); i++) {
                    name[i] = matches.getJSONObject(i).getString("localized_name");
                    int temp =  matches.getJSONObject(i).getInt("cost");
                    String t = String.valueOf(temp);
                    id[i] = "Cost: " + t;
                }

                initializeItemList(name, id);
            } catch (Exception ex) {
                Log.d("Problem", ex.getMessage());
            }
        }
    }
}
