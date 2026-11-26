
package com.valek.repository.attemptreps;

import com.valek.model.attempts.LessonAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


//история прохождения уроков, результаты и точность
@Repository
public interface LessonAttemptRepository
        extends JpaRepository<LessonAttempt, Long> {

    List<LessonAttempt> findByUser_IdAndLesson_IdOrderByStartedAtDesc(
            Long userId,
            Long lessonId
    );

    List<LessonAttempt> findByUser_IdOrderByStartedAtDesc(Long userId);

    long countByUser_Id(Long userId);

    @Query("""
        SELECT COALESCE(
            AVG(la.correctCount * 100.0 / la.totalCount), 0
        )
        FROM LessonAttempt la
        WHERE la.user.id = :userId
        AND la.totalCount > 0
        """)
    double calculateAverageAccuracy(@Param("userId") Long userId);
}
