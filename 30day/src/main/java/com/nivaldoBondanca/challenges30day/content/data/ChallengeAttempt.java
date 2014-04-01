package com.nivaldoBondanca.challenges30day.content.data;

import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by Nivaldo
 * on 01/04/2014
 */
public class ChallengeAttempt {

    public static final String TABLE_NAME = "challengeAttempt";
    public interface Datatable {
        public static final String NUMBER       = "_id";
        public static final String CHALLENGE_ID = "challenge_id";
        public static final String FIRST_DAY    = "firstDay";
        public static final String STATUS       = "status";
    }

    public static final String PATH = "attempt";

    public static Uri getContentUri(long challengeId) {
        return Uri.withAppendedPath(Challenge.getContentUri(challengeId), PATH);
    }
    public static Uri getContentUri(long challengeId, long attemptNumber) {
        return ContentUris.withAppendedId(getContentUri(challengeId), attemptNumber);
    }
}
