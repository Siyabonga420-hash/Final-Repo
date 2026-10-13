package com.example.rentmate

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.DELETE

data class ChoreClaimedByDto(
    val id: String,
    val fullName: String
)

data class ChoreDto(
    val id: String,
    val title: String,
    val description: String?,
    val frequency: String,
    val status: String,
    val dueDate: String?,
    val completedAt: String?,
    val householdId: String,
    val claimedById: String?,
    val claimedBy: ChoreClaimedByDto?
)

data class ChoresListResponse(
    val chores: List<ChoreDto>
)

data class ChoreResponse(
    val chore: ChoreDto
)

data class CreateChoreRequest(
    val householdId: String,
    val title: String,
    val description: String? = null,
    val frequency: String = "ONE_OFF",
    val dueDate: String? = null
)

interface ChoreApi {

    @GET("api/chores")
    suspend fun getChores(@Query("householdId") householdId: String): ChoresListResponse

    @POST("api/chores")
    suspend fun createChore(@Body request: CreateChoreRequest): ChoreResponse

    @POST("api/chores/{id}/claim")
    suspend fun claimChore(@Path("id") choreId: String): ChoreResponse

    @POST("api/chores/{id}/complete")
    suspend fun completeChore(@Path("id") choreId: String): ChoreResponse

    @DELETE("api/chores/{id}")
    suspend fun deleteChore(@Path("id") choreId: String): DeleteResponse
}
