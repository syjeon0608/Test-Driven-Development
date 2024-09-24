package io.hhplus.tdd.point.exception;

public class InvalidChargeAmountException extends RuntimeException {

    private InvalidChargeAmountException(String message) {
        super(message);
    }

    public static InvalidChargeAmountException amountIsTooLow() {
        return new InvalidChargeAmountException("충전할 포인트는 0보다 커야 합니다.");
    }

}