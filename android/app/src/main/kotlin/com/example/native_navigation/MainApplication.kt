package com.example.native_navigation

import android.app.Application
import com.example.native_navigation.Enums.Companion.FULL_SCREEN_ENGINE_ID
import com.example.native_navigation.Enums.Companion.PARTIAL_SCREEN_ENGINE_ID
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.view.FlutterMain

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Instantiate a FlutterEngine
        val flutterEnginePartialScreen = FlutterEngine(this)
        val flutterEngineFullScreen = FlutterEngine(this)

        // Pre-warm flutter
        flutterEnginePartialScreen
                .dartExecutor
                .executeDartEntrypoint(
                        DartExecutor.DartEntrypoint(FlutterMain.findAppBundlePath(), "partialScreenEntryPoint")
                )
        flutterEngineFullScreen
                .dartExecutor
                .executeDartEntrypoint(
                        DartExecutor.DartEntrypoint.createDefault()
                )

        // Cache to faster when you re-navigate to flutter
        FlutterEngineCache
                .getInstance()
                .put(PARTIAL_SCREEN_ENGINE_ID, flutterEnginePartialScreen)
        FlutterEngineCache
                .getInstance()
                .put(FULL_SCREEN_ENGINE_ID, flutterEngineFullScreen)

    }
}