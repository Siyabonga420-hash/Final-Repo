package com.example.rentmate

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST

data class HouseholdMemberUserDto(val id: String, val fullName: String, val email: String)
data class HouseholdMemberDto(val id: String, val role: String, val userId: String, val user: HouseholdMemberUserDto? = null)
data class HouseholdDto(
    val id: String,
    val name: String,
    val inviteCode: String? = null,
    val members: List<HouseholdMemberDto>? = null,
    val capacity: Int? = null,
    val occupied: Int? = null,
    val rentCents: Int? = null,
    val status: String? = null
)
data class HouseholdResponse(val household: HouseholdDto, val status: String? = null)
data class HouseholdsListResponse(val households: List<HouseholdDto>)
data class JoinHouseholdRequest(val householdId: String)

data class MemberNameDto(val id: String, val fullName: String)
data class MembersResponse(val members: List<MemberNameDto>)

interface HouseholdApi {
    @GET("api/households/{id}/members")
    suspend fun members(@Path("id") id: String): MembersResponse

    @GET("api/households")
    suspend fun availableHouseholds(): HouseholdsListResponse

    @POST("api/households/join")
    suspend fun joinHousehold(@Body request: JoinHouseholdRequest): HouseholdResponse

    @GET("api/households/mine")
    suspend fun myHouseholds(): HouseholdsListResponse
}
