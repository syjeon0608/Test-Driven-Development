package io.hhplus.tdd.domain.point;

import io.hhplus.tdd.point.exception.InvalidChargeAmountException;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserPointTest {

    @Test
    public void shouldChargePointsSuccessfully() {
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
        UserPoint updatedUserPoint = userPoint.charge(50L);

        assertEquals(150L, updatedUserPoint.point());
    }

    @Test
    @DisplayName("반드시 충전할 금액이 0이하면 예외를 던져야 한다.")
    public void shouldThrowExceptionWhenChargeAmountIsZeroOrLess() {
        //엔티티 내부의 상태와 관련된 비즈니스 규칙 위반 검증은 엔티티 테스트에 작성하였습니다.
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());

        assertThrows(InvalidChargeAmountException.class, () -> userPoint.charge(0L));
        assertThrows(InvalidChargeAmountException.class, () -> userPoint.charge(-50L));
    }


}