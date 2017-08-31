package casariego.jorge.tweetcomposerexample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        login_button.onActivityResult(requestCode, resultCode, data)
    }
}
