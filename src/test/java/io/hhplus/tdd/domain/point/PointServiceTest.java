package io.hhplus.tdd.domain.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.*;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @Mock
    private PointValidator pointValidator;

    @InjectMocks
    private PointService pointService;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void shouldChargePointsSuccessfully() {
        Long userId = 1L;
        Long amount = 50L;
        UserPoint userPoint = new UserPoint(userId, 100L, System.currentTimeMillis());

        when(userPointTable.selectById(userId)).thenReturn(userPoint);
        when(userPointTable.insertOrUpdate(userId, 150L)).thenReturn(new UserPoint(userId, 150L, System.currentTimeMillis()));

        UserPoint updatedUserPoint = pointService.chargePoints(userId, amount);

        assertEquals(150L, updatedUserPoint.point());
        verify(userPointTable).selectById(userId);
        verify(userPointTable).insertOrUpdate(userId, 150L);
        verify(pointHistoryTable).insert(eq(userId), eq(amount), eq(TransactionType.CHARGE), anyLong());
    }


    @Test
    public void shouldCallValidatorBeforeCharging() {
        //validator에 대한 검증은 Validator 에서 했기 때문에 서비스단에서는 Validator와의 상호작용 확인 정도만 하였습니다.
        when(userPointTable.selectById(1L)).thenReturn(new UserPoint(1L, 100L, System.currentTimeMillis()));
        pointService.chargePoints(1L, 50L);
        pointService.usePoints(1L, 50L);

        verify(pointValidator).validateCharge(1L, 50L);
        verify(pointValidator).validateUse(1L, 50L);
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {
        //해당 ID 의 유저가 존재하지 않는경우 예외를 던지는 것은 비지니스 규칙이라 서비스에 작성하였습니다.
        Long userId = 999L;
        Long amount = 50L;

        when(userPointTable.selectById(userId)).thenReturn(UserPoint.empty(userId));

        assertThrows(UserNotFoundException.class, () -> pointService.chargePoints(userId, amount));

        verify(userPointTable).selectById(userId);
    }

    @Test
    public void shouldUsePointsSuccessfully() {
        Long userId = 1L;
        Long amount = 20L;
        UserPoint userPoint = new UserPoint(userId, 100L, System.currentTimeMillis());

        when(userPointTable.selectById(userId)).thenReturn(userPoint);
        when(userPointTable.insertOrUpdate(userId, 80L)).thenReturn(new UserPoint(userId, 80L, System.currentTimeMillis()));

        UserPoint updatedUserPoint = pointService.usePoints(userId, amount);

        assertEquals(80L, updatedUserPoint.point());

        // 메서드 호출 검증
        verify(userPointTable).selectById(userId);
        verify(userPointTable).insertOrUpdate(userId, 80L);
        verify(pointHistoryTable).insert(eq(userId), eq(-amount), eq(TransactionType.USE), anyLong());
    }


}