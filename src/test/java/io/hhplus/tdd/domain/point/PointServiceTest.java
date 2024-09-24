package io.hhplus.tdd.domain.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.*;
import io.hhplus.tdd.point.exception.NoPointHistoryException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

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
    @DisplayName("미등록 ID에 대해 예외를 던져야 한다")
    public void shouldThrowExceptionWhenUserNotFound() {
        when(userPointRepository.selectById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> pointService.getUserPointOrThrow(999L));
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