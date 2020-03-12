package jp.lopezlab.appi;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.hardware.Sensor.TYPE_HEART_BEAT;
import static android.hardware.Sensor.TYPE_HEART_RATE;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private final String TAG = "APPI Main";
    private TextView heatRateText;
    private TextView messageText;
    private ProgressBar progressBar;
    private SensorManager mySensorManager;
    private Handler handler;
    private int hr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heatRateText = findViewById(R.id.hrText);
        messageText = findViewById(R.id.message);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(20);

        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        Sensor sensor = mySensorManager.getDefaultSensor(TYPE_HEART_RATE);
        mySensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        setAmbientEnabled();
    }

    protected void onStop(){
        super.onStop();
        mySensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        hr = (int)event.values[0];

        /* Output to Logcat */
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Heart rate: " + hr);
            }
        }).start();

        /* Update progress bar */
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (hr > 60) {
                    progressBar.setProgress(hr - 60);
                }
                else {
                    progressBar.setProgress(0);
                }
            }
        }).start();

        /* Update heat rate text */
        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        heatRateText.setText("HR: " + hr);
                    }
                });
            }
        }).start();

        /* Update message */
        if (hr >= 80) {
            messageText.setText("Clear!");
            progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
