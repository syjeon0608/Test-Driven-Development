package io.hhplus.tdd.point.service.impl;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.PointValidator;
import io.hhplus.tdd.point.entity.TransactionType;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.exception.NoPointHistoryException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.PointService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PointServiceImpl implements PointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointValidator pointValidator;

    public PointServiceImpl(UserPointRepository userPointRepository, PointHistoryRepository pointHistoryRepository, PointValidator pointValidator) {
        this.userPointRepository = userPointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
        this.pointValidator = pointValidator;
    }


    // 등록/미등록 유저 판별 메서드
    private UserPoint getUserPointOrThrow(Long userId) {
        return userPointRepository.selectById(userId)
                .orElseThrow(() -> UserNotFoundException.notFoundUser(userId));
    }

    // 포인트 기록 저장 로직 통합 메서드
    private void savePointHistory(Long userId, Long amount, TransactionType type) {
        pointHistoryRepository.insert(userId, amount, type, System.currentTimeMillis());
    }
    
    public UserPoint chargePoints(Long userId, Long amount) {
        pointValidator.validateCharge(userId, amount);
        UserPoint userPoint = getUserPointOrThrow(userId);

        UserPoint updatedUserPoint = userPoint.charge(amount);
        userPointRepository.insertOrUpdate(userId, updatedUserPoint.point());
        savePointHistory(userId, amount, TransactionType.CHARGE);

        return updatedUserPoint;
    }

    public UserPoint usePoints(Long userId, Long amount) {
        pointValidator.validateUse(userId, amount);
        UserPoint userPoint = getUserPointOrThrow(userId);

        UserPoint updatedUserPoint = userPoint.use(amount);
        userPointRepository.insertOrUpdate(userId, updatedUserPoint.point());
        savePointHistory(userId, -amount, TransactionType.USE);

        return updatedUserPoint;
    }

    public UserPoint getUserPoints(Long userId) {
        pointValidator.validateUserId(userId);
        return getUserPointOrThrow(userId);
    }

    public List<PointHistory> getPointHistory(Long userId) {
        pointValidator.validateUserId(userId);
        getUserPointOrThrow(userId);

        List<PointHistory> historyList = pointHistoryRepository.selectAllByUserId(userId);

        if (historyList.isEmpty()) {
            throw NoPointHistoryException.notFoundHistory(userId);
        }

        return historyList.stream()
                .sorted(Comparator.comparingLong(PointHistory::updateMillis).reversed())
                .toList();
    }

}
