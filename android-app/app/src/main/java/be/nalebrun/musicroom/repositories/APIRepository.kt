package be.nalebrun.musicroom

import be.nalebrun.musicroom.repositories.ISettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException
import javax.inject.Inject

interface IAPIRepository {
    /**
     * Get the base URL from settings
     */
    suspend fun getBaseUrl(): String

    /**
     * Make a GET query to a given path or url. If it starts with http, it uses it as is.
     * Otherwise, it prepends the base URL.
     */
    fun get(
        url: String,
        auth: String = "",
        onResponse: (call: Call, response: Response) -> Unit,
        onFailure: (call: Call, e: IOException) -> Unit
    )

    /**
     * Make a POST query to a given url
     * @author nalebrun
     * @param url the url where the request goes
     * @param auth the JWT Token
     * @param body the posted body of the request
     * @param onResponse callback function that execute when the api respond to the request
     * @param onFailure callback function that execute whet there is an error communicating with api
     */
    fun post(
        url: String,
        body: RequestBody,
        auth: String = "",
        onResponse: (call: Call, response: Response) -> Unit,
        onFailure: (call: Call, e: IOException) -> Unit
    )

    /**
     * Make a PATCH query to a given url
     * @param url the url where the request goes
     * @param auth the JWT Token
     * @param body the posted body of the request
     * @param onResponse callback function that execute when the api respond to the request
     * @param onFailure callback function that execute whet there is an error communicating with api
     */
    fun patch(
        url: String,
        body: RequestBody,
        auth: String = "",
        onResponse: (call: Call, response: Response) -> Unit,
        onFailure: (call: Call, e: IOException) -> Unit
    )
}

/**
 * Class to interact with API
 * @author nalebrun
 * @property client (private) hold the connection client
 * @see IAuthRepository
 */
class APIRepository @Inject constructor(
    private val client: OkHttpClient,
    private val settingsRepository: ISettingsRepository
) : IAPIRepository {

    override suspend fun getBaseUrl(): String {
        return settingsRepository.serverUrlFlow.first()
    }

    private fun resolveUrl(url: String): String {
        if (url.startsWith("http")) return url
        val baseUrl = runBlocking { getBaseUrl() }
        return "https://$baseUrl/${url.removePrefix("/")}"
    }

    // Methods
    override fun get(
        url: String,
        auth: String,
        onResponse: (call: Call, response: Response) -> Unit,
        onFailure: (call: Call, e: IOException) -> Unit
    ) {
        val request = Request.Builder()
            .url(resolveUrl(url))
            .addHeader("Authorization", auth)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                onResponse(call,response)
                response.close()
            }
        })
    }

    override fun post(
        url: String,
        body: RequestBody,
        auth: String,
        onResponse: (call: Call, response: Response) -> Unit,
        onFailure: (call: Call, e: IOException) -> Unit
    ) {
        val request = Request.Builder()
            .url(resolveUrl(url))
            .post(body)
            .addHeader("Authorization", auth)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                onResponse(call,response)
                response.close()
            }
        })
    }

    override fun patch(
        url: String,
        body: RequestBody,
        auth: String,
        onResponse: (call: Call, response: Response) -> Unit,
        onFailure: (call: Call, e: IOException) -> Unit
    ) {
        val request = Request.Builder()
            .url(resolveUrl(url))
            .patch(body)
            .addHeader("Authorization", auth)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                onResponse(call,response)
                response.close()
            }
        })
    }
}
