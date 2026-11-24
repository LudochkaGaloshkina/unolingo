package com.valek.repository;

import com.valek.model.learningmodels.UserDailyStats;
import com.valek.model.UserDailyStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDailyStatsRepository extends JpaRepository<UserDailyStats, UserDailyStatsId> {

    List<UserDailyStats> findByUser_IdOrderById_ActivityDateDesc(Long userId);
}
