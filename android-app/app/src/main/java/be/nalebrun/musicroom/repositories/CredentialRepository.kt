package be.nalebrun.musicroom.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import be.nalebrun.musicroom.dataStore
import be.nalebrun.musicroom.keys.PreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ICredentialRepository {
    /**
     * Flow to retrieve asynchronously from Datastore the JWT
     * @author nalebrun
     */
    val jwtFlow: Flow<String>

    /**
     * function to store the JWT in datastore
     * @param newToken new value for the JWT
     * @author nalebrun
     */
    suspend fun setJWT(newToken: String)
}

/**
 * Repository to access credential from DataStore
 * @param context ? IDK what it really is.
 * @author nalebrun
 */
class CredentialRepository(context: Context) : ICredentialRepository {
    private var dataStore = context.dataStore

    // Methode
    override val jwtFlow: Flow<String>
        get() = dataStore.data
        .map { prefs -> prefs[PreferenceKey.jwtString] ?: "" }

    override suspend fun setJWT(newToken : String) {
        dataStore.edit { preferences -> preferences[PreferenceKey.jwtString] = newToken }
    }
}