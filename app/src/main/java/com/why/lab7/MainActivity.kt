package com.why.lab7

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt


class MainActivity : AppCompatActivity(), SensorEventListener {
    private var mSensorManager : SensorManager ?= null

    private var mAccelerometer : Sensor ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {

        val mySensor: Sensor = sensorEvent.sensor
        when (mySensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val event=sensorEvent
                mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

                val alpha = 0.8

                var gravity= mutableListOf<Double>(0.0,0.0,0.0)
                gravity[0] = alpha * gravity[0] + (1 - alpha)
                gravity[0]=gravity[0]* event.values[0]
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]


                var linearAcceleration=mutableListOf<Double>(0.0,0.0,0.0)
                linearAcceleration[0] = event.values[0] - gravity[0]
                linearAcceleration[1] = event.values[1] - gravity[1]
                linearAcceleration[2] = event.values[2] - gravity[2]



                if (((linearAcceleration.sum()) < 1 &&linearAcceleration.sum()>0)||(linearAcceleration.sum()>(-1)&&linearAcceleration.sum()<0)) {

                    print("works")
                    val mediaPlayer: MediaPlayer? = MediaPlayer.create(this, R.raw.scream)
                    mediaPlayer?.start()
                    findViewById<TextView>(R.id.hel).text = "ahhhh"
                }else{
                    findViewById<TextView>(R.id.hel).text = "Hello World!"

                }
            }
            Sensor.TYPE_GYROSCOPE->{




                if (sensorEvent.values.take(3).sum() < 3) {

                    val mediaPlayer: MediaPlayer? = MediaPlayer.create(this, R.raw.scream)
                    mediaPlayer?.start()

                }

            }
        }



    }
    protected override fun onResume() {
        super.onResume();
        mSensorManager?.registerListener(this, this.mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        // repeat that line for each sensor you want to monitor
    }


    protected override fun onPause() {
        super.onPause();
        mSensorManager?.unregisterListener(this);
    }




}
