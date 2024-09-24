package io.hhplus.tdd.point;

import org.springframework.stereotype.Component;

@Component
public class PointValidator {

    // 포인트 충전 요청 검증
    public void validateCharge(Long userId, Long amount) {
        validateUserId(userId);
        if (amount == null) {
            throw new IllegalArgumentException("충전 금액의 입력이 필요합니다.");
        }
    }

    //포인트 사용 요청 검증
    public void validateUse(Long userId, Long amount) {
        validateUserId(userId);
        if (amount == null ) {
            throw new IllegalArgumentException("사용 금액의 입력이 필요합니다.");
        }
    }

    // 유저 ID 유효성 검증
    public void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID 입니다.");
        }
    }

}
