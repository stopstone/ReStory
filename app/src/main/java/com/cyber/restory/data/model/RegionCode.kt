package com.cyber.restory.data.model

enum class RegionCode(
    val teamCode: String,
    val publicApiCode: String,
    val description: String
) {
    SEOUL("SEOUL", "1", "서울"),
    INCHEON("INCHEON", "2", "인천"),
    DAEJEON("DAEJEON", "3", "대전"),
    DAEGU("DAEGU", "4", "대구"),
    GWANGJU("GWANGJU", "5", "광주"),
    BUSAN("BUSAN", "6", "부산"),
    ULSAN("ULSAN", "7", "울산"),
    GYEONGGI("GYEONGGI", "31", "경기"),
    GANGWON("GANGWON", "32", "강원"),
    CHUNGBUK("CHUNGBUK", "33", "충북"),
    CHUNGNAM("CHUNGNAM", "34", "충남"),
    GYEONGBUK("GYEONGBUK", "35", "경북"),
    GYEONGNAM("GYEONGNAM", "36", "경남"),
    JEONBUK("JEONBUK", "37", "전북"),
    JEONNAM("JEONNAM", "38", "전남"),
    JEJU("JEJU", "39", "제주");

    companion object {
        fun fromTeamCode(code: String): RegionCode? = entries.find { it.teamCode == code }
        fun fromPublicApiCode(code: String): RegionCode? = entries.find { it.publicApiCode == code }
        fun fromDescription(desc: String): RegionCode? = entries.find { it.description == desc }
    }
}
