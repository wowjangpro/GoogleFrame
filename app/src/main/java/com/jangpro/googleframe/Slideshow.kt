package com.jangpro.googleframe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.ViewFlipper
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.jangpro.googleframe.jsondata.MediaItems
import com.jangpro.googleframe.jsondata.MyAlbum
import com.jangpro.googleframe.jsondata.MyPhoto
import com.jangpro.googleframe.restful.GetPhotoInterface
import com.jangpro.googleframe.restful.OkHttp3RetrofitManager
import kotlinx.android.synthetic.main.activity_slideshow.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Thread.sleep
import java.util.*
import kotlin.concurrent.schedule


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class Slideshow : AppCompatActivity() {
    var access_token: String?= null
    var album_id: String?= null
    var arrImages: Array<Any>?= null

    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreen_content.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreen_content_controls.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_slideshow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
        fullscreen_content.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        dummy_button.setOnTouchListener(mDelayHideTouchListener)

        getPhotoList()

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(1000)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreen_content_controls.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        fullscreen_content.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }

    private fun getPhotoList() {
        if (intent.hasExtra("album_id")) {
            access_token = intent.getStringExtra("access_token")
            album_id = intent.getStringExtra("album_id")
            Log.d("album_id", "" + access_token)
            Log.d("album_id", "" + album_id)
        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }

        val restClient: GetPhotoInterface =
            OkHttp3RetrofitManager.getRetrofitService(GetPhotoInterface::class.java)

        val currentWeather = restClient.requestPhotoList(
            "/v1/mediaItems:search?access_token=$access_token&key="+getString(R.string.google_api_key),
            ""+album_id
        )

        arrImages = arrayOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRgaMt3LizQuIgGAXe5PG-8H3dlhw5M0-oGu_siUB7ofgdD3ioh",
            "https://cdn.pixabay.com/photo/2015/07/27/19/47/turtle-863336__340.jpg",
            "https://en.es-static.us/upl/2019/01/moon-venus-england-jan2-2019-adrian-strand-300x267.jpg")

        currentWeather.enqueue(object : Callback<MyPhoto> {
            override fun onResponse(call: Call<MyPhoto>?, response: Response<MyPhoto>?) {
                Log.d("photoListResponse=====", ""+response)
                if(response != null && response.isSuccessful) {
                    val gson = Gson()
                    val myPhotoList = gson.toJson(response.body())
                    Log.d("photoListResponse", "" + myPhotoList)

                    val gsonObj = GsonBuilder().setPrettyPrinting().create()
                    val mediaList: MediaItems = gsonObj.fromJson(myPhotoList, object : TypeToken<MediaItems>() {}.type)
                    Log.d("mediaList", "" + myPhotoList[0])
/*
                    for (image in images) {
                        Log.d("typeOfimage", "" + arrImages[image])
                        Glide.with(this@Slideshow).load(arrImages[image]).into(imageView)
                        fllipperImages(arrImages[image])
                        sleep(2000)
                        imageView.setImageResource(0)
                    }
*/
                    /*
                    Handler().postDelayed({
                        Log.d("typeOfimage", "" + arrImages[i])
                        Glide.with(this@Slideshow).load(arrImages[i]).into(imageView)
                        //fllipperImages(arrImages[i])
                        i++
                        if(i==1) i=0
                    }, 1000)
                    repeat(3) {
                        Log.d("typeOfimage", "" + arrImages[i])
                        Glide.with(this@Slideshow).load(arrImages[i]).into(imageView)
                        fullscreen_content.setText(arrImages[i])
                        i++
                        if (i == 2) i = 0
                        sleep(1000)
                    }
                    */
                    showGuest()
                }
                else {
                    Log.d("photoListResponse", "Error")
                }
            }
            override fun onFailure(call: Call<MyPhoto>?, t: Throwable?) {
                Log.d("errorResponse", ""+t.toString())
            }
        })
    }


    var i = 0
    val mDelayHandler: Handler by lazy {
        Handler()
    }

    fun waitGuest(){
        mDelayHandler.postDelayed(::showGuest, 2000) // 10초 후에 showGuest 함수를 실행한다.
    }

    fun showGuest(){
        Log.d("typeOfimage", "" + arrImages!![i])
        Glide.with(this@Slideshow).load(arrImages!![i]).transition(GenericTransitionOptions.with(android.R.anim.slide_in_left)).into(imageView)
        fullscreen_content.setText(arrImages!![i].toString())
        i++
        if (i == 3) i = 0
        waitGuest() // 코드 실행뒤에 계속해서 반복하도록 작업한다.
    }

}
