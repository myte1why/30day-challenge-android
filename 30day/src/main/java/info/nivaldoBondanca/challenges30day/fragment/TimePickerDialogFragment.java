package info.nivaldoBondanca.challenges30day.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;
import info.nivaldoBondanca.challenges30day.R;
import info.nivaldoBondanca.challenges30day.utils.DateTimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static info.nivaldoBondanca.challenges30day.ThirdyDayChallenges.LOG_TAG;


/**
 * Created by Nivaldo
 * on 25/04/2014
 *
 * To receive the chosen time, set the proper listener with TimePickerDialogFragment.setOnDismissListener
 */
public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static final String ARG_INITIAL_TIME = "arg.INITIAL_TIME";
    public static final String ARG_TITLE        = "arg.TITLE";

    private int mHour;
    private int mMinute;

    /**
     * @param title         The title of the dialog
     * @param initialTime   This should be in the format specified in DateTimeUtils.TIME_FORMAT
     */
    public static TimePickerDialogFragment newInstance(int title, String initialTime) {
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, title);
        args.putString(ARG_INITIAL_TIME, initialTime);

        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }


    private TimePicker.OnTimeChangedListener mOnDismissListener = null;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = 12;
        int minute = 0;

        String time = getArguments().getString(ARG_INITIAL_TIME);
        if (time != null) {
            try {
                Calendar date = Calendar.getInstance();
                date.setTime(new SimpleDateFormat(DateTimeUtils.TIME_FORMAT).parse(time));
                hour = date.get(Calendar.HOUR_OF_DAY);
                minute = date.get(Calendar.MINUTE);
            }
            catch (ParseException e) {
                Toast.makeText(getActivity(), R.string.error_dateTimeFormat, Toast.LENGTH_LONG).show();
                Log.w(LOG_TAG, getText(R.string.error_dateTimeFormat).toString(), e);
            }
        }

        boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(getActivity());
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute, is24HourFormat);

        // Setup the title
        timePickerDialog.setTitle(getArguments().getInt(ARG_TITLE));

        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onTimeChanged(null, mHour, mMinute);
        }
    }
    public void setOnDismissListener(TimePicker.OnTimeChangedListener l) {
        mOnDismissListener = l;
    }
}
