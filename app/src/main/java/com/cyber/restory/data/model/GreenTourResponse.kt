package com.cyber.restory.data.model

data class GreenTourResponse(
    val response: Response
)

data class Response(
    val header: Header,
    val body: Body
)

data class Header(
    val resultCode: String,
    val resultMsg: String
)

data class Body(
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int,
    val items: Items
)

data class Items(
    val item: List<GreenTourItem>
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