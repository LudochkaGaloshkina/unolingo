package com.valek.repository.learningreps;

import com.valek.model.learningmodels.Lesson;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByUnit_IdAndPublishedTrueOrderByPositionAsc(Long unitId);

    @EntityGraph(attributePaths = {"unit", "unit.course"})
    List<Lesson> findByPublishedTrueOrderByPositionAsc();

    @EntityGraph(attributePaths = {"unit", "unit.course"})
    Optional<Lesson> findByIdAndPublishedTrue(Long id);

}