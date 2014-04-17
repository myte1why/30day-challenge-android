package info.nivaldoBondanca.challenges30day.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import info.nivaldoBondanca.challenges30day.ChallengeAdapter;
import info.nivaldoBondanca.challenges30day.R;
import info.nivaldoBondanca.challenges30day.content.data.Challenge;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeAttempt;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeAttemptDay;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeStatus;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link ChallengeListFragment.OnChallengeListInteractionListener}
 * interface.
 */
public class ChallengeListFragment extends Fragment
        implements AbsListView.OnItemClickListener, AbsListView.OnItemLongClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public enum ListType {
        COMPLETE,
        ON_GOING,
        ALL
    }

    // Arguments
    private static final String ARG_LIST_TYPE = "arg.LIST_TYPE";

    public static ChallengeListFragment newInstance(ListType type) {
        // Prepare the arguments
        Bundle args = new Bundle();
        args.putInt(ARG_LIST_TYPE, type.ordinal());

        // Create the fragment
        ChallengeListFragment fragment = new ChallengeListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private OnChallengeListInteractionListener mListener;

    private AbsListView               mListView;
    private ChallengeAdapter          mAdapter;
    private ContentLoadingProgressBar mLoadingView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Null check
        if (getArguments() == null) {
            throw new IllegalStateException("A list type MUST be specified");
        }

        mAdapter = new ChallengeAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Prepare the list empty state
        ListType type = ListType.values()[getArguments().getInt(ARG_LIST_TYPE)];
        setEmptyView(type);

        // Display loading status
        mLoadingView = (ContentLoadingProgressBar) getView().findViewById(android.R.id.progress);
        setListShown(false);

        getView().findViewById(android.R.id.empty).setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(getArguments().getInt(ARG_LIST_TYPE), null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnChallengeListInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnChallengeListInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setListShown(boolean shown) {
        if (shown) {
            mLoadingView.hide();
        }
        else {
            mLoadingView.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onChallengeClick(mListView, position, id);
        }
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            return mListener.onChallengeLongClick(mListView, position, id);
        }
        return false;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Hide the list, as new content is loaded
        setListShown(false);

        Uri uri = Challenge.getContentUri();
        String[] projection = null;
        String selection = null;

        switch (ListType.values()[id]) {
            case COMPLETE:
                projection = new String[] {
                        ChallengeAttempt.Columns.FULL_NUMBER,
                        ChallengeAttempt.Columns.FULL_CHALLENGE_ID,
                        Challenge.Columns.FULL_NAME,
                        "("+ChallengeAttempt.Columns.FULL_FIRST_DAY+" + date('now','+30 days')) AS "+ChallengeAttempt.EVENT_DAY,
                };
                selection = ChallengeAttempt.Columns.FULL_STATUS+"="+ChallengeStatus.COMPLETED.ordinal();
                break;
            case ON_GOING:
                projection = new String[] {
                        ChallengeAttempt.Columns.FULL_NUMBER,
                        ChallengeAttempt.Columns.FULL_CHALLENGE_ID,
                        Challenge.Columns.FULL_NAME,
                        ChallengeAttemptDay.Columns.FULL_DAY_NUMBER,
                        ChallengeAttemptDay.Columns.FULL_STATUS
                };
                selection = ChallengeAttempt.Columns.FULL_STATUS+"="+ChallengeStatus.ON_GOING.ordinal();
                break;
            case ALL:
                projection = new String[] {
                        ChallengeAttempt.Columns.FULL_NUMBER,
                        ChallengeAttempt.Columns.FULL_CHALLENGE_ID,
                        Challenge.Columns.FULL_NAME,
                        ChallengeAttemptDay.Columns.FULL_DAY_NUMBER,
                        ChallengeAttemptDay.Columns.FULL_STATUS
                };
                break;
        }

        View v = getView();
        if (mAdapter.isEmpty() && v != null) {
            v.findViewById(android.R.id.empty).setVisibility(View.GONE);
        }

        return new CursorLoader(getActivity(), uri, projection, selection, null, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        // Display the list
        setListShown(true);

        View v = getView();
        if (mAdapter.isEmpty() && v != null) {
            v.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyView(ListType type) {
        int image = 0;
        int emptyText = 0;
        int actionText = 0;
        int actionImage = 0;
        View.OnClickListener l = null;

        // TODO fill up the texts and images
        switch (type) {
            case COMPLETE:
                break;
            case ON_GOING:
                break;
            case ALL:
                break;
        }

        View emptyView = getView().findViewById(android.R.id.empty);
        System.out.println(emptyView);

        TextView text = (TextView) emptyView.findViewById(R.id.empty_text);
        if (emptyText > 0) {
            text.setText(emptyText);
        }
        text.setCompoundDrawablesWithIntrinsicBounds(0, image, 0, 0);

        text = (TextView) emptyView.findViewById(R.id.empty_action);
        if (actionText > 0) {
            text.setText(actionText);
        }
        text.setCompoundDrawablesWithIntrinsicBounds(0, actionImage, 0, 0);
        text.setOnClickListener(l);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnChallengeListInteractionListener {
        public void onChallengeClick(AbsListView listView, int position, long id);
        public boolean onChallengeLongClick(AbsListView listView, int position, long id);
    }
}
