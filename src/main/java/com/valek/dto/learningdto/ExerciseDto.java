package com.valek.dto.learningdto;

import java.util.List;

public record ExerciseDto(
        Long id,
        String type,
        String question,
        String explanation,
        int points,
        int position,
        List<ExerciseOptionDto> options
) {
}