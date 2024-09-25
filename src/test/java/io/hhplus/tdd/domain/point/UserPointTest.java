package io.hhplus.tdd.domain.point;

import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.exception.InvalidAmountException;
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

        assertThrows(InvalidAmountException.class, () -> userPoint.charge(0L));
        assertThrows(InvalidAmountException.class, () -> userPoint.charge(-50L));
    }

    @Test
    @DisplayName("포인트 사용 시 0 이하의 금액은 예외를 던져야 한다.")
    public void shouldThrowExceptionWhenUseAmountIsZeroOrLess() {
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());

        assertThrows(InvalidAmountException.class, () -> userPoint.use(0L));
        assertThrows(InvalidAmountException.class, () -> userPoint.use(-10L));
    }

    @Test
    @DisplayName("포인트 충전 시 최대 잔고를 초과하면 예외를 던져야 한다.")
    public void shouldThrowExceptionWhenChargeExceedsMaxBalance() {
        UserPoint userPoint = new UserPoint(1L, 9_999_000L, System.currentTimeMillis());

        assertThrows(InvalidAmountException.class, () -> userPoint.charge(1_001L));
    }

    @Test
    @DisplayName("포인트 사용 시 잔고가 부족하면 예외를 던져야 한다.")
    public void shouldThrowExceptionWhenInsufficientBalanceForUse() {
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());

        assertThrows(InvalidAmountException.class, () -> userPoint.use(101L));
    }

    @Test
    @DisplayName("잔고가 0일 때 포인트를 사용하려고 하면 예외를 던져야 한다.")
    public void shouldThrowExceptionWhenUsingPointsWithZeroBalance() {
        UserPoint userPoint = new UserPoint(1L, 0L, System.currentTimeMillis());

        assertThrows(InvalidAmountException.class, () -> userPoint.use(50L));
    }

    @Test
    @DisplayName("최대 잔고 10,000,000원을 정확히 충전해도 성공해야 한다.")
    public void shouldChargePointsExactlyToMaxBalance() {
        UserPoint userPoint = new UserPoint(1L, 9_999_000L, System.currentTimeMillis());
        UserPoint updatedUserPoint = userPoint.charge(1_000L);

        assertEquals(10_000_000L, updatedUserPoint.point());
    }

    @Test
    @DisplayName("포인트 사용 시 잔액이 0이 될 때 성공적으로 포인트 사용")
    public void shouldUsePointsWhenBalanceReachesZero() {
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
        UserPoint updatedUserPoint = userPoint.use(100L);

        assertEquals(0L, updatedUserPoint.point());
    }

    @Test
    @DisplayName("여러 차례 포인트 충전이 정상적으로 이루어져야 한다.")
    public void shouldChargePointsMultipleTimesSuccessfully() {
        UserPoint userPoint = new UserPoint(1L, 100L, System.currentTimeMillis());
        userPoint = userPoint.charge(50L);
        userPoint = userPoint.charge(100L);

        assertEquals(250L, userPoint.point());
    }

    @Test
    @DisplayName("여러 차례 포인트 사용이 정상적으로 이루어져야 한다.")
    public void shouldUsePointsMultipleTimesSuccessfully() {
        UserPoint userPoint = new UserPoint(1L, 200L, System.currentTimeMillis());
        userPoint = userPoint.use(50L);
        userPoint = userPoint.use(100L);

        assertEquals(50L, userPoint.point());
    }

}