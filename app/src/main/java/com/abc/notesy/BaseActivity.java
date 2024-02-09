package com.abc.notesy;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;
import androidx.appcompat.app.AppCompatActivity;


public class BaseActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private final float DEFAULT_BRIGHTNESS = 0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the sensor manager and light sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            adjustScreenBrightness(DEFAULT_BRIGHTNESS);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            adjustScreenBrightnessBasedOnAmbientLight(event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used here
    }

    private void adjustScreenBrightnessBasedOnAmbientLight(float lightLevel) {
        float brightnessValue;
        if (lightLevel < 500) {
            brightnessValue = 0.1f + ((lightLevel / 500) * 0.5f);
        } else {
            brightnessValue = 0.6f + ((Math.min(lightLevel, 5000) - 500) / 4000) * 0.4f;
        }
        adjustScreenBrightness(brightnessValue);
    }

    private void adjustScreenBrightness(float brightness) {
        LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightness;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            adjustScreenBrightness(DEFAULT_BRIGHTNESS);
        }
    }
}
