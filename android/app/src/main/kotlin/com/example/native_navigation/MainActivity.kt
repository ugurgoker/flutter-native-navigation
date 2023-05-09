package com.example.native_navigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.native_navigation.Enums.Companion.PARTIAL_SCREEN_ENGINE_ID
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel

class MainActivity : AppCompatActivity() {
    private var flutterEngine: FlutterEngine? = null

    private var edt: EditText? = null
    private var btnSend: Button? = null
    private var flutterView: FlutterView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edt = findViewById(R.id.edt);
        btnSend = findViewById(R.id.btnSend);
        flutterView = findViewById(R.id.flutter_view);
        setUpFlutter()


        btnSend!!.setOnClickListener {
            sendDataToFlutterModule(edt!!.text.toString())
        }
    }

    private fun setUpFlutter() {
        if (flutterEngine == null) {
            flutterEngine = FlutterEngineCache.getInstance().get(PARTIAL_SCREEN_ENGINE_ID)
            flutterEngine!!
                    .dartExecutor
                    .executeDartEntrypoint(
                            DartExecutor.DartEntrypoint.createDefault()
                    )
        }
        flutterView!!.attachToFlutterEngine(flutterEngine!!)
    }

    private fun sendDataToFlutterModule(param: String) {
        val intent = Intent(this, MyFlutterActivity::class.java)
        intent.putExtra("param", param)
        startActivityForResult(intent, 1)
    }

    override fun onResume() {
        super.onResume()
        flutterEngine!!.lifecycleChannel.appIsResumed()
    }

    override fun onPause() {
        super.onPause()
        flutterEngine!!.lifecycleChannel.appIsInactive()
    }

    override fun onStop() {
        super.onStop()
        flutterEngine!!.lifecycleChannel.appIsPaused()
    }

    override fun onDestroy() {
        flutterView!!.detachFromFlutterEngine()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            var mData = data?.getStringExtra(Intent.EXTRA_TEXT);
            System.out.println(mData)
        }
    }
}