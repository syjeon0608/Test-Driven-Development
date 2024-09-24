package io.hhplus.tdd.point;

import org.springframework.stereotype.Component;

@Component
public class PointValidator {

    public void validateAmount(Long userId, Long amount, String operationType) {
        validateUserId(userId);
        validateAmount(amount, operationType);
    }

    // 유저 ID 유효성 검증
    public void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID 입니다.");
        }
    }

    // 포인트 금액 유효성 검증
    private void validateAmount(Long amount, String operationType) {
        if (amount == null) {
            throw new IllegalArgumentException(operationType + " 금액의 입력이 필요합니다.");
        }
    }

    public void validateCharge(Long userId, Long amount) {
        validateAmount(userId, amount, "충전");
    }

    public void validateUse(Long userId, Long amount) {
        validateAmount(userId, amount, "사용");
    }

}
