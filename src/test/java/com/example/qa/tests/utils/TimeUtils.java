package com.example.qa.tests.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    private static final DateTimeFormatter UI_DATE = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    public String convertUIDateToUnix(String date) {
        LocalDate dateFormated = LocalDate.parse(date, UI_DATE);

        return String.valueOf(dateFormated
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
    }

    public String convertUnixToUIDate(String date) {
        long timestampMs = Long.parseLong(date);

        Instant instant = Instant.ofEpochMilli(timestampMs);
        LocalDate newDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        return newDate.format(UI_DATE);
    }

    public String getUnixDateForApiAssertion() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        String todayFormatted = today.format(UI_DATE);
        return convertUIDateToUnix(todayFormatted);
    }
}
