package com.valek.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
public class UserDailyStatsId implements Serializable {

    private Long userId;

    private LocalDate activityDate;
}
