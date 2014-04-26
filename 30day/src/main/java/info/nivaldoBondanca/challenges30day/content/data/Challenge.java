package info.nivaldoBondanca.challenges30day.content.data;

import android.content.ContentUris;
import android.net.Uri;

import info.nivaldoBondanca.challenges30day.content.ChallengeContentProvider;

/**
 * Created by Nivaldo
 * on 01/04/2014
 */
public class Challenge {

    public static final String TABLE_NAME = "challenge";
    public interface Columns {
        public static final String _ID             = "_id";
        public static final String NAME            = "name";
        public static final String DESCRIPTION     = "description";
        public static final String ALARM           = "alarm";
        public static final String ALARM_TIME      = "alarmTime";
        public static final String SNOOZE_INTERVAL = "snoozeInterval";

        public static final String FULL_ID              = TABLE_NAME+"."+_ID;
        public static final String FULL_NAME            = TABLE_NAME+"."+NAME;
        public static final String FULL_DESCRIPTION     = TABLE_NAME+"."+DESCRIPTION;
        public static final String FULL_ALARM           = TABLE_NAME+"."+ALARM;
        public static final String FULL_ALARM_TIME      = TABLE_NAME+"."+ALARM_TIME;
        public static final String FULL_SNOOZE_INTERVAL = TABLE_NAME+"."+SNOOZE_INTERVAL;
    }

    public static final String PATH = "challenge";

    public static Uri getContentUri() {
        return Uri.withAppendedPath(ChallengeContentProvider.CONTENT_URI, PATH);
    }
    public static Uri getContentUri(long challengeId) {
        return ContentUris.withAppendedId(getContentUri(), challengeId);
    }
}
