package io.hhplus.tdd.point.exception;

public class NoPointHistoryException extends RuntimeException {
    public NoPointHistoryException(String message) {
        super(message);
    }

    public static NoPointHistoryException notFoundHistory(Long userId) {
        return new NoPointHistoryException("포인트 충전/사용 내역이 없습니다.");
    }
}
