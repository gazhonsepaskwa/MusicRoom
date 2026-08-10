package be.nalebrun.musicroom.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import be.nalebrun.musicroom.dataStore
import be.nalebrun.musicroom.keys.PreferenceKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository to manage the settings
 * @author :nalebrun
 */
interface ISettingsRepository {
    /**
     * flow to get the server url from the storage
     * @return the server url
     */
    val serverUrlFlow: Flow<String>

    /**
     * flow to get the device uuid from the storage
     * @return the device uuid
     */
    val deviceUuidFlow: Flow<String?>

    /**
     * flow to get the device name from the storage
     * @return the device name
     */
    val deviceNameFlow: Flow<String?>

    /**
     * flow to get the debug text from the storage
     * @return the debug text
     */
    val debugTextFlow: Flow<String>

    /**
     * Set the server url in the storage
     * @param url the server url
     */
    suspend fun setServerUrl(url: String)

    /**
     * Set the device uuid in the storage
     * @param uuid the device uuid
     */
    suspend fun setDeviceUuid(uuid: String)

    /**
     * Set the device name in the storage
     * @param name the device name
     */
    suspend fun setDeviceName(name: String)

    /**
     * Set the debug text in the storage
     * @param text the debug text
     */
    suspend fun setDebugText(text: String)
}

class SettingsRepository @Inject constructor(
    @ApplicationContext context: Context
) : ISettingsRepository {
    private val dataStore = context.dataStore
    private val defaultServerUrl = "musicroom.nalebrun.be"

    override val serverUrlFlow: Flow<String>
        get() = dataStore.data.map { prefs -> prefs[PreferenceKey.serverUrl] ?: defaultServerUrl }

    override val deviceUuidFlow: Flow<String?>
        get() = dataStore.data.map { it[PreferenceKey.deviceUuid] }

    override val deviceNameFlow: Flow<String?>
        get() = dataStore.data.map { it[PreferenceKey.deviceName] }

    override val debugTextFlow: Flow<String>
        get() = dataStore.data.map { it[PreferenceKey.debugText] ?: "" }

    override suspend fun setDeviceUuid(uuid: String) {
        dataStore.edit { it[PreferenceKey.deviceUuid] = uuid }
    }

    override suspend fun setDeviceName(name: String) {
        dataStore.edit { it[PreferenceKey.deviceName] = name }
    }

    override suspend fun setDebugText(text: String) {
        dataStore.edit { it[PreferenceKey.debugText] = text }
    }

    override suspend fun setServerUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKey.serverUrl] = url
        }
    }
}
