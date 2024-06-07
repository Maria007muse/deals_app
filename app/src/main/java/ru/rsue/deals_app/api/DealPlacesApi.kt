package ru.rsue.deals_app.api

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import ru.rsue.deals_app.places.DealPlace

interface DealPlacesApi {
    @GET("/api/deal_places")
    fun getDealPlaces(): Deferred<Response<List<DealPlace>>>

    @GET("/api/deal_places/{id}")
    fun getDealPlace(@Path("id") id: Int): Deferred<Response<DealPlace>>

    @POST("/api/deal_places")
    fun createDealPlace(@Body dealPlace: DealPlace): Deferred<Response<DealPlace>>

    @PUT("/api/deal_places/{id}")
    fun updateDealPlace(@Path("id") id: Int, @Body dealPlace: DealPlace): Deferred<Response<DealPlace>>

    @DELETE("/api/deal_places/{id}")
    fun deleteDealPlace(@Path("id") id: Int): Deferred<Response<Unit>>
}
