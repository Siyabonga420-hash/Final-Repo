package com.example.rentmate

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class GroceryDto(
    val id: String,
    val name: String,
    val quantity: String,
    val buyer: String,
    val bought: Boolean,
    val householdId: String
)

data class GroceriesListResponse(
    val groceries: List<GroceryDto>
)

data class GroceryResponse(
    val grocery: GroceryDto
)

data class CreateGroceryRequest(
    val householdId: String,
    val name: String,
    val quantity: String? = null,
    val buyer: String? = null
)

data class DeleteResponse(
    val success: Boolean
)

interface GroceryApi {

    @GET("api/groceries")
    suspend fun getGroceries(@Query("householdId") householdId: String): GroceriesListResponse

    @POST("api/groceries")
    suspend fun createGrocery(@Body request: CreateGroceryRequest): GroceryResponse

    @PATCH("api/groceries/{id}/toggle")
    suspend fun toggleGrocery(@Path("id") groceryId: String): GroceryResponse

    @DELETE("api/groceries/{id}")
    suspend fun deleteGrocery(@Path("id") groceryId: String): DeleteResponse
}
