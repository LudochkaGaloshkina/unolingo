package com.valek.service;

import com.valek.model.Course;
import com.valek.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public List<Course> getAllCourses(){
        return courseRepository.findAll();
    }

    public List<Course> getPublishedCourses(){
        return courseRepository.findByPublishedTrue();
    }
}
