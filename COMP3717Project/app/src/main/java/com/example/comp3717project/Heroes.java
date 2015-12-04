package com.example.comp3717project;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Heroes extends AppCompatActivity {

    private ActionBarDrawerToggle dToggle;
    private DrawerLayout dLayout;

    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;

    public String[] heroArray;
    public String[] idArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heroes);
        mainListView = (ListView) findViewById(R.id.navList3);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        dLayout = (DrawerLayout) findViewById(R.id.hero_layout);
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
                    Intent intent = new Intent(Heroes.this, MainActivity.class);
                    startActivity(intent);
                }
                if (position == 1) {
                    Intent intent = new Intent(Heroes.this, Heroes.class);
                    startActivity(intent);
                }
                if (position == 2) {
                    Intent intent = new Intent(Heroes.this, Items.class);
                    startActivity(intent);
                }

            }
        });

        initializeHeroList();


    }

    private void initializeHeroList(){
        ListView heroes = (ListView) findViewById(R.id.listView3);
        heroArray = new String[]{"Abbadon", "Alchemist"};
        idArray = new String[]{"Hero id: 102", "Hero id: 73"};

        ArrayList<HashMap<String,String>> heroList = new ArrayList<HashMap<String,String>>();
        for(int i =0; i <heroArray.length; i++){
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("HeroName", heroArray[i]);
            map.put("HeroID", idArray[i]);
            heroList.add(map);

        }
        SimpleAdapter adapter = new SimpleAdapter(this, heroList, android.R.layout.simple_list_item_2, new String[]{"HeroName", "HeroID"}, new int[] {android.R.id.text1, android.R.id.text2});
        heroes.setAdapter(adapter);

        heroes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Heroes.this, HeroDetail.class);
                intent.putExtra("id", heroArray[position]);
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
        getMenuInflater().inflate(R.menu.menu_heroes, menu);
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
}
