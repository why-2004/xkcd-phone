package com.why.lab7

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.widget.TextView
import android.widget.Toast
import java.util.*
import kotlin.math.abs
import kotlin.math.min


class FallService: Service(), SensorEventListener {
    private var mSensorManager : SensorManager ?= null

    private var mAccelerometer : Sensor ?= null
    private var mLight:Sensor?=null
    private lateinit var tts:TextToSpeech

    private val mBinder = LocalBinder()
    protected var mToast: Toast? = null
    var myService: FallService? = null
    var isBound = false
    var lightLastChange:Boolean?=null
    lateinit var mediaPlayer: MediaPlayer



    inner class LocalBinder : Binder() {
        fun getService(): FallService? {
            return this@FallService
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mLight = mSensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)

        tts = TextToSpeech(applicationContext,
            TextToSpeech.OnInitListener { })

        val l=resources.configuration.locales[0]
        val a = tts.isLanguageAvailable(l)
        if ( a== TextToSpeech.LANG_COUNTRY_AVAILABLE || a== TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE || a== TextToSpeech.LANG_AVAILABLE){
            tts.language = l
        }else{
            tts.language= Locale.ENGLISH
        }
        mSensorManager!!.registerListener(this, this.mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager!!.registerListener(this, this.mLight, SensorManager.SENSOR_DELAY_NORMAL)

        mediaPlayer= MediaPlayer.create(this, R.raw.scream)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSensorManager?.unregisterListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {

        val mySensor: Sensor = sensorEvent!!.sensor
        when (mySensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {


                mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

                val alpha = 0.8

                var gravity= mutableListOf(0.0,0.0,0.0)
                gravity[0] = alpha * gravity[0] + (1 - alpha)* sensorEvent.values[0]
                gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1]
                gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2]


                var linearAcceleration=mutableListOf<Double>(0.0,0.0,0.0)
                linearAcceleration[0] = sensorEvent.values[0] - gravity[0]
                linearAcceleration[1] = sensorEvent.values[1] - gravity[1]
                linearAcceleration[2] = sensorEvent.values[2] - gravity[2]

                println(linearAcceleration.sum())

                if (((linearAcceleration.sum()) < 0.5)&&linearAcceleration.sum()>-0) {


                    if (!(mediaPlayer.isPlaying)){
                        mediaPlayer.start()
                    }
                }
            }
            Sensor.TYPE_LIGHT->{
                if(lightLastChange==null){
                    lightLastChange= true
                    print("ok")
                }
                else if ((!(lightLastChange as Boolean))&&sensorEvent.values[0]>70){
                    lightLastChange=true
                    print("hi")
                    if (!tts.isSpeaking){
                        tts.speak(getString(R.string.hi), TextToSpeech.QUEUE_ADD,null,"Hi")
                    }
                }
                else if (sensorEvent.values[0]<70 && lightLastChange as Boolean){
                    lightLastChange=false
                    print("dark")
                }


            }
        }
    }

    fun speakKotlin(){
        if (!tts.isSpeaking){
        tts.speak(getString(R.string.kotlin),TextToSpeech.QUEUE_ADD,null,"Kotlin")}
    }


}