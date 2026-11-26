package com.valek.repository;

import com.valek.model.UserDailyStats;
import com.valek.model.UserDailyStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

//статистика активности и заработанного XP
@Repository
public interface UserDailyStatsRepository extends JpaRepository<UserDailyStats, UserDailyStatsId> {

    List<UserDailyStats> findByUser_IdOrderById_ActivityDateDesc(Long userId);

    @Query("""
        SELECT COALESCE(SUM(stats.xpEarned), 0)
        FROM UserDailyStats stats
        WHERE stats.user.id = :userId
        """)
    int calculateTotalXp(@Param("userId") Long userId);

    @Query("""
    SELECT stats
    FROM UserDailyStats stats
    WHERE stats.user.id = :userId
      AND stats.id.activityDate BETWEEN :startDate AND :endDate
    ORDER BY stats.id.activityDate ASC
    """)
    List<UserDailyStats> findWeeklyStats(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}