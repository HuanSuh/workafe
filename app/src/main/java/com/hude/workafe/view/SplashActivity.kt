package com.hude.workafe.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.hude.workafe.BuildConfig
import com.hude.workafe.R
import io.fabric.sdk.android.Fabric


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if(!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }

        handler.sendEmptyMessageDelayed(0, 1500)
    }

    private val handler = Handler(Handler.Callback { _ ->
        startActivity(Intent(this, MainActivity::class.java))
        finish()
         false
    })

    override fun onBackPressed() {}
}
