package com.valek.repository;

import com.valek.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByPublishedTrue();

    Optional<Course> findByIdAndPublishedTrue(Long id);
}
