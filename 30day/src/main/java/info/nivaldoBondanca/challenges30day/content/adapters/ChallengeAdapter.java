package info.nivaldoBondanca.challenges30day.content.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import info.nivaldoBondanca.challenges30day.content.adapters.item.ChallengeInfo;
import info.nivaldoBondanca.challenges30day.R;
import info.nivaldoBondanca.challenges30day.content.data.Challenge;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeAttempt;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeAttemptDay;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeStatus;
import info.nivaldoBondanca.challenges30day.fragment.ChallengeListFragment;
import info.nivaldoBondanca.challenges30day.utils.DateTimeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static info.nivaldoBondanca.challenges30day.ThirdyDayChallenges.LOG_TAG;


/**
 * Created by Nivaldo
 * on 08/04/2014
 */
public class ChallengeAdapter extends BaseAdapter {

    public static final int VIEW_FUTURE   = 0;
    public static final int VIEW_ON_GOING = 1;
    public static final int VIEW_COMPLETE = 2;

    private Context                        mContext;
    private Cursor                         mCursor;
    private LayoutInflater                 mLayoutInflater;
    private ChallengeListFragment.ListType mType;

    private int mIndexChallengeID;
    private int mIndexAttemptNumber;
    private int mIndexDayNumber;
    private int mIndexChallengeName;
    private int mIndexEventDay;
    private int mIndexStatus;


    public ChallengeAdapter(Context context, ChallengeListFragment.ListType type) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        mType = type;
    }


    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        mCursor.moveToPosition(position);
        return new ChallengeInfo(
                mCursor.getLong(mIndexChallengeID),
                mCursor.getLong(mIndexAttemptNumber),
                mCursor.getLong(mIndexDayNumber)
        );
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(mIndexChallengeID);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the view is a recycled one
        // If not, instantiate it
        if (convertView == null) {
            // Inflate the new view
            convertView = mLayoutInflater.inflate(R.layout.view_challenge_list_item, parent, false);
            convertView.findViewById(R.id.challenge_newAttempt).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            long challengeId = (Long) v.getTag();
                            System.out.println("Creating attempt for challengeId = "+challengeId);
                            // TODO
                        }
                    });
        }

        // Move the cursor
        Cursor c = mCursor;
        c.moveToPosition(position);

        // Bind the view to the data
        ((TextView) convertView.findViewById(R.id.challenge_name)).setText(c.getString(mIndexChallengeName));
        ViewAnimator dynamicView = (ViewAnimator) convertView.findViewById(R.id.challenge_dynamicView);
        switch (mType) {
            case COMPLETE: {
                dynamicView.setDisplayedChild(VIEW_COMPLETE);
                try {
                    // Completion date
                    DateFormat timeFormat = android.text.format.DateFormat.getMediumDateFormat(mContext);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT, Locale.getDefault());
                    String date = timeFormat.format(dateFormat.parse(c.getString(mIndexEventDay)));
                    ((TextView) dynamicView.findViewById(R.id.challenge_completeText))
                            .setText(mContext.getString(R.string.message_challenge_completed, date));
                }
                catch (ParseException e) {
                    Toast.makeText(mContext, R.string.error_dateTimeFormat, Toast.LENGTH_LONG).show();
                    Log.w(LOG_TAG, mContext.getText(R.string.error_dateTimeFormat).toString(), e);
                }

                break;
            }


            case ALL:
                if (c.getInt(mIndexStatus) != ChallengeStatus.ON_GOING.ordinal()) {
                    dynamicView.findViewById(R.id.challenge_newAttempt).setTag(c.getLong(mIndexChallengeID));
                    dynamicView.setDisplayedChild(VIEW_FUTURE);
                    break;
                }
            case ON_GOING:
                dynamicView.setDisplayedChild(VIEW_ON_GOING);
                // TODO - The SUPER view
                break;
        }


        return convertView;
    }


    /**
     * Changes the data cursor and closes the old one.
     *
     * @param cursor new data cursor
     */
    public void setCursor(Cursor cursor) {
        Cursor oldCursor = swapCursor(cursor);

        if (oldCursor != null && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }
    /**
     * Changes the data cursor and closes the old one.
     *
     * @param cursor new data cursor
     * @return the old cursor
     */
    public Cursor swapCursor(Cursor cursor) {
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        // Update indexes
        if (mCursor != null) {
            mIndexChallengeID = mCursor.getColumnIndex(ChallengeAttemptDay.Columns.CHALLENGE_ID);
            mIndexAttemptNumber = mCursor.getColumnIndex(ChallengeAttemptDay.Columns.ATTEMPT_NUMBER);
            mIndexDayNumber = mCursor.getColumnIndex(ChallengeAttemptDay.Columns.DAY_NUMBER);
            mIndexChallengeName = mCursor.getColumnIndex(Challenge.Columns.NAME);
            mIndexEventDay = mCursor.getColumnIndex(ChallengeAttempt.EVENT_DAY);
            mIndexStatus = mCursor.getColumnIndex(ChallengeAttempt.Columns.STATUS);
        }

        notifyDataSetChanged();
        return oldCursor;
    }
}
