package ru.rsue.deals_app.api

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import ru.rsue.deals_app.types.DealType

interface DealsTypesApi {
    @GET("/api/deal_types")
    fun getDealTypes(): Deferred<Response<List<DealType>>>

    @GET("/api/deal_types/{id}")
    fun getDealType(@Path("id") id: Int): Deferred<Response<DealType>>

    @POST("/api/deal_types")
    fun createDealType(@Body dealType: DealType): Deferred<Response<DealType>>

    @PUT("/api/deal_types/{id}")
    fun updateDealType(@Path("id") id: Int, @Body dealType: DealType): Deferred<Response<DealType>>

    @DELETE("/api/deal_types/{id}")
    fun deleteDealType(@Path("id") id: Int): Deferred<Response<Unit>>
}





