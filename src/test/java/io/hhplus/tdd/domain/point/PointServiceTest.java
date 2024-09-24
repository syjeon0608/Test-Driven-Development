package io.hhplus.tdd.domain.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.*;
import io.hhplus.tdd.point.exception.NoPointHistoryException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    public void shouldCallValidatorForCharge() {
        //validator에 대한 검증은 Validator 에서 했기 때문에 서비스단에서는 Validator와의 상호작용 확인 정도만 하였습니다.
        when(userPointTable.selectById(1L)).thenReturn(new UserPoint(1L, 100L, System.currentTimeMillis()));

        pointService.chargePoints(1L, 50L);

        verify(pointValidator).validateCharge(1L, 50L);
    }

    @Test
    public void shouldCallValidatorForUse() {
        when(userPointTable.selectById(1L)).thenReturn(new UserPoint(1L, 100L, System.currentTimeMillis()));

        pointService.usePoints(1L, 50L);

        verify(pointValidator).validateUse(1L, 50L);
    }

    @Test
    public void shouldCallValidatorForGetUser() {
        when(userPointTable.selectById(1L)).thenReturn(new UserPoint(1L, 100L, System.currentTimeMillis()));

        pointService.getUserPoints(1L);

        verify(pointValidator).validateUserId(1L);
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

    @Test
    public void shouldGetUserPointsSuccessfully() {
        Long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 100L, System.currentTimeMillis());
        when(userPointTable.selectById(userId)).thenReturn(userPoint);

        UserPoint result = pointService.getUserPoints(userId);

        assertEquals(userPoint.id(), result.id());
        assertEquals(userPoint.point(), result.point());

    }

    @Test
    public void shouldGetPointHistorySuccessfully() {
        Long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 30L, System.currentTimeMillis());
        when(userPointTable.selectById(userId)).thenReturn(userPoint);

        List<PointHistory> pointHistoryList = List.of(
                new PointHistory(1L, userId, 50L, TransactionType.CHARGE, System.currentTimeMillis() - 1000),
                new PointHistory(2L, userId, -20L, TransactionType.USE, System.currentTimeMillis())
        );

        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(pointHistoryList);

        List<PointHistory> result = pointService.getPointHistory(userId);
        System.out.println(result);

        assertEquals(2, result.size());
        assertTrue(result.get(0).updateMillis() > result.get(1).updateMillis());
    }

    @Test
    public void shouldThrowExceptionWhenNoPointHistoryExists() {
        Long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 30L, System.currentTimeMillis());
        when(userPointTable.selectById(userId)).thenReturn(userPoint);
        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(Collections.emptyList());

        assertThrows(NoPointHistoryException.class, () -> pointService.getPointHistory(userId));
    }

}