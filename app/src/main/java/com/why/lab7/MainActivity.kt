package com.why.lab7

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    var myService: FallService? = null
    var isBound = false
    var audioManager:AudioManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, FallService::class.java)
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE)
        audioManager=
            applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    private val myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder
        ) {
            val binder = service as FallService.LocalBinder
            myService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    fun xkcd(view: View) {
        val webpage: Uri = Uri.parse("https://m.xkcd.com/1363/")
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
        for (i in mutableListOf(0,0,0,0,0,0,0,0,0,0)){
            audioManager!!.adjustVolume(AudioManager.ADJUST_RAISE,AudioManager.FLAG_PLAY_SOUND)
        }
        if (isBound ){

            myService!!.speakKotlin()
        }
    }


}
