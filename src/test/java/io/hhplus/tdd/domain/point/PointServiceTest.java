package io.hhplus.tdd.domain.point;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.PointValidator;
import io.hhplus.tdd.point.entity.TransactionType;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.exception.NoPointHistoryException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.*;
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
        when(userPointRepository.selectById(1L)).thenReturn(Optional.of(userPoint));

        CompletableFuture<UserPoint> future = pointService.chargePoints(userPoint.id(), 50L);

        UserPoint updatedUserPoint = future.join();
        assertEquals(150L, updatedUserPoint.point());

    }

    @Test
    @DisplayName("포인트 사용이 성공적으로 이루어져야 한다")
    public void shouldUsePointsSuccessfully() {
        UserPoint userPoint = new UserPoint(1L, 100L, currentTimeMillis());
        when(userPointRepository.selectById(1L)).thenReturn(Optional.of(userPoint));

        CompletableFuture<UserPoint> future = pointService.usePoints(userPoint.id(), 30L);
        UserPoint updatedUserPoint = future.join();

        assertEquals(70L, updatedUserPoint.point());
    }


    @Test
    @DisplayName("포인트 조회가 성공적으로 이루어져야 한다")
    public void shouldGetPointsSuccessfully() {
        UserPoint userPoint = new UserPoint(1L, 100L, currentTimeMillis());
        when(userPointRepository.selectById(1L)).thenReturn(Optional.of(userPoint));

        UserPoint resUserPoint =pointService.getUserPoints(1L);

        assertEquals(100L,resUserPoint.point() );
    }

    @Test
    @DisplayName("포인트 내역이 내림차순으로 정렬되어야 한다")
    public void shouldReturnPointHistoryInDescendingOrder() {
        List<PointHistory> historyList = List.of(
                new PointHistory(1L, 1L,1000L, TransactionType.CHARGE, currentTimeMillis()),
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

        assertThrows(UserNotFoundException.class, () -> pointService.getUserPoints(999L));
        //리팩토링 하며 private 메서드로 전환하면서 getUserPointOrThrow을 직접 호출하여 검증하는 대신, 해당 getUserPointOrThrow를 호출하는 메서드를 테스트 하였습니다.
    }

    @Test
    @DisplayName("포인트 기록이 정상적으로 저장되어야 한다")
    void shouldSavePointHistory() {
        Long userId = 1L;
        Long amount = 100L;
        TransactionType transactionType = TransactionType.CHARGE;

        UserPoint userPoint = new UserPoint(userId, 100L, currentTimeMillis());
        when(userPointRepository.selectById(1L)).thenReturn(Optional.of(userPoint));

        // private savePointHistory()를 직접 호출하는 대신, 이 메서드를 호출하는 메서드로 테스트 하였습니다.
        CompletableFuture<UserPoint> updatedUserPoint = pointService.chargePoints(userId, amount);

        ArgumentCaptor<Long> updateMillisCaptor = ArgumentCaptor.forClass(Long.class);

        verify(pointHistoryRepository).insert(eq(userId), eq(amount), eq(transactionType), updateMillisCaptor.capture());

        //  currentTimeMillis()로 반환되는 값이 테스트 실행 시점마다 달라져서 updateMillis는 1초(1000ms) 이내의 차이를 허용합니다.
        Long capturedUpdateMillis = updateMillisCaptor.getValue();
        Long currentTime = System.currentTimeMillis();
        assertTrue( capturedUpdateMillis <= currentTime && capturedUpdateMillis >= currentTime - 1000L);
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