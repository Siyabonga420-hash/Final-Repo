package com.example.rentmate

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class ScheduleEventDto(
    val id: String,
    val event: String,
    val person: String,
    val time: String,
    val householdId: String
)

data class ScheduleListResponse(val events: List<ScheduleEventDto>)
data class ScheduleResponse(val event: ScheduleEventDto)

data class CreateScheduleRequest(
    val householdId: String,
    val event: String,
    val person: String? = null,
    val time: String? = null
)

interface ScheduleApi {
    @GET("api/schedule")
    suspend fun getEvents(@Query("householdId") householdId: String): ScheduleListResponse

    @POST("api/schedule")
    suspend fun createEvent(@Body request: CreateScheduleRequest): ScheduleResponse

    @DELETE("api/schedule/{id}")
    suspend fun deleteEvent(@Path("id") eventId: String)
}