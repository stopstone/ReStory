package com.cyber.restory.data.api

import com.cyber.restory.data.model.GreenTourResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GreenTourApiService {
    @GET("B551011/GreenTourService1/areaBasedList1")
    suspend fun getGreenTourInfo(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String,
        @Query("MobileApp") mobileApp: String,
        @Query("areaCode") areaCode: String,
        @Query("_type") type: String,
        @Query("serviceKey") serviceKey: String
    ): GreenTourResponse
}