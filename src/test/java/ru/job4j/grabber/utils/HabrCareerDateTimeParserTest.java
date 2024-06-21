package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import java.time.LocalDateTime;

class HabrCareerDateTimeParserTest {

    @Test
    void parseFirst() {
        String date = "2024-06-18T18:27:27+03:00";
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        LocalDateTime result = parser.parse(date);
        assertThat(result).isEqualTo("2024-06-18T18:27:27");
    }

    @Test
    void parseSecond() {
        String date = "2024-06-21T10:22:20+07:00";
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        LocalDateTime result = parser.parse(date);
        assertThat(result).isEqualTo("2024-06-21T10:22:20");
    }
}