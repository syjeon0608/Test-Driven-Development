package io.hhplus.tdd.point.exception;

public class InvalidUseAmountException extends RuntimeException {

    public InvalidUseAmountException(String message) {
        super(message);
    }
    public static InvalidUseAmountException amountIsTooLow() {
        return new InvalidUseAmountException("사용할 포인트는 0보다 커야 합니다.");
    }

}
