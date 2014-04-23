package info.nivaldoBondanca.challenges30day.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import info.nivaldoBondanca.challenges30day.content.data.Challenge;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeAttempt;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeAttemptDay;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

        // Null check
        if (selection == null) {
            selection = "1";
        }

        switch (mUriMatcher.match(uri)) {
            case URI_CHALLENGE_ITEM: {
                long id = ContentUris.parseId(uri);
                selection = Challenge.Columns.FULL_ID+"="+id;
            }
            case URI_CHALLENGE: {
                final String query = String.format(Locale.ENGLISH,
                        "SELECT %s" +
                        " FROM "+Challenge.TABLE_NAME+
                            " LEFT JOIN "+ChallengeAttempt.TABLE_NAME+
                            " ON "+Challenge.Columns.FULL_ID+"="+ChallengeAttempt.Columns.FULL_CHALLENGE_ID+
                            " LEFT JOIN "+ChallengeAttemptDay.TABLE_NAME+
                            " ON "+ChallengeAttemptDay.Columns.FULL_CHALLENGE_ID+"="+Challenge.Columns.FULL_ID+" AND "+ChallengeAttempt.Columns.FULL_NUMBER+"="+ChallengeAttemptDay.Columns.FULL_ATTEMPT_NUMBER+
                        " WHERE %s"+
                        " GROUP BY "+ChallengeAttempt.Columns.FULL_CHALLENGE_ID+", "+ChallengeAttempt.Columns.FULL_NUMBER+
                        " ORDER BY "+ChallengeAttempt.Columns.FULL_FIRST_DAY+" DESC, "+ChallengeAttemptDay.Columns.FULL_DAY_NUMBER+" DESC",
                        getProjectionString(projection), selection);

                cursor = mDatabase.rawQuery(query, selectionArgs);
                break;
            }
            case URI_ATTEMPT_ITEM:
            case URI_ATTEMPT:
            case URI_DAY_ITEM:
            case URI_DAY:
                break;

            default:
                throw new UnsupportedOperationException("Invalid Uri: "+uri);
        }

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        long newId = 0;
        switch (mUriMatcher.match(uri)) {
            case URI_CHALLENGE:
                newId = mDatabase.insert(Challenge.TABLE_NAME, null, values);
                break;
            case URI_ATTEMPT:
            case URI_DAY:
                break;

            default:
                throw new UnsupportedOperationException("Invalid Uri: "+uri);
        }

        // Update uri
        uri = ContentUris.withAppendedId(uri, newId);
        getContext().getContentResolver().notifyChange(uri, null);

        return uri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int result = -1;
        switch (mUriMatcher.match(uri)) {
            case URI_CHALLENGE_ITEM: {
                long id = ContentUris.parseId(uri);
                selection = Challenge.Columns.FULL_ID+"="+id;
            }
            case URI_CHALLENGE:
                result = mDatabase.update(Challenge.TABLE_NAME, values, selection, selectionArgs);
                break;

            case URI_ATTEMPT_ITEM:
            case URI_ATTEMPT:
            case URI_DAY_ITEM:
            case URI_DAY:
                break;

            default:
                throw new UnsupportedOperationException("Invalid Uri: "+uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO: Implement this to handle requests to delete one or more rows.
        int result = -1;
        List<String> pathSegments = uri.getPathSegments();
        switch (mUriMatcher.match(uri)) {
            case URI_CHALLENGE_ITEM:
                selection = Challenge.Columns.FULL_ID+"="+ContentUris.parseId(uri);
            case URI_CHALLENGE:
                result = mDatabase.delete(Challenge.TABLE_NAME, selection, selectionArgs);
                break;

            case URI_ATTEMPT_ITEM:
                selection = ChallengeAttempt.Columns.FULL_NUMBER + "=" + pathSegments.get(3) +
                        " AND " + ChallengeAttempt.Columns.FULL_CHALLENGE_ID + "=" + pathSegments.get(1);
            case URI_ATTEMPT:
                result = mDatabase.delete(ChallengeAttempt.TABLE_NAME, selection, selectionArgs);
                break;

            case URI_DAY_ITEM:
            case URI_DAY:
                break;

            default:
                throw new UnsupportedOperationException("Invalid Uri: "+uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return result;
    }


    private String getProjectionString(String[] projection) {
        String project = "*";
        if (projection != null) {
            project = Arrays.toString(projection);
            // Remove brackets
            project = project.substring(1, project.length()-1);
        }
        return project;
    }
}
