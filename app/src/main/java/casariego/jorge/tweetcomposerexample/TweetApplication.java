package casariego.jorge.tweetcomposerexample;

import android.app.Application;

import com.twitter.sdk.android.core.Twitter;

/**
 * Created by jorgecasariego on 31/8/17.
 */

public class TweetApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        Twitter.initialize(this);
    }
}
