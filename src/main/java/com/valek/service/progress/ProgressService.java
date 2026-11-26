package com.valek.service.progress;

import com.valek.dto.progressdto.DailyXpDto;
import com.valek.dto.progressdto.ProgressSummaryDto;
import com.valek.model.User;
import com.valek.model.UserDailyStats;
import com.valek.repository.UserDailyStatsRepository;
import com.valek.repository.attemptreps.LessonAttemptRepository;
import com.valek.repository.attemptreps.UserLessonProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {
    private final LessonAttemptRepository lessonAttemptRepository;
    private final UserDailyStatsRepository userDailyStatsRepository;
    private final UserLessonProgressRepository userLessonProgressRepository;
    private static final int XP_PER_LEVEL = 300;

    public ProgressSummaryDto getProgressSummary(User user) {
        ProgressSummaryDto summary = new ProgressSummaryDto();

        Long userID = user.getId();

        summary.setTotalXp(userDailyStatsRepository.calculateTotalXp(userID));

        summary.setCompletedLessons(userLessonProgressRepository.countByUser_IdAndStatus(userID, "COMPLETED"));

        summary.setTotalAttempts(lessonAttemptRepository.countByUser_Id(userID));

        summary.setAverageAccuracy(lessonAttemptRepository.calculateAverageAccuracy(userID));

        summary.setWeeklyActivity(getWeeklyActivity(userID));

        int totalXp = summary.getTotalXp();

        summary.setLevel(totalXp / XP_PER_LEVEL + 1);
        summary.setXpInCurrentLevel(totalXp % XP_PER_LEVEL);
        summary.setXpForNextLevel(XP_PER_LEVEL);

        return summary;
    }

    private List<DailyXpDto> getWeeklyActivity(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        List<UserDailyStats> statsList = userDailyStatsRepository.findWeeklyStats(userId, startDate, endDate);

        Map<LocalDate, Integer> xpByDate = new HashMap<>();

        for (UserDailyStats stats : statsList) {
            xpByDate.put(stats.getId().getActivityDate(), stats.getXpEarned());
        }

        List<DailyXpDto> weeklyActivity = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = startDate.plusDays(i);

            DailyXpDto dailyXp = new DailyXpDto();
            dailyXp.setActivityDate(currentDate);
            dailyXp.setXpEarned(xpByDate.getOrDefault(currentDate, 0));

            weeklyActivity.add(dailyXp);
        }

        return weeklyActivity;
    }

    @Transactional(readOnly = true)
    public Set<Long> getCompletedLessonIds(User user) {
        return userLessonProgressRepository.findByUser_Id(user.getId())
                .stream()
                .filter(progress -> "COMPLETED".equals(progress.getStatus()))
                .map(progress -> progress.getLesson().getId())
                .collect(Collectors.toSet());
    }

    public long getCompletedLessons(User user) {
        return userLessonProgressRepository.countByUser_IdAndStatus(
                user.getId(),
                "COMPLETED"
        );
    }
}