package info.nivaldoBondanca.challenges30day;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import info.nivaldoBondanca.challenges30day.content.data.Challenge;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeAttemptDay;


/**
 * Created by Nivaldo
 * on 08/04/2014
 */
public class ChallengeAdapter extends BaseAdapter {

    private Context        mContext;
    private Cursor         mCursor;
    private LayoutInflater mLayoutInflater;

    private int mIndexChallengeID;
    private int mIndexAttemptNumber;
    private int mIndexDayNumber;
    private int mIndexChallengeName;

    public ChallengeAdapter(Context context)
    {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }


    @Override
    public int getCount()
    {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public Object getItem(int position)
    {
        mCursor.moveToPosition(position);
        return new ChallengeInfo(
                mCursor.getLong(mIndexChallengeID),
                mCursor.getLong(mIndexAttemptNumber),
                mCursor.getLong(mIndexDayNumber)
        );
    }

    @Override
    public long getItemId(int position)
    {
        mCursor.moveToPosition(position);
        return mCursor.getLong(mIndexDayNumber);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the view is a recycled one
        // If not, instantiate it
        if (convertView == null) {
            // Inflate the new view
            convertView = mLayoutInflater.inflate(R.layout.view_challenge_list_item, parent, false);
        }

        // Move the cursor
        Cursor c = mCursor;
        c.moveToPosition(position);

        // TODO bind the view to the data
        ((TextView) convertView.findViewById(R.id.challenge_name)).setText(c.getString(mIndexChallengeName));
        ((TextView) convertView.findViewById(R.id.challenge_x)).setText(DatabaseUtils.dumpCurrentRowToString(c));

        return convertView;
    }


    /**
     * Changes the data cursor and closes the old one.
     * @param cursor    new data cursor
     */
    public void setCursor(Cursor cursor) {
        Cursor oldCursor = swapCursor(cursor);

        if (oldCursor != null && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }
    /**
     * Changes the data cursor and closes the old one.
     * @param cursor    new data cursor
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
        }

        notifyDataSetChanged();
        return oldCursor;
    }
}
