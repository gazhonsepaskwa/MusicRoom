package be.nalebrun.musicroom

import android.app.Application
import android.os.StrictMode
import android.util.Log

class MusicRoomApp : Application() {
    companion object {
        const val TAG = "MusicRoomApp"
    }

    override fun onCreate() {
        super.onCreate()

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath() // kill the app when the code violate the strict mode to preserve the phone
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                // no kill in VM to debug
                .build()
        )

        // Global uncaught exception handler
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "Uncaught crash in thread: ${thread.name}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        Log.d(TAG, "Application created")
    }
}
