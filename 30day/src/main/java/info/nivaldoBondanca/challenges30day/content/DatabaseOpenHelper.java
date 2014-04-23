package info.nivaldoBondanca.challenges30day.content;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static info.nivaldoBondanca.challenges30day.ThirdyDayChallenges.LOG_TAG;

/**
 * Created by Nivaldo
 * on 01/04/2014
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME    = "challenges.db";
    private static final int    DATABASE_VERSION = 1;

    private final Context mContext;


    public DatabaseOpenHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = null;
        InputStream inputStream;
        try {
            // Get the input
            inputStream = mContext.getAssets().open("databaseDefinition.sql");
        } catch (IOException e) {
            throw new IllegalStateException("Could not find the databaseDefinition.sql file, so I crashed the application!");
        }

        // Execute the SQL to create the database
        db.beginTransaction();
        Log.i(LOG_TAG, "Creating the database...");

        try {
            // Read the SQL file with the commands to create the database
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder buffer = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("--")) {
                    line = reader.readLine();
                    continue;
                }
                buffer.append(line);

                if (line.endsWith(";")) {
                    // This ends a SQL command, so run it!

                    // Parse the SQL command
                    sql = buffer.toString();

                    // Execute the command
                    db.execSQL(sql);

                    // Clear the buffer
                    buffer.delete(0, buffer.length());
                }

                line = reader.readLine();
            }

            db.setTransactionSuccessful();
            Log.i(LOG_TAG, "Database created successfully!");

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error creating the database!", e);
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
