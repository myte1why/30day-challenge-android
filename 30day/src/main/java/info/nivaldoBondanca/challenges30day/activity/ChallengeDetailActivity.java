package info.nivaldoBondanca.challenges30day.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import info.nivaldoBondanca.challenges30day.R;
import info.nivaldoBondanca.challenges30day.fragment.ChallengeAdapterListFragment;


public class ChallengeDetailActivity extends ActionBarActivity {

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
                    .add(R.id.container, ChallengeAdapterListFragment.newInstance(challengeId))
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.challenge_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
