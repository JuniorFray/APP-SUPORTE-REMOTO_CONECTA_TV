package com.junio.tvremote

import android.content.Intent

object ScreenCaptureStore {
    var resultCode: Int? = null
    var data: Intent? = null

    fun hasPermission(): Boolean {
        return resultCode != null && data != null
    }

    fun clear() {
        resultCode = null
        data = null
    }
}
