package com.example.rentmate

import retrofit2.http.Body
import retrofit2.http.POST

data class SignUpRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val idNumber: String,
    val phoneNumber: String,
    val accountType: String,
    val studentNumber: String?,
    val emergencyName: String,
    val emergencyPhone: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UserResponse(
    val id: String,
    val fullName: String,
    val email: String
)

data class AuthResponse(
    val user: UserResponse,
    val token: String
)

interface AuthApi {

    @POST("api/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}