package com.valek.service.learningservices;

import com.valek.model.learningmodels.Exercise;
import com.valek.repository.learningreps.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public List<Exercise> getExercisesByLesson(Long lessonId) {
        return exerciseRepository.findByLesson_IdOrderByPositionAsc(lessonId);
    }
}