package io.hhplus.tdd.point.repository.impl;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserPointRepositoryImpl implements UserPointRepository {
    private final UserPointTable userPointTable;

    public UserPointRepositoryImpl(UserPointTable userPointTable) {
        this.userPointTable = userPointTable;
    }

    @Override
    public Optional<UserPoint> selectById(long id) {
        return Optional.ofNullable(userPointTable.selectById(id));
    }

    @Override
    public UserPoint insertOrUpdate(long id, long amount) {
        return userPointTable.insertOrUpdate(id, amount);
    }

}
