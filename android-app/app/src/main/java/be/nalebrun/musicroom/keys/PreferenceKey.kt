package be.nalebrun.musicroom.keys

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKey {
    val jwtString = stringPreferencesKey("jwtPreferenceKey")
}