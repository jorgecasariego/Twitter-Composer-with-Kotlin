package casariego.jorge.tweetcomposerexample

import com.twitter.sdk.android.tweetcomposer.TweetUploadService
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.support.v4.app.NotificationCompat.getExtras
import android.os.Bundle
import android.widget.Toast


/**
 * Created by jorgecasariego on 31/8/17.
 */
class MyResultReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        var intentExtras = intent.getExtras()

        if (TweetUploadService.UPLOAD_SUCCESS == intent.action) {
            // success
            val tweetId = intentExtras.getLong(TweetUploadService.EXTRA_TWEET_ID)
            Toast.makeText(context, "Success Tweet ", Toast.LENGTH_LONG).show()
        } else if (TweetUploadService.UPLOAD_FAILURE == intent.action) {
            // failure
            val retryIntent = intentExtras.getParcelable<Intent>(TweetUploadService.EXTRA_RETRY_INTENT)
        } else if (TweetUploadService.TWEET_COMPOSE_CANCEL == intent.action) {
            // cancel
        }
    }
}