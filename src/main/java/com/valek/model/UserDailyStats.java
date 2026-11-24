package com.valek.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_daily_stats")
@Getter
@Setter
public class UserDailyStats {

    @EmbeddedId
    private UserDailyStatsId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "xp_earned", nullable = false)
    private int xpEarned = 0;

    @Column(name = "lessons_completed", nullable = false)
    private int lessonsCompleted = 0;
}
