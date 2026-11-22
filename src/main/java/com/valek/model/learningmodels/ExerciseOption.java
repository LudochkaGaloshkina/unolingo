
package com.valek.model.learningmodels;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exercise_options")
@Getter
@Setter
public class ExerciseOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_text", nullable = false)
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(nullable = false)
    private int position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;
}
