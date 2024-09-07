package com.cyber.restory.data.model

enum class RegionCode(
    val teamCode: String,
    val publicApiCode: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
) {
    SEOUL("SEOUL", "1", "서울", 37.5666791, 126.9782914),
    INCHEON("INCHEON", "2", "인천", 37.4562557, 126.7052062),
    DAEJEON("DAEJEON", "3", "대전", 36.3504119, 127.3845475),
    DAEGU("DAEGU", "4", "대구", 35.8714354, 128.601445),
    GWANGJU("GWANGJU", "5", "광주", 35.1595454, 126.8526012),
    BUSAN("BUSAN", "6", "부산", 35.1795543, 129.0756416),
    ULSAN("ULSAN", "7", "울산", 35.5383773, 129.3113596),
    GYEONGGI("GYEONGGI", "31", "경기", 37.2749668, 127.0094198),
    GANGWON("GANGWON", "32", "강원", 37.8853763, 127.7329211),
    CHUNGBUK("CHUNGBUK", "33", "충북", 36.6354891, 127.4910155),
    CHUNGNAM("CHUNGNAM", "34", "충남", 36.6578805, 126.6726681),
    GYEONGBUK("GYEONGBUK", "35", "경북", 36.5760207, 128.5056877),
    GYEONGNAM("GYEONGNAM", "36", "경남", 35.2382405, 128.6924204),
    JEONBUK("JEONBUK", "37", "전북", 35.8202, 127.1086),
    JEONNAM("JEONNAM", "38", "전남", 34.8160008, 126.4632557),
    JEJU("JEJU", "39", "제주", 33.4996213, 126.5311884);

    companion object {
        fun fromTeamCode(code: String): RegionCode? = entries.find { it.teamCode == code }
        fun fromPublicApiCode(code: String): RegionCode? = entries.find { it.publicApiCode == code }
        fun fromDescription(desc: String): RegionCode? = entries.find { it.description == desc }
    }
}