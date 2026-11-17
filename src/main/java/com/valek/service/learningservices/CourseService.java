package com.valek.service.learningservices;

import com.valek.model.learningmodels.Course;
import com.valek.repository.learningreps.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getPublishedCourses() {
        return courseRepository.findByPublishedTrue();
    }

    public Optional<Course> getPublishedCourseById(Long id) {
        return courseRepository.findByIdAndPublishedTrue(id);
    }
}