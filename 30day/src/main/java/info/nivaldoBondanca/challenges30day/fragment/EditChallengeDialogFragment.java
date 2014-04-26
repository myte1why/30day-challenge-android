package info.nivaldoBondanca.challenges30day.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.*;
import info.nivaldoBondanca.challenges30day.R;
import info.nivaldoBondanca.challenges30day.content.data.Challenge;
import info.nivaldoBondanca.challenges30day.utils.DateTimeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static info.nivaldoBondanca.challenges30day.ThirdyDayChallenges.LOG_TAG;
import static info.nivaldoBondanca.challenges30day.activity.SettingsActivity.*;


/**
 * Created by Nivaldo
 * on 22/04/2014
 */
public class EditChallengeDialogFragment extends DialogFragment
        implements DialogInterface.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final String ARG_CHALLENGE_ID = "arg.CHALLENGE_ID";

    private static final int LOAD_CHALLENGE = 0;

    private static final int VIEW_LOADING = 0;
    private static final int VIEW_CONTENT = 1;

    /**
     * @param challengeId   Use -1 to specify that a new challenge is being created
     */
    public static DialogFragment newInstance(long challengeId) {
        Bundle args = new Bundle();
        args.putLong(ARG_CHALLENGE_ID, challengeId);

        DialogFragment fragment = new EditChallengeDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public static DialogFragment newInstance()
    {
        return newInstance(0);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Setup the title
        long challengeId = getArguments().getLong(ARG_CHALLENGE_ID);
        builder.setTitle(challengeId > 0 ? R.string.title_challenge_edit : R.string.title_challenge_new);

        // Setup the view components
        @SuppressLint("InflateParams")
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_edit_challenge, null);
        builder.setView(view);
        builder.setPositiveButton(R.string.action_save, this);
        builder.setNegativeButton(R.string.action_cancel, this);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    AlertDialog d = (AlertDialog) getDialog();
                    d.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

                    d.findViewById(R.id.challenge_notificationTime).setOnClickListener(EditChallengeDialogFragment.this);
                    d.findViewById(R.id.challenge_snoozeTime).setOnClickListener(EditChallengeDialogFragment.this);
                }
        });
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(LOAD_CHALLENGE, null, this);
    }

    @Override
    public void onClick(View v) {
        Dialog d = getDialog();
        switch (v.getId()) {
            case R.id.challenge_notificationTime: {
                String notificationTime = (String) d.findViewById(R.id.challenge_notificationTime).getTag();
                TimePickerDialogFragment fragment = TimePickerDialogFragment.newInstance(R.string.title_challenge_notificationTime, notificationTime);
                fragment.setOnDismissListener(
                        new TimePicker.OnTimeChangedListener() {
                            @Override
                            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                                // Update data
                                Dialog dialog = getDialog();
                                if (dialog != null) {
                                    // Parse the info
                                    DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity());
                                    Calendar date = Calendar.getInstance();
                                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    date.set(Calendar.MINUTE, minute);
                                    String notificationTime = timeFormat.format(date.getTime());

                                    // Notification time
                                    TextView notificationView = (TextView) dialog.findViewById(R.id.challenge_notificationTime);
                                    notificationView.setTag(String.format(Locale.ENGLISH, "%02d:%02d:00", hourOfDay, minute));
                                    notificationView.setText(notificationTime);
                                }
                            }
                        });
                fragment.show(getFragmentManager(), null);
                break;
            }

            case R.id.challenge_snoozeTime: {
                int snoozeTime = (Integer) d.findViewById(R.id.challenge_snoozeTime).getTag();
                SnoozeTimePickerDialogFragment fragment = SnoozeTimePickerDialogFragment.newInstance(R.string.title_challenge_snoozeTime, snoozeTime);
                fragment.setOnDismissListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        // Update data
                        Dialog dialog = getDialog();
                        if (dialog != null) {
                            TextView snoozeView = (TextView) dialog.findViewById(R.id.challenge_snoozeTime);
                            snoozeView.setTag(hourOfDay*60 + minute);
                            snoozeView.setText(getSnoozeString(hourOfDay, minute));
                        }
                    }
                });
                fragment.show(getFragmentManager(), null);
                break;
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which)
        {
            case DialogInterface.BUTTON_POSITIVE: {
                // Save the challenge!
                ContentValues challenge = new ContentValues();
                Dialog d = getDialog();

                CharSequence name = ((TextView) d.findViewById(R.id.challenge_name)).getText();
                CharSequence description = ((TextView) d.findViewById(R.id.challenge_description)).getText();
                int snoozeTime = (Integer) d.findViewById(R.id.challenge_snoozeTime).getTag();
                boolean notification = ((CheckBox) d.findViewById(R.id.challenge_notification)).isChecked();
                String notificationTime = (String) d.findViewById(R.id.challenge_notificationTime).getTag();

                challenge.put(Challenge.Columns.NAME, name.toString());
                challenge.put(Challenge.Columns.DESCRIPTION, description.toString());
                challenge.put(Challenge.Columns.SNOOZE_INTERVAL, snoozeTime);
                challenge.put(Challenge.Columns.ALARM, notification);
                challenge.put(Challenge.Columns.ALARM_TIME, notificationTime);

                long challengeId = getArguments().getLong(ARG_CHALLENGE_ID);
                ContentResolver contentResolver = getActivity().getContentResolver();
                if (challengeId  > 0) {
                    // Update challenge
                    contentResolver.update(Challenge.getContentUri(challengeId), challenge, null, null);
                }
                else {
                    // Create challenge
                    contentResolver.insert(Challenge.getContentUri(), challenge);
                }

                break;
            }

            case DialogInterface.BUTTON_NEGATIVE:
                // Do nothing, just dismiss the dialog
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Uri uri = Challenge.getContentUri(getArguments().getLong(ARG_CHALLENGE_ID));
        final String[] projection = {
                Challenge.Columns.FULL_ID,
                Challenge.Columns.FULL_NAME,
                Challenge.Columns.FULL_DESCRIPTION,
                Challenge.Columns.FULL_SNOOZE_INTERVAL,
                Challenge.Columns.FULL_ALARM,
                Challenge.Columns.FULL_ALARM_TIME
        };

        return new CursorLoader(getActivity(), uri, projection, null, null, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String name = "";
        String description = "";
        String snoozeTimeString;
        String notificationTime;
        String notificationTimeString = "";
        int snoozeTime;
        boolean notification = true;

        if (data.moveToFirst()) {
            name = data.getString(data.getColumnIndex(Challenge.Columns.NAME));
            description = data.getString(data.getColumnIndex(Challenge.Columns.DESCRIPTION));
            snoozeTime = data.getInt(data.getColumnIndex(Challenge.Columns.SNOOZE_INTERVAL));
            notification = data.getInt(data.getColumnIndex(Challenge.Columns.ALARM)) == 1;
            notificationTime = data.getString(data.getColumnIndex(Challenge.Columns.ALARM_TIME));
        }
        else {
            // This is a new entry
            // use default values
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            snoozeTime = preferences.getInt(PREF_SNOOZE_TIME, PREF_DEFAULT_SNOOZE_TIME);
            notificationTime = preferences.getString(PREF_NOTIFICATION_TIME, PREF_DEFAULT_NOTIFICATION_TIME);
        }

        // Parse the time to display it correctly

        // Snooze time
        int hours = snoozeTime / 60;
        int minutes = snoozeTime % 60;
        snoozeTimeString = getSnoozeString(hours, minutes);

        try {
            // Notification time
            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity());
            SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeUtils.TIME_FORMAT, Locale.getDefault());
            notificationTimeString = timeFormat.format(dateFormat.parse(notificationTime));
        }
        catch (ParseException e) {
            Toast.makeText(getActivity(), R.string.error_dateTimeFormat, Toast.LENGTH_LONG).show();
            Log.w(LOG_TAG, getText(R.string.error_dateTimeFormat).toString(), e);
        }

        Dialog dialog = getDialog();
        if (dialog != null) {
            ((TextView) dialog.findViewById(R.id.challenge_name)).setText(name);
            ((TextView) dialog.findViewById(R.id.challenge_description)).setText(description);
            TextView snoozeView = (TextView) dialog.findViewById(R.id.challenge_snoozeTime);
            snoozeView.setTag(snoozeTime);
            snoozeView.setText(snoozeTimeString);
            ((CheckBox) dialog.findViewById(R.id.challenge_notification)).setChecked(notification);
            TextView notificationView = (TextView) dialog.findViewById(R.id.challenge_notificationTime);
            notificationView.setTag(notificationTime);
            notificationView.setText(notificationTimeString);

            // Remove loading screen
            ((ViewSwitcher) dialog.findViewById(R.id.viewAnimator)).setDisplayedChild(VIEW_CONTENT);
            ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            // add loading screen
            ((ViewSwitcher) dialog.findViewById(R.id.viewAnimator)).setDisplayedChild(VIEW_LOADING);
            ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        }
    }

    private String getSnoozeString(int hours, int minutes) {
        String snoozeTimeString;
        if (hours > 0) {
            // Parse to the format H:mm 'h' (ex: 1:00 h)
            snoozeTimeString = String.format(
                    Locale.getDefault(),
                    getText(R.string.format_time_hourMinute).toString(),
                    hours, minutes);
        }
        else {
            // Parse to the format m 'min' (ex: 15 min)
            snoozeTimeString = String.format(Locale.getDefault(),
                                             getText(R.string.format_time_minute).toString(),
                                             minutes);
        }
        return snoozeTimeString;
    }
}
