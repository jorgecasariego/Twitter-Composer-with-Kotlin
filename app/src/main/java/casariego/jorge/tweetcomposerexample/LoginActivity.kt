package casariego.jorge.tweetcomposerexample

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import kotlinx.android.synthetic.main.activity_login.*
import android.content.SharedPreferences
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.internal.TwitterApi


class LoginActivity : AppCompatActivity() {
    val TWITTER_APP: String = "My twitter app"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val prefs = getSharedPreferences(TWITTER_APP, Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val secret = prefs.getString("secret", null)

        if(token != null && secret != null){
            val isExpired = TwitterAuthToken(token, secret).isExpired

            if(!isExpired){
                openMainactivity()
            }

        }

        login_button.setCallback(object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                // Do something with result, which provides a TwitterSession for making API calls
                Toast.makeText(this@LoginActivity, "Resultado exitoso!", Toast.LENGTH_LONG).show()

                openMainactivity()
            }

            override fun failure(exception: TwitterException) {
                // Do something on failure
                Toast.makeText(this@LoginActivity, "Fallo al hacer login", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun openMainactivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        login_button.onActivityResult(requestCode, resultCode, data)
    }
}
