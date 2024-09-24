package com.cyber.restory.utils

import com.cyber.restory.presentation.custom.Region
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class MapUtils {

    companion object {


        /**
        * 각 지역별 도청 좌표 구하기
         * @param region Region Model
         * @return Pair<Double, Double> 위도, 경도
        * */
        fun getCoordinate(region: Region?): Pair<Double, Double> {
            return when (region?.code) {
                // 서울
                "SEOUL" -> 37.56677014292466 to 126.97865227425055
                // 경기
                "GYEONGGI" -> 37.26321417323435 to 127.03286213616008
                // 강원
                "GANGWON" -> 37.8853257858209 to 127.729829010354
                // 부산
                "BUSAN" -> 35.179675369619964 to 129.0750113723
                // 인천
                "INCHEON" -> 37.455923629216066 to 126.70536010784367
                // 대구
                "DAEGU" -> 35.8713802646197 to 128.601805491072
                // 대전
                "DAEJEON" -> 36.3505388992836 to 127.38483484675
                // 광주
                "GWANGJU" -> 35.1601037626662 to 126.851629955742
                // 울산
                "ULSAN" -> 35.5395955247058 to 129.311603446508
                // 충북
                "CHUNGBUK" -> 36.63527014888193 to 127.49183021883579
                // 충남
                "CHUNGNAM" -> 36.6588292532864 to 126.672776193822
                // 경남
                "GYEONGNAM" -> 35.2377742104522 to 128.69189688916
                // 경북
                "GYEONGBUK" -> 36.5759962255808 to 128.505799255401
                // 제주
                "JEJU" -> 33.4889179032603 to 126.498229141199
                // 전남
                "JEONNAM" -> 34.816111078663184 to 126.4628078191417
                // 전북
                "JEONBUK" -> 35.8201963639272 to 127.108976712011
                // 그 외 서울
                else -> 37.56677014292466 to 126.97865227425055
            }
        }

        /**
        * 각 지역별 도청명 구하기
         * @param city String 도시명
         * @return String 도청명
        * */
        fun getCityGovernmentBuilding(city: String): String {

            return when (city) {
                // 서울
                "SEOUL" -> "서울특별시청"
                // 경기
                "GYEONGGI" -> "경기도청"
                // 강원
                "GANGWON" -> "강원특별자치도청"
                // 부산
                "BUSAN" -> "부산광역시청"
                // 인천
                "INCHEON" -> "인천광역시청"
                // 대구
                "DAEGU" -> "대구광역시청"
                // 대전
                "DAEJEON" -> "대전광역시청"
                // 광주
                "GWANGJU" -> "광주광역시청"
                // 울산
                "ULSAN" -> "울산광역시청"
                // 충북
                "CHUNGBUK" -> "충청북도청"
                // 충남
                "CHUNGNAM" -> "충청남도청"
                // 경남
                "GYEONGNAM" -> "경상남도청"
                // 경북
                "GYEONGBUK" -> "경상북도청"
                // 제주
                "JEJU" -> "제주특별자치도청"
                // 전남
                "JEONNAM" -> "전라남도청"
                // 전북
                "JEONBUK" -> "전라북도청"
                // 그 외 서울
                else -> "서울특별시청"
            }
        }
        private const val R = 6372.8 * 1000
        /**
        *   두 좌표의 거리를 계산한다.
        * @param lat1 위도1
        * @param lon1 경도1
        * @param lat2 위도2
        * @param lon2 경도2
        * @return 두 좌표의 거리(m)
        * */
        fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val cal = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(lat2))
            val cal2 = 2 * asin(sqrt(cal))
            val result = (R * cal2)
            return result
        }
    }
}