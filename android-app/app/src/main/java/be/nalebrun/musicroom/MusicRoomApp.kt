package be.nalebrun.musicroom

import android.app.Application
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import be.nalebrun.musicroom.repositories.ISettingsRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.UUID
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject

@HiltAndroidApp
class MusicRoomApp : Application() {
    companion object {
        const val TAG = "MusicRoomApp"
    }

    @Inject
    lateinit var settingsRepository: ISettingsRepository

    override fun onCreate() {
        super.onCreate()

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                // kill the app when the code violate the strict mode to preserve the phone
                // in comment cause there are system errors that causes strictMode to trigger
                //.penaltyDeath()
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

        val scope = MainScope()
        scope.launch {
            val currentUuid = settingsRepository.deviceUuidFlow.first()
            if (currentUuid == null) {
                val newUuid = UUID.randomUUID().toString()
                settingsRepository.setDeviceUuid(newUuid)
                Log.d(TAG, "Generated new device UUID: $newUuid")
            }

            val currentName = settingsRepository.deviceNameFlow.first()
            if (currentName == null) {
                val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
                settingsRepository.setDeviceName(deviceName)
                Log.d(TAG, "Stored device name: $deviceName")
            }
        }
    }
}
