package com.example.rentmate

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class MealDto(
    val id: String,
    val day: String,
    val meal: String,
    val cook: String,
    val time: String,
    val householdId: String
)

data class MealsListResponse(val meals: List<MealDto>)
data class MealResponse(val meal: MealDto)

data class CreateMealRequest(
    val householdId: String,
    val day: String? = null,
    val meal: String,
    val cook: String? = null,
    val time: String? = null
)

interface MealApi {
    @GET("api/meals")
    suspend fun getMeals(@Query("householdId") householdId: String): MealsListResponse

    @POST("api/meals")
    suspend fun createMeal(@Body request: CreateMealRequest): MealResponse

    @DELETE("api/meals/{id}")
    suspend fun deleteMeal(@Path("id") mealId: String)
}