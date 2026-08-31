package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.SearchResponseJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * The logic for the search page
 * @author nalebrun
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    val apiRepository: IAPIRepository,
    val credentialRepository: ICredentialRepository
) : ViewModel() {

    // List of search results matching the current query
    private val _results = MutableStateFlow<List<SearchResponseJson>>(emptyList())
    val results : StateFlow<List<SearchResponseJson>> = _results

    /**
     * Perform a search query against the API
     * @param query The search string
     * @param offset The starting point for pagination
     * @param limit The maximum number of results to return
     * @param filters Optional list of categories to filter by (e.g., "track", "album", "artist")
     */
    fun search(query: String, offset: Int, limit: Int, filters: List<String> = emptyList()) {
        if (query.isEmpty()) {
            _results.value = emptyList()
            return
        }
        viewModelScope.launch {
            var url: String = ""
            if (filters.isNotEmpty()){
                val serializedFilters: String = filters.joinToString(",")
                url = "search?query=$query&offset=$offset&limit=$limit&type=$serializedFilters"
            }
            //Log.d("API_RESPONSE", url)
            credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
                if (jwt.isNotEmpty() && url.isNotEmpty()) {
                    apiRepository.get(
                        url,
                        "Bearer $jwt",
                        { _, response ->
                            if (response.code in 200..<300) {
                                val body = response.body?.string()
                                if (body != null) {
                                    try {
                                        val parsedResults = Json.decodeFromString<List<SearchResponseJson>>(body)
                                        _results.value = parsedResults
                                        Log.d("API_RESPONSE", "Search results: $parsedResults")
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        },
                        { _, e ->
                            e.printStackTrace()
                        }
                    )
                }
            }
        }
    }
}
