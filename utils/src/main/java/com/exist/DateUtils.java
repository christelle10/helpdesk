package com.exist;

import java.time.LocalDate;
import java.time.Period;

public class DateUtils {
    public static int calculateAge(LocalDate birthdate) {
        return birthdate != null ? Period.between(birthdate, LocalDate.now()).getYears() : 0;
    }
}