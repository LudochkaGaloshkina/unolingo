
package com.valek.model.attempts;

import com.valek.model.learningmodels.Exercise;
import com.valek.model.learningmodels.ExerciseOption;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "exercise_attempts")
@Getter
@Setter
public class ExerciseAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_attempt_id", nullable = false)
    private LessonAttempt lessonAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private ExerciseOption selectedOption;

    @Column(name = "user_answer")
    private String userAnswer;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "earned_points", nullable = false)
    private int earnedPoints = 0;

    @Column(name = "answered_at", nullable = false)
    private OffsetDateTime answeredAt;
}
