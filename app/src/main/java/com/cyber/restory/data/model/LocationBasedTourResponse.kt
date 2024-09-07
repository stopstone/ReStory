package com.cyber.restory.data.model

data class LocationBasedTourResponse(
    val response: LocationBasedTourResponseBody
)

data class LocationBasedTourResponseBody(
    val header: LocationBasedTourHeader,
    val body: LocationBasedTourBody
)

data class LocationBasedTourHeader(
    val resultCode: String,
    val resultMsg: String
)

data class LocationBasedTourBody(
    val items: LocationBasedTourItems,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class LocationBasedTourItems(
    val item: List<LocationBasedTourItem>
)

data class LocationBasedTourItem(
    val contentid: String,
    val addr1: String,
    val addr2: String,
    val areacode: String,
    val booktour: String,
    val cat1: String,
    val cat2: String,
    val cat3: String,
    val contentTypeId: String?,
    val createdtime: String,
    val dist: String,
    val firstimage: String,
    val firstimage2: String,
    val cpyrhtDivCd: String,
    val mapx: String,
    val mapy: String,
    val mlevel: String,
    val modifiedtime: String,
    val sigungucode: String,
    val tel: String,
    val title: String,
    val distance: Double,
)