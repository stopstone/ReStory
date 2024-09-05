package com.cyber.restory.data.api

import com.cyber.restory.data.model.GreenTourResponse
import com.cyber.restory.data.model.LocationBasedTourResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TourApiService {
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

    @GET("B551011/KorService1/locationBasedList1")
    suspend fun getLocationBasedTourInfo(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String,
        @Query("MobileApp") mobileApp: String,
        @Query("_type") type: String,
        @Query("listYN") listYN: String,
        @Query("arrange") arrange: String,
        @Query("mapX") mapX: String,
        @Query("mapY") mapY: String,
        @Query("radius") radius: String,
        @Query("contentTypeId") contentTypeId: String,
        @Query("serviceKey") serviceKey: String
    ): LocationBasedTourResponse
}