package info.nivaldoBondanca.challenges30day.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import info.nivaldoBondanca.challenges30day.R;

import java.util.Locale;


/**
 * Created by Nivaldo
 * on 25/04/2014
 */
public class SnoozeTimePickerDialogFragment extends DialogFragment
        implements NumberPicker.OnValueChangeListener,
                   DialogInterface.OnClickListener {

    public static final String ARG_INITIAL_VALUE = "arg.INITIAL_VALUE";
    public static final String ARG_TITLE         = "arg.TITLE";

    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 96;
    public static final int INTERVAL  = 5;

    public static SnoozeTimePickerDialogFragment newInstance(int title, int initialValue) {
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, title);
        args.putInt(ARG_INITIAL_VALUE, initialValue);

        SnoozeTimePickerDialogFragment fragment = new SnoozeTimePickerDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }


    private TimePicker.OnTimeChangedListener mOnDismissListener;

    private int      mValue;
    private String[] mDisplayValues;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDisplayValues = new String[MAX_VALUE - MIN_VALUE + 1];
        for (int i = 0; i < mDisplayValues.length; i++) {
            int value = (MIN_VALUE+i) * INTERVAL;
            mDisplayValues[i] = getSnoozeString(value/60, value%60);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Setup the title
        builder.setTitle(getArguments().getInt(ARG_TITLE));

        // Get the starting value
        int snoozeTime = getArguments().getInt(ARG_INITIAL_VALUE);

        // Setup the view components
        @SuppressLint("InflateParams")
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_snooze_time_picker, null);

        // Prepare the picker
        NumberPicker picker = (NumberPicker) view.findViewById(R.id.numberPicker);
        picker.setOnValueChangedListener(this);
        picker.setMinValue(MIN_VALUE);
        picker.setMaxValue(MAX_VALUE);
        picker.setWrapSelectorWheel(false);
        picker.setDisplayedValues(mDisplayValues);
        picker.setValue(snoozeTime/5);

        builder.setView(view);
        builder.setPositiveButton(R.string.action_save, this);
        builder.setNegativeButton(R.string.action_cancel, this);

        return builder.create();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        mValue = newVal * INTERVAL;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE && mOnDismissListener != null) {
            mOnDismissListener.onTimeChanged(null, mValue / 60, mValue % 60);
        }
    }

    public void setOnDismissListener(TimePicker.OnTimeChangedListener l) {
        mOnDismissListener = l;
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
