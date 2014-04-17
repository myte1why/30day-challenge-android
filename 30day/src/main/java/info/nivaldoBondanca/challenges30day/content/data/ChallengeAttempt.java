package info.nivaldoBondanca.challenges30day.content.data;

import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by Nivaldo
 * on 01/04/2014
 */
public class ChallengeAttempt {

    public static final String TABLE_NAME = "challengeAttempt";
    public interface Columns {
        public static final String NUMBER       = "_id";
        public static final String CHALLENGE_ID = "challenge_id";
        public static final String FIRST_DAY    = "firstDay";
        public static final String STATUS       = "status";

        public static final String FULL_NUMBER       = TABLE_NAME+"."+NUMBER;
        public static final String FULL_CHALLENGE_ID = TABLE_NAME+"."+CHALLENGE_ID;
        public static final String FULL_FIRST_DAY    = TABLE_NAME+"."+FIRST_DAY;
        public static final String FULL_STATUS       = TABLE_NAME+"."+STATUS;

    }
    /**
     *  Support variable to serve as a label to resulting query in the column
     *  that contains the day of importance.
     *  For example, eventDay will mean the day the challenge was completed in
     *  the case of a list of completed
     */
    public static final String EVENT_DAY = "eventDay";


    public static final String PATH = "attempt";

    public static Uri getContentUri(long challengeId) {
        return Uri.withAppendedPath(Challenge.getContentUri(challengeId), PATH);
    }
    public static Uri getContentUri(long challengeId, long attemptNumber) {
        return ContentUris.withAppendedId(getContentUri(challengeId), attemptNumber);
    }
}
