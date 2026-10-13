package com.example.rentmate

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST

data class ProfileUser(val id: String, val fullName: String, val email: String, val phoneNumber: String? = null)
data class ProfileResult(val user: ProfileUser)
data class ProfileUpdate(val fullName: String, val phoneNumber: String)
data class PasswordUpdate(val currentPassword: String, val newPassword: String)

interface ProfileApi {
    @GET("api/me") suspend fun me(): ProfileResult
    @PUT("api/me") suspend fun update(@Body data: ProfileUpdate): ProfileResult
    @POST("api/me/password") suspend fun changePassword(@Body data: PasswordUpdate)
}
