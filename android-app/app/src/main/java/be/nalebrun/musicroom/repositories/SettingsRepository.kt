package be.nalebrun.musicroom.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import be.nalebrun.musicroom.dataStore
import be.nalebrun.musicroom.keys.PreferenceKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ISettingsRepository {
    val serverUrlFlow: Flow<String>
    suspend fun setServerUrl(url: String)
    val deviceUuidFlow: Flow<String?>
    val deviceNameFlow: Flow<String?>
    suspend fun setDeviceUuid(uuid: String)
    suspend fun setDeviceName(name: String)
}

class SettingsRepository @Inject constructor(
    @ApplicationContext context: Context
) : ISettingsRepository {
    private val dataStore = context.dataStore
    private val defaultServerUrl = "musicroom.nalebrun.be"

    override val serverUrlFlow: Flow<String>
        get() = dataStore.data
            .map { prefs -> prefs[PreferenceKey.serverUrl] ?: defaultServerUrl }

    override suspend fun setServerUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKey.serverUrl] = url
        }
    }

    override val deviceUuidFlow: Flow<String?>
        get() = dataStore.data.map { it[PreferenceKey.deviceUuid] }

    override val deviceNameFlow: Flow<String?>
        get() = dataStore.data.map { it[PreferenceKey.deviceName] }

    override suspend fun setDeviceUuid(uuid: String) {
        dataStore.edit { it[PreferenceKey.deviceUuid] = uuid }
    }

    override suspend fun setDeviceName(name: String) {
        dataStore.edit { it[PreferenceKey.deviceName] = name }
    }
}
