package ru.rsue.deals_app.api

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import ru.rsue.deals_app.deals.Deal

interface DealsApi {
    @GET("/api/deals")
    fun getDeals(): Deferred<Response<List<Deal>>>

    @GET("/api/deals/{id}")
    fun getDeal(@Path("id") id: Int): Deferred<Response<Deal>>

    @POST("/api/deals")
    fun createDeal(@Body deal: Deal): Deferred<Response<Deal>>

    @PUT("/api/deals/{id}")
    fun updateDeal(@Path("id") id: Int, @Body deal: Deal): Deferred<Response<Deal>>

    @DELETE("/api/deals/{id}")
    fun deleteDeal(@Path("id") id: Int): Deferred<Response<Unit>>
}

