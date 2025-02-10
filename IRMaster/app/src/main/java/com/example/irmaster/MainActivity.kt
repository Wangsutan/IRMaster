package com.example.irmaster

import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import com.example.irmaster.ui.theme.IRMasterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IRMasterTheme {
                RemoteControlApp()
            }
        }
    }
}

@Composable
fun RemoteControlApp() {
    val context = LocalContext.current
    val irManager = remember { context.getSystemService<ConsumerIrManager>() }
    var showUnsupported by remember { mutableStateOf(false) }

    // 检查设备是否支持红外功能
    LaunchedEffect(irManager) {
        if (irManager == null || !irManager.hasIrEmitter()) {
            showUnsupported = true
        }
    }

    // 设备不支持红外功能的提示
    if (showUnsupported) {
        // 设备不支持红外功能的提示
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text(
                text = "设备不支持红外功能",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    } else {
        RemoteControlButtons(irManager)
    }
}

@Composable
fun RemoteControlButtons(irManager: ConsumerIrManager?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // 震动：获取 Vibrator 实例
        val context = LocalContext.current
        val vibrator = remember {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION") context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        }

        // 第一行：开关和静音
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RemoteButton("Power", "开关", "23DCDD22", irManager, vibrator)
            RemoteButton("Mute", "静音", "FF0DD22", irManager, vibrator)
        }

        // 第二行：方向键和确定键
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RemoteButton("Left", "左", "6699DD22", irManager, vibrator)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RemoteButton("Up", "上", "35CADD22", irManager, vibrator)
                RemoteButton("OK", "确定", "31CEDD22", irManager, vibrator)
                RemoteButton("Down", "下", "2DD2DD22", irManager, vibrator)
            }
            RemoteButton("Right", "右", "3EC1DD22", irManager, vibrator)
        }

        // 第三行：返回、语音（空白）和菜单
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RemoteButton("Back", "返回", "6A95DD22", irManager, vibrator)
            Box(
                modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center
            ) {
                Text("语音", style = MaterialTheme.typography.bodyMedium)
            }
            RemoteButton("Menu", "菜单", "7D82DD22", irManager, vibrator)
        }

        // 第四行：音量、主页/设置、频道
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 音量（加和减）
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RemoteButton("Volume Up", "音量加", "7F80DD22", irManager, vibrator)
                RemoteButton("Volume Down", "音量减", "7E81DD22", irManager, vibrator)
            }

            // 主页和设置
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RemoteButton("Home", "主页", "7788DD22", irManager, vibrator)
                RemoteButton("Settings", "设置", "728DDD22", irManager, vibrator)
            }

            // 频道（加和减）
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RemoteButton("Channel Up", "频道加", "7A85DD22", irManager, vibrator)
                RemoteButton("Channel Down", "频道减", "7986DD22", irManager, vibrator)
            }
        }
    }
}

@Composable
fun RemoteButton(
    enName: String,
    zhName: String,
    rawData: String,
    irManager: ConsumerIrManager?,
    vibrator: Vibrator
) {
    Button(
        onClick = {
            vibrate(vibrator, duration = 100)

            val pattern = generateNecPattern(rawData)
            if (pattern.isNotEmpty()) {
                irManager?.transmit(38000, pattern)
            }
        }, shape = CircleShape, modifier = Modifier.size(80.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(zhName, style = MaterialTheme.typography.bodyMedium)
            Text(enName, style = MaterialTheme.typography.labelSmall)
        }
    }
}

fun vibrate(vibrator: Vibrator, duration: Long) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                duration, VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        @Suppress("DEPRECATION") vibrator.vibrate(duration)
    }
}

private fun generateNecPattern(hexData: String): IntArray {
    return try {
        val data = hexData.uppercase().replace("0X", "").toLong(16)
        val pattern = mutableListOf<Int>()

        // NEC协议引导脉冲
        pattern.add(9000)  // 9ms脉冲
        pattern.add(4500)  // 4.5ms间隔

        // 添加32位数据（LSB first）
        for (i in 0 until 32) {
            pattern.add(560)  // 脉冲
            val bit = (data shr i) and 1
            pattern.add(if (bit == 1L) 1680 else 560)  // 间隔
        }

        // 结束脉冲
        pattern.add(560)
        pattern.toIntArray()
    } catch (e: Exception) {
        intArrayOf()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRemoteControlApp() {
    IRMasterTheme { RemoteControlApp() }
}
