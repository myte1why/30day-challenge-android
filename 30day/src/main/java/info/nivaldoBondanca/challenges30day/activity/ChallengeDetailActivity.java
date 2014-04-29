package info.nivaldoBondanca.challenges30day.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import info.nivaldoBondanca.challenges30day.R;
import info.nivaldoBondanca.challenges30day.fragment.ChallengeAttemptListFragment;


public class ChallengeDetailActivity extends ActionBarActivity
        implements ChallengeAttemptListFragment.OnChallengeAttemptListInteractionListener {

    public static final String EXTRA_CHALLENGE_ID = "extra.CHALLENGE_ID";

    public static Intent newInstance(Context context, long challengeId) {
        return new Intent(context, ChallengeDetailActivity.class).putExtra(EXTRA_CHALLENGE_ID, challengeId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        // Null check
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new IllegalStateException("Challenge ID MUST be specified");
        }

        long challengeId = extras.getLong(EXTRA_CHALLENGE_ID, 0);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ChallengeAttemptListFragment.newInstance(challengeId))
                    .commit();
        }
    }
}
