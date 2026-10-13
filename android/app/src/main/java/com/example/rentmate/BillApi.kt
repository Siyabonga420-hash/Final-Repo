package com.example.rentmate

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class BillShareDto(
    val id: String,
    val userId: String,
    val userName: String,
    val amount: Double,
    val paid: Boolean,
    val paidAt: String?
)

data class BillDto(
    val id: String,
    val name: String,
    val totalAmount: Double,
    val createdAt: String,
    val createdById: String,
    val shares: List<BillShareDto>,
    val yourShare: BillShareDto
)

data class BillsListResponse(val bills: List<BillDto>)
data class BillResponse(val bill: BillDto)
data class ShareResponse(val share: BillShareDto)

data class CreateBillRequest(
    val householdId: String,
    val name: String,
    val totalAmount: Double
)

interface BillApi {
    @GET("api/bills")
    suspend fun getBills(@Query("householdId") householdId: String): BillsListResponse

    @POST("api/bills")
    suspend fun createBill(@Body request: CreateBillRequest): BillResponse

    @PATCH("api/bills/shares/{shareId}/pay")
    suspend fun payShare(@Path("shareId") shareId: String): ShareResponse

    @DELETE("api/bills/{id}")
    suspend fun deleteBill(@Path("id") billId: String)
}