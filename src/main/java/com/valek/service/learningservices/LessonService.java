package com.valek.service.learningservices;

import com.valek.model.learningmodels.Lesson;
import com.valek.repository.learningreps.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;

    public List<Lesson> getPublishedLessonsByUnit(Long unitId) {
        return lessonRepository.findByUnit_IdAndPublishedTrueOrderByPositionAsc(unitId);
    }
}