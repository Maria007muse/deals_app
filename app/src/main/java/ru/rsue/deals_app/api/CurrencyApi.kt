package ru.rsue.deals_app.api

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import ru.rsue.deals_app.currencies.Currency

interface CurrencyApi {
    @GET("/api/currencies")
    fun getCurrencies(): Deferred<Response<List<Currency>>>

    @GET("/api/currencies/{id}")
    fun getCurrency(@Path("id") id: Int): Deferred<Response<Currency>>

    @POST("/api/currencies")
    fun createCurrency(@Body currency: Currency): Deferred<Response<Currency>>

    @PUT("/api/currencies/{id}")
    fun updateCurrency(@Path("id") id: Int, @Body currency: Currency): Deferred<Response<Currency>>

    @DELETE("/api/currencies/{id}")
    fun deleteCurrency(@Path("id") id: Int): Deferred<Response<Unit>>
}
