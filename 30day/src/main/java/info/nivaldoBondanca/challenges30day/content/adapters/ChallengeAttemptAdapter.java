package info.nivaldoBondanca.challenges30day.content.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import info.nivaldoBondanca.challenges30day.R;
import info.nivaldoBondanca.challenges30day.content.adapters.item.ChallengeAttemptInfo;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeAttempt;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeAttemptDay;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeStatus;
import info.nivaldoBondanca.challenges30day.utils.DateTimeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static info.nivaldoBondanca.challenges30day.ThirdyDayChallenges.LOG_TAG;


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
    private int mIndexDay;
    private int mIndexStatus;
    private int mIndexDayStatus;
    private int mIndexFirstDay;


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
        int currentDay = c.getInt(mIndexDay);
        int currentDayStatus = c.getInt(mIndexDayStatus);

        try {
            // Completion date
            DateFormat timeFormat = android.text.format.DateFormat.getLongDateFormat(mContext);
            SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT, Locale.getDefault());
            String date = timeFormat.format(dateFormat.parse(c.getString(mIndexFirstDay)));
            ((TextView) view.findViewById(R.id.challengeAttempt_firstDay))
                    .setText(mContext.getString(R.string.message_challengeAttempt_firstDay, date));
        }
        catch (ParseException e) {
            Toast.makeText(mContext, R.string.error_dateTimeFormat, Toast.LENGTH_LONG).show();
            Log.w(LOG_TAG, mContext.getText(R.string.error_dateTimeFormat).toString(), e);
        }

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
                v.setAlpha(1);
            }
            else if (currentDayStatus == ChallengeStatus.ON_GOING.ordinal() && i+1 == currentDay) {
                v.setBackgroundColor(Color.parseColor("#7700CC22")); // TODO
                v.setAlpha(1);
            }
            else {
                v.setBackgroundColor(Color.TRANSPARENT);
                v.setAlpha(.5f);
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
            mIndexStatus = mCursor.getColumnIndex(ChallengeAttempt.Columns.STATUS);
            mIndexFirstDay = mCursor.getColumnIndex(ChallengeAttempt.Columns.FIRST_DAY);
            mIndexDay = mCursor.getColumnIndex(ChallengeAttemptDay.CURRENT_DAY);
            mIndexDayStatus = mCursor.getColumnIndex(ChallengeAttempt.EVENT_DAY);
        }

        notifyDataSetChanged();
        return oldCursor;
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }
}
