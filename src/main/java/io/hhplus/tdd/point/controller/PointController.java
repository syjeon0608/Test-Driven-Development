package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public ResponseEntity<UserPoint>  point(@PathVariable long id) {
        UserPoint updatedUserPoint = pointService.getUserPoints(id);
        return ResponseEntity.ok(updatedUserPoint);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public ResponseEntity<List<PointHistory>> history(@PathVariable long id) {
        List<PointHistory> historyList = pointService.getPointHistory(id);
        return ResponseEntity.ok(historyList);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<CompletableFuture<UserPoint>> charge(@PathVariable long id, @RequestBody long amount) {
        CompletableFuture<UserPoint> updatedUserPoint = pointService.chargePoints(id, amount);
        return ResponseEntity.ok(updatedUserPoint);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public ResponseEntity<CompletableFuture<UserPoint>> use(@PathVariable long id,@RequestBody long amount) {
        CompletableFuture<UserPoint> updatedUserPoint = pointService.usePoints(id, amount);
        return ResponseEntity.ok(updatedUserPoint);
    }
}
