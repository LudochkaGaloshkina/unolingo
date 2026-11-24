package com.valek.repository.attemptreps;

import com.valek.model.attempts.ExerciseAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseAttemptRepository
        extends JpaRepository<ExerciseAttempt, Long> {

    List<ExerciseAttempt> findByLessonAttempt_Id(Long lessonAttemptId);

    List<ExerciseAttempt> findByExercise_Id(Long exerciseId);
}
