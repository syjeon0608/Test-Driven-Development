package io.hhplus.tdd.point.exception;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException(String message) {
        super(message);
    }

    public static InvalidAmountException chargeAmountIsTooLow() {
        return new InvalidAmountException("충전할 포인트는 0보다 커야 합니다.");
    }

    public static InvalidAmountException amountExceedsMaxCharge() {
        return new InvalidAmountException("최대 충전 포인트는 10,000,000원 입니다.");
    }

    public static InvalidAmountException useAmountIsTooLow() {
        return new InvalidAmountException("사용할 포인트는 0보다 커야 합니다.");
    }

    public static InvalidAmountException insufficientPoints() {
        return new InvalidAmountException("잔여 포인트가 부족합니다.");
    }

}
