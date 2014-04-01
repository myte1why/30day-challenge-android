package com.nivaldoBondanca.challenges30day.content.data;

import android.content.ContentUris;
import android.net.Uri;

import com.nivaldoBondanca.challenges30day.content.ChallengeContentProvider;

/**
 * Created by Nivaldo
 * on 01/04/2014
 */
public class Challenge {

    public static final String TABLE_NAME = "challenge";
    public interface Datatable {
        public static final String _ID             = "_id";
        public static final String NAME            = "name";
        public static final String DESCRIPTION     = "description";
        public static final String ALARM           = "alarm";
        public static final String SNOOZE_INTERVAL = "snoozeInterval";
    }

    enum Status {
        PENDING,
        SKIPPED,
        FAILED,
        COMPLETED;
    }

    public static final String PATH = "challenge";

    public static Uri getContentUri() {
        return Uri.withAppendedPath(ChallengeContentProvider.CONTENT_URI, PATH);
    }
    public static Uri getContentUri(long challengeId) {
        return ContentUris.withAppendedId(getContentUri(), challengeId);
    }

}
