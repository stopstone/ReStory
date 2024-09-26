package com.cyber.restory.data.repository

import com.cyber.restory.data.model.Notice
import com.cyber.restory.domain.repository.NoticeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeRepositoryImpl @Inject constructor() : NoticeRepository {
    override suspend fun getNotices(): List<Notice> {
        // 실제 구현에서는 여기서 API 호출이나 로컬 데이터베이스 접근 등이 이루어질 것입니다.
        return listOf(
            Notice(
                "시스템 점검 안내",
                "24.05.04",
                "5월 10일 오전 2시부터 4시까지 시스템 점검이 있을 예정입니다. 이용에 참고 부탁드립니다."
            ),
            Notice(
                "신규 기능 업데이트 안내",
                "24.03.04",
                "새로운 기능이 추가되었습니다. 1. 개인화된 추천 시스템 2. 실시간 채팅 기능 3. 다크 모드 지원"
            ),
            Notice(
                "개인정보 처리방침 개정 안내",
                "23.10.04",
                "당사의 개인정보 처리방침이 2023년 11월 1일부터 개정됩니다. 주요 변경사항은 다음과 같습니다: ..."
            ),
            Notice(
                "서비스 이용약관 변경 안내",
                "23.05.04",
                "서비스 이용약관이 변경되었습니다. 주요 변경사항: 1. 서비스 이용 연령 제한 변경 2. 결제 정책 업데이트 3. 콘텐츠 제공 범위 확대"
            )
        )
    }
}