package be.nalebrun.musicroom.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import be.nalebrun.musicroom.dataStore
import be.nalebrun.musicroom.keys.PreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface ICredentialRepository {
    suspend fun setJWT(newToken: String)
    suspend fun getJWT() : String
    val jwtFlow: Flow<String>
}

/**
 * Repository to access credential from DataStore (with cache implementation)
 * @author nalebrun
 */
class CredentialRepository(context: Context) : ICredentialRepository{
    private var dataStore = context.dataStore

    // Methode
    override val jwtFlow: Flow<String>
        get() = dataStore.data
        .map { prefs -> prefs[PreferenceKey.jwtString] ?: "" }

    override suspend fun setJWT(newToken : String) {
        dataStore.edit { preferences -> preferences[PreferenceKey.jwtString] = newToken }
    }

    override suspend fun getJWT() : String {
        val prefs = dataStore.data.first() // need to be rewritten without using first for prod i think
        return prefs[PreferenceKey.jwtString] ?: ""
    }

    //
}