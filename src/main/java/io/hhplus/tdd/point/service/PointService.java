package io.hhplus.tdd.point.service;


import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;

import java.util.List;

public interface PointService {
    UserPoint chargePoints(Long userId, Long amount);
    UserPoint usePoints(Long userId, Long amount);
    UserPoint getUserPoints(Long userId);
    List<PointHistory> getPointHistory(Long userId);
}
