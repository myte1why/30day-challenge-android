package info.nivaldoBondanca.challenges30day.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import info.nivaldoBondanca.challenges30day.R;
import info.nivaldoBondanca.challenges30day.content.data.Challenge;


/**
 * Created by Nivaldo
 * on 22/04/2014
 */
public class EditChallengeDialogFragment extends DialogFragment
        implements DialogInterface.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_CHALLENGE_ID = "arg.CHALLENGE_ID";
    private static final int    LOAD_CHALLENGE   = 0;

    private static final int VIEW_LOADING = 0;
    private static final int VIEW_CONTENT = 1;

    /**
     * @param challengeId   Use -1 to specify that a new challenge is being created
     */
    public static DialogFragment newInstance(long challengeId)
    {
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


    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Setup the title
        long challengeId = getArguments().getLong(ARG_CHALLENGE_ID);
        builder.setTitle(challengeId > 0 ? R.string.title_challenge_edit : R.string.title_challenge_new);

        // Setup the view components
        builder.setView(getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_edit_challenge, null));
        builder.setPositiveButton(R.string.action_save, this);
        builder.setNegativeButton(R.string.action_cancel, this);

        return builder.create();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(LOAD_CHALLENGE, null, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which)
        {
            case DialogInterface.BUTTON_POSITIVE: {
                // Save the challenge!
                ContentValues challenge = new ContentValues();
                Dialog d = getDialog();

                CharSequence name = ((TextView) d.findViewById(R.id.challenge_name)).getText().toString();
                CharSequence description = ((TextView) d.findViewById(R.id.challenge_description)).getText();
                CharSequence snoozeTime = ((TextView) d.findViewById(R.id.challenge_snoozeTime)).getText();
                CharSequence notificationTime = ((TextView) d.findViewById(R.id.challenge_notificationTime)).getText();

                challenge.put(Challenge.Columns.NAME, name.toString());
                challenge.put(Challenge.Columns.DESCRIPTION, description.toString());
                challenge.put(Challenge.Columns.SNOOZE_INTERVAL, snoozeTime.toString());
                challenge.put(Challenge.Columns.ALARM, notificationTime.toString());

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
                Challenge.Columns.FULL_ALARM
        };

        return new CursorLoader(getActivity(), uri, projection, null, null, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String name = "";
        String description = "";
        String snoozeTime;
        String notificationTime;

        if (data.moveToFirst()) {
            name = data.getString(data.getColumnIndex(Challenge.Columns.NAME));
            description = data.getString(data.getColumnIndex(Challenge.Columns.DESCRIPTION));
            snoozeTime = data.getString(data.getColumnIndex(Challenge.Columns.SNOOZE_INTERVAL));
            notificationTime = data.getString(data.getColumnIndex(Challenge.Columns.ALARM));
        }
        else {
            // This is a new entry
            // use default values
            snoozeTime = "15 min";
            notificationTime = "8:00pm";
        }

        // TODO parse the display date correctly

        Dialog dialog = getDialog();
        if (dialog != null) {
            ((TextView) dialog.findViewById(R.id.challenge_name)).setText(name);
            ((TextView) dialog.findViewById(R.id.challenge_description)).setText(description);
            ((TextView) dialog.findViewById(R.id.challenge_snoozeTime)).setText(snoozeTime);
            ((TextView) dialog.findViewById(R.id.challenge_notificationTime)).setText(notificationTime);

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
}
