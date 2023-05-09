package com.example.native_navigation

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.example.native_navigation.Enums.Companion.FULL_SCREEN_ENGINE_ID
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class MyFlutterActivity : FlutterActivity() {

    override fun onResume() {
        super.onResume()
        MethodChannel(FlutterEngineCache.getInstance()[FULL_SCREEN_ENGINE_ID]!!.dartExecutor.binaryMessenger, Enums.CHANNEL)
                .invokeMethod("notifyNavToFlutter", intent.getStringExtra("param"))
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, Enums.CHANNEL)
                .setMethodCallHandler { call: MethodCall, result: MethodChannel.Result ->
                    when (call.method) {
                        "getBatteryLevel" -> {
                            val batteryLevel = batteryLevel
                            if (batteryLevel != -1) {
                                result.success(batteryLevel)
                            } else {
                                result.error("UNAVAILABLE", "Battery level not available.", null)
                            }
                        }
                        "getParam" -> result.success(this@MyFlutterActivity.intent.getStringExtra("param"))
                        "exitFlutter" -> {
                            println(call.arguments)
                            val intent = Intent()
                            intent.putExtra(Intent.EXTRA_TEXT, call.arguments as String)
                            setResult(RESULT_OK, intent)
                            this@MyFlutterActivity.setResult(1, intent)
                            finish()
                        }
                        "getUserId" -> {
                            result.success(1);
                        }
                        else -> result.notImplemented()
                    }
                }
    }

    override fun provideFlutterEngine(context: Context): FlutterEngine? {
        return FlutterEngineCache.getInstance()[FULL_SCREEN_ENGINE_ID]
    }

    private val batteryLevel: Int
        private get() {
            var batteryLevel = -1
            batteryLevel = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            } else {
                val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            }
            return batteryLevel
        }
}