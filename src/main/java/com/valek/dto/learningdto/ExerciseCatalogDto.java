package com.valek.dto.learningdto;

public record ExerciseCatalogDto(
        Long lessonId,
        String lessonTitle,
        String lessonDescription,
        String unitTitle,
        String courseTitle,
        String courseLevel
) {}