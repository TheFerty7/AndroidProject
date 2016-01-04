package com.example.comp3717project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Duy on 20/12/2015.
 */
public class AppDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "dota2_recent_game_analyzer_db";
    public static final int DB_VERSION = 2;

    public static final String MATCH_TABLE = "DOTA_2_MATCH";
    public static final String MATCH_ID = "ID";
    public static final String MATCH_SEQ_NUM = "SEQ_NUMBER";
    public static final String MATCH_TIME = "MATCH_TIME";
    public static final String MATCH_LOBBY = "LOBBY_TYPE";

    public static final String HERO_TABLE = "DOTA_2_HEROES";
    public static final String HERO_ID = "ID";
    public static final String HERO_NAME = "NAME";

    public static final String ITEM_TABLE = "DOTA_2_ITEMS";
    public static final String ITEM_ID = "ID";
    public static final String ITEM_NAME = "NAME";

    public AppDatabaseHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d("Initial SQLite Instance", "Version " + DB_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + MATCH_TABLE + " (" +
                "ID_                INTEGER         PRIMARY KEY      AUTOINCREMENT, " +
                MATCH_ID + "        TEXT            UNIQUE           ON CONFLICT IGNORE, " +
                MATCH_SEQ_NUM + "   INTEGER         NOT NULL, " +
                MATCH_TIME + "      TEXT            NOT NULL, " +
                MATCH_LOBBY + "     TEXT            NOT NULL" +
                ")");

        database.execSQL("CREATE TABLE " + HERO_TABLE + " (" +
                "ID_                INTEGER        PRIMARY KEY      AUTOINCREMENT," +
                HERO_ID + "         TEXT, " +
                HERO_NAME + "       TEXT )");

        database.execSQL("CREATE TABLE " + ITEM_TABLE + " (" +
                "ID_                INTEGER        PRIMARY KEY      AUTOINCREMENT," +
                ITEM_ID + "         TEXT, " +
                ITEM_NAME + "       TEXT )");
    }

    public void insertMatch(final SQLiteDatabase database,
                            final String id,
                            final long seq_number,
                            final String start_time,
                            final String lobby_type) {
        final ContentValues content;

        content = new ContentValues();
        content.put(MATCH_ID, id);
        content.put(MATCH_SEQ_NUM, seq_number);
        content.put(MATCH_TIME, start_time);
        content.put(MATCH_LOBBY, lobby_type);

        database.insert(MATCH_TABLE, null, content);
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
    public void onUpgrade(final SQLiteDatabase db,
                          final int oldVersion,
                          final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MATCH_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + HERO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
        onCreate(db);

        Log.d("Upgrade SQLite Instance", "Version " + newVersion);
    }
}
