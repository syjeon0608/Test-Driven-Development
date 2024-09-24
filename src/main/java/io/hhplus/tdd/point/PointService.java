package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.exception.NoPointHistoryException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final PointValidator pointValidator;

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable, PointValidator pointValidator) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
        this.pointValidator = pointValidator;
    }
    // 공통 사용자 조회 및 검증 메서드
    private UserPoint getUserPointOrThrow(Long userId) {
        UserPoint userPoint = userPointTable.selectById(userId);
        if (userPoint.isEmpty()) {
            throw UserNotFoundException.notFoundUser(userId);
        }
        return userPoint;
    }

    // 포인트 기록 저장 로직 통합 메서드
    private void savePointHistory(Long userId, Long amount, TransactionType type) {
        pointHistoryTable.insert(userId, amount, type, System.currentTimeMillis());
    }


    public UserPoint chargePoints(Long userId, Long amount) {
        pointValidator.validateCharge(userId, amount);
        UserPoint userPoint = getUserPointOrThrow(userId);

        UserPoint updatedUserPoint = userPoint.charge(amount);
        userPointTable.insertOrUpdate(userId, updatedUserPoint.point());
        savePointHistory(userId, amount, TransactionType.CHARGE);

        return updatedUserPoint;
    }

    public UserPoint usePoints(Long userId, Long amount) {
        pointValidator.validateUse(userId, amount);
        UserPoint userPoint = getUserPointOrThrow(userId);

        UserPoint updatedUserPoint = userPoint.use(amount);
        userPointTable.insertOrUpdate(userId, updatedUserPoint.point());
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

        List<PointHistory> historyList = pointHistoryTable.selectAllByUserId(userId);

        if (historyList.isEmpty()) {
            throw NoPointHistoryException.notFoundHistory(userId);
        }

        return historyList.stream()
                .sorted(Comparator.comparingLong(PointHistory::updateMillis).reversed())
                .toList();
    }

}
