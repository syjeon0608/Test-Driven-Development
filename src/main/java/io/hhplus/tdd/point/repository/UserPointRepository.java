package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.entity.UserPoint;

import java.util.Optional;

public interface UserPointRepository {
    Optional<UserPoint> selectById(long id);
    UserPoint insertOrUpdate(long id, long amount);

}
