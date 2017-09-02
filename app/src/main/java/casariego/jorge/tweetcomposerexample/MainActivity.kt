package casariego.jorge.tweetcomposerexample

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.tweetcomposer.ComposerActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    val CAMERA_REQUEST_CODE = 0
    val RESULT_LOAD_IMAGE: Int = 1
    val REQUEST_PERMISSION = 1
    val TWITTER_APP: String = "My twitter app"
    var imageFilePath: String = ""
    var uriImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val session = TwitterCore.getInstance().sessionManager.activeSession as TwitterSession
        val authClient = TwitterAuthClient()

        val authToken = session.authToken
        val token = authToken.token
        val secret = authToken.secret

        val editor = getSharedPreferences(TWITTER_APP, Context.MODE_PRIVATE).edit()
        editor.putString("token", token)
        editor.putString("secret", secret)
        editor.commit()

        authClient.requestEmail(session, object : Callback<String>() {
            override fun failure(exception: TwitterException?) {
                email.setText("Welcome to Twitter")
                openLoginActivity()
            }

            override fun success(result: Result<String>?) {
                email.setText("Welcome " + result?.data)
            }

        })

        my_image.setOnClickListener {
            openGallery()
        }

        fab.setOnClickListener {

            if (uriImage != null) {
                Log.d("TAG", "con imagen")
                val intent = ComposerActivity.Builder(this)
                        .session(session)
                        .image(uriImage)
                        .text(content.text.toString())
                        .hashtags("#" + hashtag.text.toString())
                        .createIntent()

                startActivity(intent)
            } else {
                Log.d("TAG", "sin imagen")
                val intent = ComposerActivity.Builder(this)
                        .session(session)
                        .text(content.text.toString())
                        .hashtags("#" + hashtag.text.toString())
                        .createIntent()

                startActivity(intent)
            }

        }
    }

    private fun openGallery() {
        val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                TwitterCore.getInstance().sessionManager.clearActiveSession()
                val editor = getSharedPreferences(TWITTER_APP, Context.MODE_PRIVATE).edit()
                editor.putString("token", null)
                editor.putString("secret", null)
                editor.commit()
                openLoginActivity()
                return true
            }
            R.id.action_camera -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
                } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION)
                } else {
                    requestCamera()
                }

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestCamera()
            }
        }
    }

    private fun requestCamera() {
        try {
            val imageFile = createImageFile()
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (callCameraIntent.resolveActivity(packageManager) != null) {
                val authorities = packageName + ".fileprovider"
                uriImage = FileProvider.getUriForFile(this, authorities, imageFile)
                callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage)
                startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Could not create file!", Toast.LENGTH_SHORT).show()
        }

    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = "JPEG_" + timeStamp + "_"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!storageDir.exists()) storageDir.mkdirs()
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        imageFilePath = imageFile.absolutePath
        return imageFile
    }

    fun setScaledBitmap(): Bitmap {
        val imageViewWidth = 200
        val imageViewHeight = 200

        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFilePath, bmOptions)
        val bitmapWidth = bmOptions.outWidth
        val bitmapHeight = bmOptions.outHeight

        val scaleFactor = Math.min(bitmapWidth / imageViewWidth, bitmapHeight / imageViewHeight)

        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        return BitmapFactory.decodeFile(imageFilePath, bmOptions)
    }

    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()

            uriImage = Uri.fromFile(File(picturePath));
            my_image.setImageBitmap(BitmapFactory.decodeFile(picturePath))
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            Toast.makeText(this, "Mostrando imagen!", Toast.LENGTH_LONG).show()
            //Toast.makeText(this, "Mostrando imagen: " + File(file?.path).absolutePath, Toast.LENGTH_LONG).show()
            //Log.d("TAG", "File: " + File(file?.path).absolutePath)

//            Glide.with(this)
//                    .load(file)
//                    .into(my_image)
//
//            my_image.setImageBitmap(BitmapFactory.decodeFile(File(file?.path).absolutePath))
            //compressImage()
            if (resultCode == Activity.RESULT_OK) {
                if (setScaledBitmap() != null) {
                    Toast.makeText(this, "Showing image", Toast.LENGTH_LONG).show()
                    my_image.setImageBitmap(setScaledBitmap())
                }else {
                    Toast.makeText(this, "Error while capturing Image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
