
package com.valek.service.learningservices;

import com.valek.dto.learningdto.ExerciseCatalogDto;
import com.valek.model.learningmodels.Lesson;
import com.valek.repository.learningreps.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;

    public List<Lesson> getPublishedLessonsByUnit(Long unitId) {
        return lessonRepository.findByUnit_IdAndPublishedTrueOrderByPositionAsc(unitId);
    }

    @Transactional(readOnly = true)
    public List<ExerciseCatalogDto> getAllPublishedLessons() {

        List<Lesson> lessons = lessonRepository.findByPublishedTrueOrderByPositionAsc();

        return lessons.stream()
                .map(lesson -> new ExerciseCatalogDto(
                        lesson.getId(),
                        lesson.getTitle(),
                        lesson.getDescription(),
                        lesson.getUnit().getTitle(),
                        lesson.getUnit().getCourse().getTitle(),
                        String.valueOf(lesson.getUnit().getCourse().getLevel())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public Lesson getPublishedLessonById(Long lessonId) {
        return lessonRepository.findByIdAndPublishedTrue(lessonId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Урок не найден или не опубликован")
                );
    }

    @Transactional(readOnly = true)
    public boolean isLessonUnlocked(Long lessonId, Set<Long> completedLessonIds) {

        Lesson targetLesson = lessonRepository.findByIdAndPublishedTrue(lessonId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Урок не найден или не опубликован")
                );

        Long courseId = targetLesson.getUnit().getCourse().getId();

        List<Lesson> courseLessons = lessonRepository.findByPublishedTrueOrderByPositionAsc()
                .stream()
                .filter(lesson ->
                        lesson.getUnit().getCourse().getId().equals(courseId)
                )
                .sorted(
                        Comparator
                                .comparing((Lesson lesson) -> lesson.getUnit().getPosition())
                                .thenComparing(Lesson::getPosition)
                )
                .toList();

        boolean previousLessonsCompleted = true;

        for (Lesson lesson : courseLessons) {

            if (lesson.getId().equals(lessonId)) {
                return previousLessonsCompleted
                        || completedLessonIds.contains(lessonId);
            }

            boolean isCompleted = completedLessonIds.contains(lesson.getId());

            previousLessonsCompleted =
                    previousLessonsCompleted && isCompleted;
        }

        return false;
    }
}
