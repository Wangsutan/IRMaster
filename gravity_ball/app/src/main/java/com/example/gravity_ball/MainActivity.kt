package com.example.gravity_ball

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var gravitySensor: Sensor
    private val gravityData = mutableStateOf(Triple(0.0f, 0.0f, 0.0f)) // 使用 Triple 存储 X, Y, Z

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)!!

        setContent {
            GravityDataScreen(gravityData)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_GRAVITY) {
                val x = it.values[0] // X轴重力值
                val y = it.values[1] // Y轴重力值
                val z = it.values[2] // Z轴重力值
                gravityData.value = Triple(x, y, z) // 更新 Triple
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 不需要处理精度变化
    }
}

@Composable
fun GravityDataScreen(gravityData: MutableState<Triple<Float, Float, Float>>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Gravity Sensor Data", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        val (x, y, z) = gravityData.value // 解构 Triple
        Text(text = "X: ${"%.2f".format(x)}", fontSize = 24.sp)
        Text(text = "Y: ${"%.2f".format(y)}", fontSize = 24.sp)
        Text(text = "Z: ${"%.2f".format(z)}", fontSize = 24.sp)
    }
}
