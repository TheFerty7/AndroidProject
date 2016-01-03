package com.example.comp3717project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Duy on 20/12/2015.
 */
public class AppDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "dota2_recent_game_analyzer_db";
    public static final int DB_VERSION = 1;
    public static final String HERO_TABLE = "HEROES";
    public static final String HERO_ID = "ID";
    public static final String HERO_NAME = "NAME";

    public static final String ITEM_TABLE = "ITEMS";
    public static final String ITEM_ID = "ID";
    public static final String ITEM_NAME = "NAME";

    public AppDatabaseHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + HERO_TABLE + " (" +
                "ID_                INTEGER        PRIMARY KEY      AUTOINCREMENT," +
                HERO_ID + "      TEXT, " +
                HERO_NAME + "    TEXT" +
                ")");
        database.execSQL("CREATE TABLE " + ITEM_TABLE + " (" +
                "ID_                INTEGER        PRIMARY KEY      AUTOINCREMENT," +
                ITEM_ID + "      TEXT, " +
                ITEM_NAME + "    TEXT" +
                ")");
    }

    public void insertHero(final SQLiteDatabase database,
                              final String id,
                              final String name) {
        final ContentValues HeroValue;

        HeroValue = new ContentValues();
        HeroValue.put(HERO_ID, id);
        HeroValue.put(HERO_NAME, name);
        database.insert(HERO_TABLE, null, HeroValue);
    }

    public void insertItem(final SQLiteDatabase database,
                           final String id,
                           final String name) {
        final ContentValues ItemValue;

        ItemValue = new ContentValues();
        ItemValue.put(ITEM_ID, id);
        ItemValue.put(ITEM_NAME, name);
        database.insert(ITEM_TABLE, null, ItemValue);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase database,
                          final int oldVersion,
                          final int newVersion) {
    }
}
