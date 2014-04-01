package com.nivaldoBondanca.challenges30day.content;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.nivaldoBondanca.challenges30day.ThirdyDayChallenges.LOG_TAG;

/**
 * Created by Nivaldo
 * on 01/04/2014
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME    = "challenges.db";
    private static final int    DATABASE_VERSION = 1;

    private final Context mContext;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = null;
        InputStream inputStream;
        try {
            // Get the input
            inputStream = mContext.getAssets().open("databaseDefinition.sql");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not find the databaseDefinition file, so I crashed the application!");
        }

        try {
            // Read the file
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();
            String line;
            do {
                line = reader.readLine();
                buffer.append(line);
            }
            while (line != null);

            // File is read, now parse the sql
            sql = buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Execute the SQL to create the database
        db.beginTransaction();
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error creating the database!", e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(LOG_TAG, "Updating database from version "+ oldVersion +" to version "+ newVersion);
        switch (oldVersion) {
            case 1:
                Log.i(LOG_TAG, "Updating database to version 2");
                break;
        }
    }
}
