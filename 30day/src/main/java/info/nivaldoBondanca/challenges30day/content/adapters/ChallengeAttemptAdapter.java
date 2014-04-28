package info.nivaldoBondanca.challenges30day.content.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import info.nivaldoBondanca.challenges30day.R;
import info.nivaldoBondanca.challenges30day.content.adapters.item.ChallengeAttemptInfo;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeAttempt;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeStatus;


/**
 * Created by Nivaldo
 * on 27/04/2014
 */
public class ChallengeAttemptAdapter extends PagerAdapter {

    private Context        mContext;
    private Cursor         mCursor;
    private LayoutInflater mLayoutInflater;

    private int mIndexChallengeID;
    private int mIndexAttemptNumber;
    private int mIndexEventDay;
    private int mIndexStatus;
    private int mIndexDayStatus;


    public ChallengeAttemptAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Inflate the new view
        View view = mLayoutInflater.inflate(R.layout.view_challenge_attempt_list_item, container, false);
        // Work around to make the View Pager vertical
        view.setRotation(-90);

        // Move the cursor
        Cursor c = mCursor;
        c.moveToPosition(position);

        // Bind content to the View
        int currentDay = c.getInt(mIndexEventDay);
        int currentDayStatus = c.getInt(mIndexDayStatus);

        // Fill the grid!
        // TODO Implement the final version (this is only a test)
        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.challengeAttempt_dayGrid);
        int childCount = gridLayout.getChildCount();
        if (childCount == 0) {
            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.default_text_padding) / 8;
            for (int i = 0; i < 30; i++) {
                // Prepare the View
                TextView v = new TextView(mContext);
                v.setText(Integer.toString(i+1));
                v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                v.setTypeface(null, Typeface.BOLD);
                v.setPadding(padding, padding, padding, padding);
                v.setGravity(Gravity.CENTER);

                // And its positioning in the grid
                int elementsByRow = 5;
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec    = GridLayout.spec(i / elementsByRow, 1);
                params.columnSpec = GridLayout.spec(i % elementsByRow, 1);

                gridLayout.addView(v, params);
            }

            // Update the child count
            childCount = gridLayout.getChildCount();
        }

        for (int i = 0; i < childCount; i++) {
            View v = gridLayout.getChildAt(i);
            if (currentDayStatus == ChallengeStatus.COMPLETED.ordinal() || i+1 < currentDay) {
                v.setBackgroundResource(R.drawable.ic_check);
                v.setAlpha(1f - (currentDay - i)/31f);
            }
            else if (currentDayStatus == ChallengeStatus.ON_GOING.ordinal() && i+1 == currentDay) {
                v.setBackgroundColor(Color.parseColor("#7700CC22")); // TODO
                v.setAlpha(1);
            }
            else {
                v.setBackgroundColor(Color.TRANSPARENT);
                v.setAlpha(1);
            }
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        GridLayout gridLayout = (GridLayout) container.findViewById(R.id.challengeAttempt_dayGrid);
        gridLayout.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Object getItem(int position) {
        mCursor.moveToPosition(position);
        return new ChallengeAttemptInfo(
                mCursor.getLong(mIndexChallengeID),
                mCursor.getLong(mIndexAttemptNumber)
        );
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
            mIndexChallengeID = mCursor.getColumnIndex(ChallengeAttempt.Columns.CHALLENGE_ID);
            mIndexAttemptNumber = mCursor.getColumnIndex(ChallengeAttempt.Columns.NUMBER);
            mIndexEventDay = mCursor.getColumnIndex(ChallengeAttempt.EVENT_DAY);
            mIndexStatus = mCursor.getColumnIndex(ChallengeAttempt.Columns.STATUS);
            mIndexDayStatus = mCursor.getColumnIndex(ChallengeAttempt.Columns.STATUS);
        }

        notifyDataSetChanged();
        return oldCursor;
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }
}
