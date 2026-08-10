package be.nalebrun.musicroom.keys

import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * datastore preference keys
 * @author nalebrun
 */
object PreferenceKey {
    val jwtString = stringPreferencesKey("jwtPreferenceKey")
    val serverUrl = stringPreferencesKey("serverUrl")
    val deviceUuid = stringPreferencesKey("deviceUuid")
    val deviceName = stringPreferencesKey("deviceName")
    val debugText = stringPreferencesKey("debugText")
}