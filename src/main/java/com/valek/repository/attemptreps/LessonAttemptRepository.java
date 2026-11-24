
package com.valek.repository.attemptreps;

import com.valek.model.attempts.LessonAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonAttemptRepository
        extends JpaRepository<LessonAttempt, Long> {

    List<LessonAttempt> findByUser_IdAndLesson_IdOrderByStartedAtDesc(
            Long userId,
            Long lessonId
    );

    List<LessonAttempt> findByUser_IdOrderByStartedAtDesc(Long userId);
}
