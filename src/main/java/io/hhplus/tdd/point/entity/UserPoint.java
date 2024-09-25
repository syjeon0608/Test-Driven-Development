package io.hhplus.tdd.point.entity;

import io.hhplus.tdd.point.exception.InvalidAmountException;

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
            throw InvalidAmountException.chargeAmountIsTooLow();
        }
        if (this.point + amount > 10_000_000L){     //최대 10,000,000P 까지 충전가능
            throw InvalidAmountException.amountExceedsMaxCharge();
        }
        return new UserPoint(this.id, this.point + amount, System.currentTimeMillis());
    }

    // 포인트 차감 후 새로운 인스턴스를 반환하는 메서드
    public UserPoint use(Long amount) {
        if (amount <= 0) {
            throw InvalidAmountException.useAmountIsTooLow();
        }
        if (this.point < amount ){
            throw InvalidAmountException.insufficientPoints();
        }
        return new UserPoint(this.id, this.point - amount, System.currentTimeMillis());
    }

}