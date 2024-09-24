package io.hhplus.tdd.point;

import org.springframework.stereotype.Component;

@Component
public class PointValidator {

    // 포인트 충전 요청 검증
    public void validateCharge(Long userId, Long amount) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("유효 하지 않은 사용자ID 입니다.");
        }
        if (amount == null) {
            throw new IllegalArgumentException("충전 금액의 입력이 필요합니다.");
        }
    }

}
