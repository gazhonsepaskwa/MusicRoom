package be.nalebrun.musicroom.viewmodel

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

@HiltViewModel
class SearchViewModel @Inject constructor(
    val apiRepository: IAPIRepository,
    val credentialRepository: ICredentialRepository
) : ViewModel() {

    // search result
    private val _results = MutableStateFlow<List<SearchResponseJson>>(emptyList())
    val results : StateFlow<List<SearchResponseJson>> = _results

    fun search(query: String, offset: Int, limit: Int, filters: List<String> = emptyList()) {
        if (query.isEmpty()) {
            _results.value = emptyList()
            return
        }
        viewModelScope.launch {
            var url: String = ""
            if (filters.isNotEmpty()){
                val serializedFilters: String = filters.joinToString(",")
                url = "https://musicroom.nalebrun.be/search?query=$query&offset=$offset&limit=$limit&type=$serializedFilters"
            }
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
