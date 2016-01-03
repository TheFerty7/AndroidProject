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
    public static final String STUDENT_TABLE = "STUDENT";
    public static final String STUDENT_ID = "ID";
    public static final String STUDENT_NAME = "NAME";

    public AppDatabaseHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + STUDENT_TABLE + " (" +
                "ID_                INTEGER        PRIMARY KEY      AUTOINCREMENT," +
                STUDENT_ID + "      TEXT, " +
                STUDENT_NAME + "    TEXT" +
                ")");
    }

    public void insertStudent(final SQLiteDatabase database,
                              final String id,
                              final String name) {
        final ContentValues studentValue;

        studentValue = new ContentValues();
        studentValue.put(STUDENT_ID, id);
        studentValue.put(STUDENT_NAME, name);
        database.insert(STUDENT_TABLE, null, studentValue);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase database,
                          final int oldVersion,
                          final int newVersion) {
    }
}
