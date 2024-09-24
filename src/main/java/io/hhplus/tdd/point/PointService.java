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

    public UserPoint chargePoints(Long userId, Long amount) {
        pointValidator.validateCharge(userId, amount);
        UserPoint userPoint = userPointTable.selectById(userId);
        if (userPoint.isEmpty()) {
            throw UserNotFoundException.notFoundUser(userId);
        }

        UserPoint updatedUserPoint = userPoint.charge(amount);
        userPointTable.insertOrUpdate(userId, updatedUserPoint.point());
        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, System.currentTimeMillis());

        return updatedUserPoint;
    }

    public UserPoint usePoints(Long userId, Long amount) {
        pointValidator.validateUse(userId, amount);
        UserPoint userPoint = userPointTable.selectById(userId);
        if (userPoint.isEmpty()) {
            throw UserNotFoundException.notFoundUser(userId);
        }

        UserPoint updatedUserPoint = userPoint.use(amount);
        userPointTable.insertOrUpdate(userId, updatedUserPoint.point());
        pointHistoryTable.insert(userId, -amount, TransactionType.USE, System.currentTimeMillis());

        return updatedUserPoint;
    }

    public UserPoint getUserPoints(Long userId) {
        pointValidator.validateUserId(userId);
        UserPoint userPoint = userPointTable.selectById(userId);

        if (userPoint.isEmpty()) {
            throw UserNotFoundException.notFoundUser(userId);
        }

        return userPoint;
    }

    public List<PointHistory> getPointHistory(Long userId) {
        pointValidator.validateUserId(userId);
        UserPoint userPoint = userPointTable.selectById(userId);
        if (userPoint.isEmpty()) {
            throw UserNotFoundException.notFoundUser(userId);
        }

        List<PointHistory> historyList = pointHistoryTable.selectAllByUserId(userId);

        if (historyList.isEmpty()) {
            throw NoPointHistoryException.notFoundHistory(userId);
        }

        return historyList.stream()
                .sorted(Comparator.comparingLong(PointHistory::updateMillis).reversed())
                .toList();
    }

}
