package io.hhplus.tdd.point;

import io.hhplus.tdd.point.exception.InvalidChargeAmountException;

public record UserPoint(
        long id,
        long point,
        long updateMillis,
        boolean isEmpty  // 포인트가 0인 정상 유저와 포인트가 0인 미등록 유저를 구분하기 위해 작성
) {

    // 미등록 유저는 포인트가 0이며, isEmpty = true로 설정된다
    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis(), true);
    }

    // 등록된 유저는 isEmpty = false로 설정된다
    public UserPoint(long id, long point, long updateMillis) {
        this(id, point, updateMillis, false);
    }

    // 포인트 충전 후 새로운 인스턴스를 반환하는 메서드
    public UserPoint charge(Long amount) {
        if (amount <= 0) {
            throw InvalidChargeAmountException.amountIsTooLow();
        }
        return new UserPoint(this.id, this.point + amount, System.currentTimeMillis(), false);
    }

}