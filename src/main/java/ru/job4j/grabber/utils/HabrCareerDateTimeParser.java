package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        ZonedDateTime timeForWebSite = ZonedDateTime.parse(parse, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return timeForWebSite.toLocalDateTime();
    }
}
