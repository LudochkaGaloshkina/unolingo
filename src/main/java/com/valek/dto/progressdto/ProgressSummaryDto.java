package com.valek.dto.progressdto;

import lombok.Data;

import java.util.List;

@Data
public class ProgressSummaryDto {

    private int totalXp; //user_daily_stats
    private long completedLessons; //user_lesson_progress
    private long totalAttempts; //lesson_attempts
    private double averageAccuracy; //lesson_attempts
    private List<DailyXpDto> weeklyActivity; //user_daily_stats
    private int level;
    private int xpInCurrentLevel;
    private int xpForNextLevel;
}