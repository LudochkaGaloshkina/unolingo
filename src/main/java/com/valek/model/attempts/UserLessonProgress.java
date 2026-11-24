package com.valek.model.attempts;

import com.valek.model.User;
import com.valek.model.learningmodels.Lesson;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_lesson_progress")
@Getter
@Setter
public class UserLessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(nullable = false, length = 20)
    private String status = "NOT_STARTED";

    @Column(name = "best_score", nullable = false)
    private int bestScore = 0;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;
}
