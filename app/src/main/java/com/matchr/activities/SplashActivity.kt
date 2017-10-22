package com.matchr.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ca.allanwang.kau.utils.postDelayed
import ca.allanwang.kau.utils.startActivity

/**
 * Created by Allan Wang on 2017-10-22.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //I know this is bad, but this is for aesthetics
        postDelayed(1000) {
            startActivity(LoginActivity::class.java)
        }
    }
}