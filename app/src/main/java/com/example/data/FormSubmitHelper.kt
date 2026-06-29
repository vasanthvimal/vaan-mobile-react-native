package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object FormSubmitHelper {
    private const val TAG = "FormSubmitHelper"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    /**
     * Sends form/session registration data to the form endpoints.
     */
    suspend fun sendSubmission(
        name: String,
        email: String,
        company: String?,
        service: String,
        message: String,
        subjectLine: String
    ): Boolean = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("name", name)
            put("email", email)
            if (company != null) {
                put("company", company)
            }
            put("service", service)
            put("message", message)
            put("_subject", subjectLine)
        }

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val formspreeUrl = "https://formspree.io/f/mvzjrrjj"
        val request = Request.Builder()
            .url(formspreeUrl)
            .post(requestBody)
            .build()

        var success = false
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respBody = response.body?.string()
                    Log.i(TAG, "Formspree successful submission: $respBody")
                    success = true
                } else {
                    Log.e(TAG, "Formspree failed with code: ${response.code}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error posting to Formspree", e)
        }

        return@withContext success
    }
}
