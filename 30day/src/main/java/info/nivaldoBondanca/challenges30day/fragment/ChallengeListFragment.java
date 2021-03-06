package info.nivaldoBondanca.challenges30day.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import info.nivaldoBondanca.challenges30day.R;
import info.nivaldoBondanca.challenges30day.content.adapters.ChallengeAdapter;
import info.nivaldoBondanca.challenges30day.content.adapters.item.ChallengeInfo;
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

        mAdapter = new ChallengeAdapter(getActivity(), ListType.values()[getArguments().getInt(ARG_LIST_TYPE)]);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set click listeners
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        registerForContextMenu(mListView);

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ListType type = ListType.values()[getArguments().getInt(ARG_LIST_TYPE)];
        switch (type) {
            case ALL:
            case ON_GOING:
            case COMPLETE:
                inflater.inflate(R.menu.fragment_challenge_list, menu);
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_newContent) {
            newChallenge();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.challenge_options, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(!getUserVisibleHint()) {
            return false;
        }
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ChallengeInfo challenge = (ChallengeInfo) mAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.action_challenge_edit:
                editChallenge(challenge.challengeID);
                return true;

            case R.id.action_challenge_delete:
                // TODO show undo-able toast or confirmation dialog
                getActivity().getContentResolver()
                                 .delete(Challenge.getContentUri(challenge.challengeID), null, null);
                Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onContextItemSelected(item);
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
        return null != mListener && mListener.onChallengeLongClick(mListView, position, id);
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
                        Challenge.Columns.FULL_ID+" AS "+ChallengeAttemptDay.Columns.CHALLENGE_ID,
                        ChallengeAttemptDay.Columns.FULL_ATTEMPT_NUMBER,
                        Challenge.Columns.FULL_NAME,
                        ChallengeAttempt.Columns.FULL_STATUS,
                        "date("+ChallengeAttempt.Columns.FULL_FIRST_DAY+",'+30 days') AS "+ChallengeAttempt.EVENT_DAY
                };
                selection = ChallengeAttempt.Columns.FULL_STATUS+"="+ChallengeStatus.COMPLETED.ordinal();
                break;
            case ON_GOING:
                projection = new String[] {
                        ChallengeAttemptDay.Columns.FULL_DAY_NUMBER,
                        ChallengeAttemptDay.Columns.FULL_ATTEMPT_NUMBER,
                        ChallengeAttemptDay.Columns.FULL_CHALLENGE_ID,
                        Challenge.Columns.FULL_NAME,
                        ChallengeAttempt.Columns.FULL_STATUS,
                        ChallengeAttemptDay.Columns.FULL_STATUS+" AS "+ChallengeAttemptDay.CURRENT_DAY
                };
                selection = ChallengeAttempt.Columns.FULL_STATUS+"="+ChallengeStatus.ON_GOING.ordinal();
                break;
            case ALL:
                projection = new String[] {
                        Challenge.Columns.FULL_ID+" AS "+ChallengeAttemptDay.Columns.CHALLENGE_ID,
                        ChallengeAttemptDay.Columns.FULL_ATTEMPT_NUMBER,
                        ChallengeAttemptDay.Columns.FULL_DAY_NUMBER,
                        Challenge.Columns.FULL_NAME,
                        ChallengeAttempt.Columns.FULL_STATUS,
                        ChallengeAttemptDay.Columns.FULL_STATUS+" AS "+ChallengeAttemptDay.CURRENT_DAY,
                        "date("+ChallengeAttempt.Columns.FULL_FIRST_DAY+",'+30 days') AS "+ChallengeAttempt.EVENT_DAY
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
        else if (v != null) {
            v.findViewById(android.R.id.empty).setVisibility(View.GONE);
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
        CharSequence emptyText = null;
        int emptyImage = 0;
        CharSequence actionText = null;
        int actionImage = 0;
        View.OnClickListener l = null;

        // TODO fill up the images
        switch (type) {
            case COMPLETE:
                emptyText = getText(R.string.empty_challengeList_complete);
                break;
            case ON_GOING:
                emptyText = getText(R.string.empty_challengeList_onGoing);
                actionText = getText(R.string.emptyAction_challengeList_onGoing);
                actionImage = R.drawable.ic_action_new_content;
                l = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.setTab(ListType.ALL.ordinal());
                    }
                };
                break;
            case ALL:
                emptyText = getText(R.string.empty_challengeList_all);
                actionText = getText(R.string.emptyAction_challengeList_all);
                actionImage = R.drawable.ic_action_new_content;
                l = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newChallenge();
                    }
                };
                break;
        }

        View emptyView = getView().findViewById(android.R.id.empty);

        TextView text = (TextView) emptyView.findViewById(R.id.empty_text);
        text.setText(emptyText);
        text.setCompoundDrawablesWithIntrinsicBounds(0, emptyImage, 0, 0);

        text = (TextView) emptyView.findViewById(R.id.empty_action);
        text.setText(actionText);
        text.setCompoundDrawablesWithIntrinsicBounds(actionImage, 0, 0, 0);
        text.setOnClickListener(l);
        if (actionText == null && actionImage == 0) {
            text.setVisibility(View.GONE);
        }
    }

    private void editChallenge(long challengeId) {
        EditChallengeDialogFragment.newInstance(challengeId).show(getFragmentManager(), null);
    }
    private void newChallenge() {
        editChallenge(0);
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
        public void setTab(int position);
        public void onChallengeClick(AbsListView listView, int position, long id);
        public boolean onChallengeLongClick(AbsListView listView, int position, long id);
    }
}
