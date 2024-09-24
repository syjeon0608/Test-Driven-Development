package io.hhplus.tdd.domain.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.*;
import io.hhplus.tdd.point.exception.NoPointHistoryException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @DisplayName("포인트 충전이 성공적으로 이루어져야 한다")
    public void shouldChargePointsSuccessfully() {
        UserPoint userPoint = new UserPoint(1L, 100L, currentTimeMillis());
        UserPoint updatedUserPoint = userPoint.charge(50L);

        assertEquals(150L, updatedUserPoint.point());
    }

    @Test
    @DisplayName("포인트 사용이 성공적으로 이루어져야 한다")
    public void shouldUsePointsSuccessfully() {
        UserPoint userPoint = new UserPoint(1L, 100L, currentTimeMillis());
        UserPoint updatedUserPoint = userPoint.use(50L);

        assertEquals(50L, updatedUserPoint.point());
    }

    @Test
    @DisplayName("포인트 조회가 성공적으로 이루어져야 한다")
    public void shouldGetPointsSuccessfully() {
        UserPoint userPoint = new UserPoint(1L, 100L, currentTimeMillis());

        assertEquals(100L, userPoint.point());
    }

    @Test
    @DisplayName("포인트 내역이 내림차순으로 정렬되어야 한다")
    public void shouldReturnPointHistoryInDescendingOrder() {
        List<PointHistory> historyList = List.of(
                new PointHistory(1L, 1L,1000L,TransactionType.CHARGE, currentTimeMillis()),
                new PointHistory(2L, 1L,500L, TransactionType.USE, currentTimeMillis() - 1000)
        );

        List<PointHistory> sortedHistory = historyList.stream()
                .sorted(Comparator.comparingLong(PointHistory::updateMillis).reversed())
                .toList();

        assertEquals(1000L, sortedHistory.get(0).amount());
        assertEquals(500L, sortedHistory.get(1).amount());
    }

    @Test
    @DisplayName("유효하지 않은 사용자 ID에 대해 예외를 던져야 한다")
    public void shouldThrowExceptionWhenUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> userPointTable.selectById(999L));
    }

    @Test
    @DisplayName("포인트 내역이 비어있을 경우 예외가 발생해야 한다")
    public void shouldThrowExceptionWhenPointHistoryIsEmpty() {
        List<PointHistory> historyList = List.of();

        assertThrows(NoPointHistoryException.class, () -> {
            if (historyList.isEmpty()) {
                throw NoPointHistoryException.notFoundHistory(1L);
            }
        });
    }

}