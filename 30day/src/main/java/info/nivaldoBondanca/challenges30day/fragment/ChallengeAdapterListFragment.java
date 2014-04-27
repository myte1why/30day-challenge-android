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
import info.nivaldoBondanca.challenges30day.content.adapters.ChallengeAttemptAdapter;
import info.nivaldoBondanca.challenges30day.content.adapters.item.ChallengeAttemptInfo;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeAttempt;
import info.nivaldoBondanca.challenges30day.content.data.ChallengeAttemptDay;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link info.nivaldoBondanca.challenges30day.fragment.ChallengeAdapterListFragment.OnChallengeAttemptListInteractionListener}
 * interface.
 */
public class ChallengeAdapterListFragment extends Fragment
        implements AbsListView.OnItemClickListener,
                   AbsListView.OnItemLongClickListener,
                   LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOAD_CHALLENGE_ATTEMPT = 0;

    // Arguments
    private static final String ARG_CHALLENGE_ID = "arg.CHALLENGE_ID";

    public static ChallengeAdapterListFragment newInstance(long challengeId) {
        // Prepare the arguments
        Bundle args = new Bundle();
        args.putLong(ARG_CHALLENGE_ID, challengeId);

        // Create the fragment
        ChallengeAdapterListFragment fragment = new ChallengeAdapterListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private OnChallengeAttemptListInteractionListener mListener;

    private AbsListView               mListView;
    private ChallengeAttemptAdapter   mAdapter;
    private ContentLoadingProgressBar mLoadingView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Null check
        if (getArguments() == null) {
            throw new IllegalStateException("Challenge ID MUST be specified");
        }

        mAdapter = new ChallengeAttemptAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_attempt_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // TODO Set click listeners
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        registerForContextMenu(mListView);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Prepare the list empty state
        setEmptyView();

        // Display loading status
        mLoadingView = (ContentLoadingProgressBar) getView().findViewById(android.R.id.progress);
        setListShown(false);

        getView().findViewById(android.R.id.empty).setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(LOAD_CHALLENGE_ATTEMPT, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnChallengeAttemptListInteractionListener) activity;
        }
        catch (ClassCastException e) {
            // TODO
//            throw new ClassCastException(
//                    activity.toString()
//                            + " must implement OnChallengeListInteractionListener"
//            );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.challenge_options, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (!getUserVisibleHint()) {
            return false;
        }
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ChallengeAttemptInfo challenge = (ChallengeAttemptInfo) mAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.action_challenge_edit:
                editChallengeAttempt(challenge.attemptNumber);
                return true;

            case R.id.action_challenge_delete:
                // TODO show undo-able toast or confirmation dialog
                getActivity().getContentResolver()
                             .delete(ChallengeAttempt.getContentUri(challenge.challengeID, challenge.attemptNumber), null, null);
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
//            mListener.onChallengeClick(mListView, position, id); TODO
        }
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return null != mListener;
//                && mListener.onChallengeLongClick(mListView, position, id); TODO
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Hide the list, as new content is loaded
        setListShown(false);

        final Uri uri = ChallengeAttempt.getContentUri(getArguments().getLong(ARG_CHALLENGE_ID));
        final String[] projection = new String[]{
                ChallengeAttemptDay.Columns.FULL_CHALLENGE_ID,
                ChallengeAttemptDay.Columns.FULL_ATTEMPT_NUMBER,
                ChallengeAttemptDay.Columns.FULL_DAY_NUMBER,
                ChallengeAttemptDay.Columns.FULL_DAY_NUMBER + " AS " + ChallengeAttempt.EVENT_DAY,
                ChallengeAttempt.Columns.FULL_FIRST_DAY,
                ChallengeAttempt.Columns.FULL_STATUS,
                ChallengeAttemptDay.Columns.FULL_STATUS
        };

        View v = getView();
        if (mAdapter.isEmpty() && v != null) {
            v.findViewById(android.R.id.empty).setVisibility(View.GONE);
        }

        return new CursorLoader(getActivity(), uri, projection, null, null, null);
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
    public void setEmptyView() {
        CharSequence emptyText = "No challenges attempts yet";
        CharSequence actionText = "New Attempt";
        int actionImage = R.drawable.ic_action_new_content;
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newChallengeAttempt();
            }
        };

        View emptyView = getView().findViewById(android.R.id.empty);

        TextView text = (TextView) emptyView.findViewById(R.id.empty_text);
        text.setText(emptyText);

        text = (TextView) emptyView.findViewById(R.id.empty_action);
        text.setText(actionText);
        text.setCompoundDrawablesWithIntrinsicBounds(actionImage, 0, 0, 0);
        text.setOnClickListener(l);
    }

    private void editChallengeAttempt(long attemptNumber) {
        // TODO
//        EditChallengeAttemptDialogFragment.newInstance(challengeId).show(getFragmentManager(), null);
    }

    private void newChallengeAttempt() {
        // TODO
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnChallengeAttemptListInteractionListener {
    }
}
