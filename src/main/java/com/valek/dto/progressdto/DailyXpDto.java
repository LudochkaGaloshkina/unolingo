package com.valek.dto.progressdto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyXpDto {
    private LocalDate activityDate;
    private int xpEarned;
}