package com.valek.repository.attemptreps;

import com.valek.model.attempts.UserLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLessonProgressRepository
        extends JpaRepository<UserLessonProgress, Long> {

    Optional<UserLessonProgress> findByUser_IdAndLesson_Id(
            Long userId,
            Long lessonId
    );

    List<UserLessonProgress> findByUser_Id(Long userId);
}
