package com.nivaldoBondanca.challenges30day.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.nivaldoBondanca.challenges30day.content.data.Challenge;
import com.nivaldoBondanca.challenges30day.content.data.ChallengeAttempt;
import com.nivaldoBondanca.challenges30day.content.data.ChallengeAttemptDay;

public class ChallengeContentProvider extends ContentProvider {

    public static final String AUTHORITY   = ChallengeContentProvider.class.getPackage().getName();

    public static final Uri    CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    // Uri codes
    private static final int URI_CHALLENGE      = 100;

    private static final int URI_CHALLENGE_ITEM = 110;
    private static final int URI_ATTEMPT        = 200;
    private static final int URI_ATTEMPT_ITEM   = 201;
    private static final int URI_DAY            = 300;
    private static final int URI_DAY_ITEM       = 301;


    private final UriMatcher     mUriMatcher;
    private       SQLiteDatabase mDatabase;


    public ChallengeContentProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        String path;
        path = Challenge.PATH;
        mUriMatcher.addURI(AUTHORITY, path, URI_CHALLENGE);
        path += "/#";
        mUriMatcher.addURI(AUTHORITY, path, URI_CHALLENGE_ITEM);
        path += "/"+ChallengeAttempt.PATH;
        mUriMatcher.addURI(AUTHORITY, path, URI_ATTEMPT);
        path += "/#";
        mUriMatcher.addURI(AUTHORITY, path, URI_ATTEMPT_ITEM);
        path += "/"+ChallengeAttemptDay.PATH;
        mUriMatcher.addURI(AUTHORITY, path, URI_DAY);
        path += "/#";
        mUriMatcher.addURI(AUTHORITY, path, URI_DAY_ITEM);
    }

    @Override
    public boolean onCreate() {
        // Open the database
        DatabaseOpenHelper openHelper = new DatabaseOpenHelper(getContext());
        mDatabase = openHelper.getWritableDatabase();

        return mDatabase != null && mDatabase.isOpen();
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        Cursor cursor = null;

        switch (mUriMatcher.match(uri)) {
            case URI_CHALLENGE_ITEM:
            case URI_CHALLENGE: {
                String query = "";
                cursor = mDatabase.rawQuery(query, selectionArgs);
                break;
            }
            case URI_ATTEMPT_ITEM:
            case URI_ATTEMPT:
            case URI_DAY_ITEM:
            case URI_DAY:
                break;

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        switch (mUriMatcher.match(uri)) {
            case URI_CHALLENGE:
            case URI_CHALLENGE_ITEM:
            case URI_ATTEMPT:
            case URI_ATTEMPT_ITEM:
            case URI_DAY:
            case URI_DAY_ITEM:
                break;

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        switch (mUriMatcher.match(uri)) {
            case URI_CHALLENGE:
            case URI_CHALLENGE_ITEM:
            case URI_ATTEMPT:
            case URI_ATTEMPT_ITEM:
            case URI_DAY:
            case URI_DAY_ITEM:
                break;

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return -1;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO: Implement this to handle requests to delete one or more rows.
        switch (mUriMatcher.match(uri)) {
            case URI_CHALLENGE:
            case URI_CHALLENGE_ITEM:
            case URI_ATTEMPT:
            case URI_ATTEMPT_ITEM:
            case URI_DAY:
            case URI_DAY_ITEM:
                break;

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return -1;
    }
}
