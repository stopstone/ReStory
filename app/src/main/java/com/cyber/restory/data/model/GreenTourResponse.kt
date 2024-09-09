package com.cyber.restory.data.model

import com.google.gson.annotations.SerializedName

data class GreenTourResponse(
    val response: GreenTourResponseBody
)

data class GreenTourResponseBody(
    val header: GreenTourHeader,
    val body: GreenTourBody
)

data class GreenTourHeader(
    val resultCode: String,
    val resultMsg: String
)

data class GreenTourBody(
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int,
    @SerializedName("items")
    val itemsWrapper: ItemsWrapper?
)

data class ItemsWrapper(
    @SerializedName("item")
    val items: List<GreenTourItem>?
)

data class GreenTourItem(
    val tel: String,
    val telname: String,
    val title: String,
    val addr: String,
    val areacode: String,
    val mainimage: String,
    val modifiedtime: String,
    val cpyrhtDivCd: String,
    val createdtime: String,
    val contentid: String,
    val sigungucode: String,
    val subtitle: String,
    val summary: String
)