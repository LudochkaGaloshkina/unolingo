package com.valek.repository.learningreps;

import com.valek.model.learningmodels.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByUnit_IdAndPublishedTrueOrderByPositionAsc(Long unitId);
}