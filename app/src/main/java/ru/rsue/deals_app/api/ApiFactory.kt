package ru.rsue.deals_app.api

import android.annotation.SuppressLint
import com.google.gson.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiFactory {
    private val gsonBuilder = GsonBuilder()
    private var retrofit = Retrofit.Builder()
        .baseUrl("https://1acc-87-117-62-8.ngrok-free.app")
        .client(getUnsafeOkHttpClient())
        .addConverterFactory(
            GsonConverterFactory.create(
            gsonBuilder.create()))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
    val dealTypeApi = retrofit.create(DealsTypesApi::class.java)
    val dealPlaceApi = retrofit.create(DealPlacesApi::class.java)
    val dealCurrencyApi = retrofit.create(CurrencyApi::class.java)
    val dealApi = retrofit.create(DealsApi::class.java)
    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            val trustAllCerts: Array<TrustManager> =
                arrayOf<TrustManager>(object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String?
                    ) {
                    }
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String?
                    ) {
                    }
                    override fun getAcceptedIssuers():
                            Array<X509Certificate> =
                        arrayOf()
                })
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory: SSLSocketFactory =
                sslContext.socketFactory
            val httpClient = OkHttpClient.Builder()
            httpClient.sslSocketFactory(sslSocketFactory,
                trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { hostname, session -> true }
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
