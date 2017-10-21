package com.matchr

import android.app.Application
import com.matchr.utils.L

/**
 * Created by Allan Wang on 2017-10-21.
 */
class MatchrApp : Application() {
    override fun onCreate() {
        L.debug(BuildConfig.DEBUG)
        super.onCreate()
    }
}