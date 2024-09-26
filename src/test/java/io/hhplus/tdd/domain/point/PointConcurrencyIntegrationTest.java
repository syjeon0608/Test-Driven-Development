package io.hhplus.tdd.domain.point;

import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PointConcurrencyIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointRepository userPointRepository;


    @BeforeEach
    public void setUp() {
        userPointRepository.insertOrUpdate(1L,100L);
        userPointRepository.insertOrUpdate(2L,100L);
        userPointRepository.insertOrUpdate(3L,100L);
    }

    @Test
    @DisplayName("동시에 동일한 유저가 여러 개의 포인트 충전 요청을 보낼 때 정상적으로 처리되어야 한다.")
    public void shouldHandleConcurrentChargesSuccessfully() throws Exception {
        CompletableFuture<UserPoint> future1 = pointService.chargePoints(1L, 50L);
        CompletableFuture<UserPoint> future2 = pointService.chargePoints(1L, 30L);
        CompletableFuture<UserPoint> future3 = pointService.chargePoints(1L, 20L);

        CompletableFuture.allOf(future1, future2, future3).join();

        UserPoint finalUserPoint = userPointRepository.selectById(1L).orElseThrow();

        assertEquals(200L, finalUserPoint.point());
    }

    @Test
    @DisplayName("동시에 서로 다른 유저가 각각 포인트 충전 요청을 보낼 때 병렬로 처리되어야 한다.")
    public void shouldProcessConcurrentRequestsInParallelWhenCharge() throws Exception {
        CompletableFuture<UserPoint> future1 = pointService.chargePoints(1L, 30L);
        CompletableFuture<UserPoint> future2 = pointService.chargePoints(2L, 40L);
        CompletableFuture<UserPoint> future3 = pointService.chargePoints(3L, 50L);

        CompletableFuture.allOf(future1, future2, future3).join();

        assertEquals(130L, future1.join().point());
        assertEquals(140L, future2.join().point());
        assertEquals(150L, future3.join().point());
    }

    @Test
    @DisplayName("동시에 동일한 유저가 여러 개의 포인트 사용 요청을 보낼 때 정상적으로 처리되어야 한다.")
    public void shouldHandleConcurrentUseSuccessfully() throws Exception {
        CompletableFuture<UserPoint> future1 = pointService.usePoints(1L, 10L);
        CompletableFuture<UserPoint> future2 = pointService.usePoints(1L, 30L);
        CompletableFuture<UserPoint> future3 = pointService.usePoints(1L, 20L);

        CompletableFuture.allOf(future1, future2, future3).join();

        UserPoint finalUserPoint = userPointRepository.selectById(1L).orElseThrow();

        assertEquals(40L, finalUserPoint.point());  // 최종 포인트 검증
    }

    @Test
    @DisplayName("동시에 서로 다른 유저가 각각 동시에 포인트 사용 요청을 보낼 때 병렬로 처리되어야 한다.")
    public void shouldProcessConcurrentRequestsInParallelWhenUse() throws Exception {
        CompletableFuture<UserPoint> future1 = pointService.usePoints(1L, 30L);
        CompletableFuture<UserPoint> future2 = pointService.usePoints(2L, 50L);
        CompletableFuture<UserPoint> future3 = pointService.usePoints(3L, 20L);

        CompletableFuture.allOf(future1, future2, future3).join();

        assertEquals(70L, future1.join().point());
        assertEquals(50L, future2.join().point());
        assertEquals(80L, future3.join().point());
    }

    @Test
    @DisplayName("동시에 동일한 유저에게 여러 개의 포인트 충전 및 사용 요청할 때 모든 작업이 정상적으로 처리되어야 한다")
    public void shouldHandleConcurrentChargesAndUsesSuccessfully() throws Exception {
        CompletableFuture<UserPoint> future1 = pointService.chargePoints(1L, 700L);
        CompletableFuture<UserPoint> future2 = pointService.chargePoints(1L, 1000L);
        CompletableFuture<UserPoint> future3 = pointService.chargePoints(1L, 400L);
        CompletableFuture<UserPoint> future4 = pointService.chargePoints(1L, 500L);
        CompletableFuture<UserPoint> future5 = pointService.usePoints(1L, 300L);
        CompletableFuture<UserPoint> future6 = pointService.usePoints(1L, 400L);

        CompletableFuture.allOf(future1, future2, future3, future4, future5, future6).join();

        UserPoint finalUserPoint = userPointRepository.selectById(1L).orElseThrow();
        assertEquals(2000L, finalUserPoint.point());
    }

    @Test
    @DisplayName("동시에 A, B, C 유저가 각각 포인트 충전 및 사용을 요청할 때 모든 작업이 정상적으로 처리되어야 한다")
    public void shouldHandleConcurrentRequestsForMultipleUsers() throws Exception {
        CompletableFuture<UserPoint> futureA1 = pointService.chargePoints(1L, 50L);
        CompletableFuture<UserPoint> futureA2 = pointService.usePoints(1L, 30L);
        CompletableFuture<UserPoint> futureA3 = pointService.chargePoints(1L, 20L);

        CompletableFuture<UserPoint> futureB1 = pointService.usePoints(2L, 50L);
        CompletableFuture<UserPoint> futureB2 = pointService.usePoints(2L, 30L);
        CompletableFuture<UserPoint> futureB3 = pointService.chargePoints(2L, 40L);

        CompletableFuture<UserPoint> futureC1 = pointService.chargePoints(3L, 60L);
        CompletableFuture<UserPoint> futureC2 = pointService.chargePoints(3L, 40L);
        CompletableFuture<UserPoint> futureC3 = pointService.usePoints(3L, 50L);

        CompletableFuture.allOf(futureA1, futureA2, futureA3, futureB1, futureB2, futureB3, futureC1, futureC2, futureC3).join();

        UserPoint finalUser1Point = userPointRepository.selectById(1L).orElseThrow();
        UserPoint finalUser2Point = userPointRepository.selectById(2L).orElseThrow();
        UserPoint finalUser3Point = userPointRepository.selectById(3L).orElseThrow();

        assertEquals(140, finalUser1Point.point());
        assertEquals(60, finalUser2Point.point());
        assertEquals(150, finalUser3Point.point());
    }



}
