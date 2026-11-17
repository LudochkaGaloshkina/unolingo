package com.valek.service.learningservices;

import com.valek.model.learningmodels.CourseUnit;
import com.valek.repository.learningreps.CourseUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseUnitService {

    private final CourseUnitRepository courseUnitRepository;

    public List<CourseUnit> getUnitsByCourse(Long courseId) {
        return courseUnitRepository.findByCourse_IdOrderByPositionAsc(courseId);
    }
}