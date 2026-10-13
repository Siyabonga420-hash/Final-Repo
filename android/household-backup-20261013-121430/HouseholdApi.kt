package com.example.rentmate

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class HouseholdMemberUserDto(
    val id: String,
    val fullName: String,
    val email: String
)

data class HouseholdMemberDto(
    val id: String,
    val role: String,
    val userId: String,
    val user: HouseholdMemberUserDto? = null
)

data class HouseholdDto(
    val id: String,
    val name: String,
    val inviteCode: String,
    val members: List<HouseholdMemberDto>? = null
)

data class HouseholdResponse(
    val household: HouseholdDto
)

data class HouseholdsListResponse(
    val households: List<HouseholdDto>
)

data class CreateHouseholdRequest(
    val name: String
)

data class JoinHouseholdRequest(
    val inviteCode: String
)

interface HouseholdApi {

    @POST("api/households")
    suspend fun createHousehold(@Body request: CreateHouseholdRequest): HouseholdResponse

    @POST("api/households/join")
    suspend fun joinHousehold(@Body request: JoinHouseholdRequest): HouseholdResponse

    @GET("api/households/mine")
    suspend fun myHouseholds(): HouseholdsListResponse
}