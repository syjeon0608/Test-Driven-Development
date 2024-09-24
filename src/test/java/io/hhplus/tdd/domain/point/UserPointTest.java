package io.hhplus.tdd.domain.point;

import io.hhplus.tdd.point.exception.InvalidChargeAmountException;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.exception.InvalidUseAmountException;
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
    public void shouldUsePointsSuccessfully() {
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
        UserPoint updatedUserPoint = userPoint.use(50L);

        assertEquals(50L, updatedUserPoint.point());
    }

    @Test
    @DisplayName("포인트 충전 시 1 이상의 최소 금액으로 충전이 성공해야 한다.")
    public void shouldChargePointsWithMinimumValidAmount() {
        //경계값 테스트를 추가하였습니다.
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
        UserPoint updatedUserPoint = userPoint.charge(1L);

        assertEquals(101L, updatedUserPoint.point());
    }

    @Test
    @DisplayName("포인트 사용 시 1 이상의 최소 금액으로 사용이 성공해야 한다.")
    public void shouldUsePointsWithMinimumValidAmount() {
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
        UserPoint updatedUserPoint = userPoint.use(1L);

        assertEquals(99L, updatedUserPoint.point());
    }


    @Test
    @DisplayName("포인트 충전 시 0 이하의 금액은 예외를 던져야 한다.")
    public void shouldThrowExceptionWhenChargeAmountIsZeroOrLess() {
        //엔티티 내부의 상태와 관련된 비즈니스 규칙 위반 검증은 엔티티 테스트에 작성하였습니다.
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());

        assertThrows(InvalidChargeAmountException.class, () -> userPoint.charge(0L));
        assertThrows(InvalidChargeAmountException.class, () -> userPoint.charge(-50L));
    }

    @Test
    @DisplayName("포인트 사용 시 0 이하의 금액은 예외를 던져야 한다.")
    public void shouldThrowExceptionWhenUseAmountIsZeroOrLess() {
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());

        assertThrows(InvalidUseAmountException.class, () -> userPoint.use(0L));
        assertThrows(InvalidUseAmountException.class, () -> userPoint.use(-10L));
    }

}