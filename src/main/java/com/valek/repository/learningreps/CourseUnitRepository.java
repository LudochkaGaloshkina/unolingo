package com.valek.repository.learningreps;

import com.valek.model.learningmodels.CourseUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseUnitRepository extends JpaRepository<CourseUnit, Long> {
    List<CourseUnit> findByCourse_IdOrderByPositionAsc(Long courseId);
}
