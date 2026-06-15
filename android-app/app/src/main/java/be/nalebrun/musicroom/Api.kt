package be.nalebrun.musicroom

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException

/**
 * Class to interact with API
 * @author nalebrun
 * @property client (private) hold the connection client
 */
class Api() {
    // Property
    private val client = OkHttpClient()

    // Methods
    /**
     * Make a GET query to a given url
     * @author nalebrun
     * @param url the url where the request goes
     * @param onResponse callback function that execute when the api respond to the request
     * @param onFailure callback function that execute whet there is an error communicating with api
     */
    fun get(
        url: String,
        onResponse: (call: Call, response: Response) -> Unit,
        onFailure: (call: Call, e: IOException) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                onResponse(call,response)
            }
        })
    }

    /**
     * Make a POST query to a given url
     * @author nalebrun
     * @param url the url where the request goes
     * @param body the posted body of the request
     * @param onResponse callback function that execute when the api respond to the request
     * @param onFailure callback function that execute whet there is an error communicating with api
     */
    fun post(
        url: String,
        body: RequestBody,
        onResponse: (call: Call, response: Response) -> Unit,
        onFailure: (call: Call, e: IOException) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                onResponse(call,response)
            }
        })
    }
}
