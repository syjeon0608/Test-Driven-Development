package io.hhplus.tdd.point.exception;

public class UserNotFoundException extends RuntimeException {

    private UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException notFoundUser(Long userId) {
        return new UserNotFoundException("해당 ID의 유저를 찾을 수 없습니다: "+ userId);
    }
}
