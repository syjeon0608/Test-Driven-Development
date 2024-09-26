package io.hhplus.tdd.point.entity;

import io.hhplus.tdd.point.exception.InvalidChargeAmountException;
import io.hhplus.tdd.point.exception.InvalidUseAmountException;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {


    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    // 포인트 충전 후 새로운 인스턴스를 반환하는 메서드
    public UserPoint charge(Long amount) {
        if (amount <= 0) {
            throw InvalidChargeAmountException.amountIsTooLow();
        }
        return new UserPoint(this.id, this.point + amount, System.currentTimeMillis());
    }

    // 포인트 차감 후 새로운 인스턴스를 반환하는 메서드
    public UserPoint use(Long amount) {
        if (amount <= 0) {
            throw InvalidUseAmountException.amountIsTooLow();
        }
        return new UserPoint(this.id, this.point - amount, System.currentTimeMillis());
    }

}