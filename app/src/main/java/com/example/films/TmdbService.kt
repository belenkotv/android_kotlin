package com.example.films

import android.app.Service
import android.content.Intent
import android.os.IBinder

class TmdbService : Service() {

    companion object {
        const val BROADCAST_DATA_CHANGED: String = "com.example.films.intent.action.data_changed"
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}