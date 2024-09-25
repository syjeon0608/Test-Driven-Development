package io.hhplus.tdd.domain.point;

import io.hhplus.tdd.point.entity.PointValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointValidatorTest {

    private final PointValidator validator = new PointValidator();

    @Test
    public void shouldValidateChargeSuccessfully() {
        assertDoesNotThrow(() -> validator.validateCharge(1L, 50L));
    }

    @Test
    public void shouldValidateUseSuccessfully() {
        assertDoesNotThrow(() -> validator.validateUse(1L, 50L));
    }

    @Test
    public void shouldValidateUserIdSuccessfully() {
        assertDoesNotThrow(() -> validator.validateUserId(1L));
    }

    @Test
    @DisplayName("유저ID가 0이하거나 null이면 예외를 던져야 한다")
    public void shouldThrowExceptionForInvalidUserId() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateUserId(null));
        assertThrows(IllegalArgumentException.class, () -> validator.validateUserId(0L));
        assertThrows(IllegalArgumentException.class, () -> validator.validateUserId(-1L));
    }

    @Test
    @DisplayName("포인트 충전/사용 시 유효 하지 않은 금액은(null) 예외를 던져야 한다")
    public void shouldThrowExceptionForInvalidAmount() {
        // Validator에서는 amount의 null여부만 검증합니다.
        // 충전/사용 금액이 0이하 음수이면 안되는것은 엔티티에서 책임지도록 코드를 작성하여 이부분에 대한 테스트 코드는 UserPointService 에 작성하였습니다.
        IllegalArgumentException chargeException = assertThrows(IllegalArgumentException.class, () -> validator.validateCharge(1L, null));
        assertEquals("충전 금액의 입력이 필요합니다.", chargeException.getMessage());

        IllegalArgumentException useException = assertThrows(IllegalArgumentException.class, () -> validator.validateUse(1L, null));
        assertEquals("사용 금액의 입력이 필요합니다.", useException.getMessage());
    }

}