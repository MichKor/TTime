package pl.com.tt.ttime.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HolidayUtils {
    //Source: https://dzone.com/articles/algorithm-calculating-date
    public static LocalDate getEasterDay(int year) {
        int Y = year;
        int a = Y % 19;
        int b = Y / 100;
        int c = Y % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int L = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * L) / 451;
        int month = (h + L - 7 * m + 114) / 31;
        int day = ((h + L - 7 * m + 114) % 31) + 1;
        return LocalDate.of(year, month, day);
    }

    public static List<LocalDate> getStaticHolidays(int year) {
        return Arrays.asList(
            LocalDate.of(year, 1, 1), //Nowy Rok
                LocalDate.of(year, 1, 6), //Trzech Króli
                LocalDate.of(year, 5, 1), //Święto Pracy
                LocalDate.of(year, 5, 3), //Trzeciego Maja
                LocalDate.of(year, 8, 15), //Wniebowzięcie Najświętszej Marii Panny
                LocalDate.of(year, 11, 1), //Święto Zmarłych
                LocalDate.of(year, 11, 11), //Dzień Niepodległości
                LocalDate.of(year, 12, 25),
                LocalDate.of(year, 12, 26) //Boże Narodzenie
        );
    }

    public static List<LocalDate> getDynamicHolidaysBasedOffEaster(int year) {
        ArrayList<LocalDate> holidays = new ArrayList<>(4);
        LocalDate easter = getEasterDay(year);
        holidays.add(easter); //Niedziela Wielkanocna
        holidays.add(easter.plusDays(1)); //Poniedziałek Wielkanocny
        holidays.add(easter.plusDays(49)); //Zielone Świątki
        holidays.add(easter.plusDays(60)); //Boże Ciało
        return holidays;
    }

    public static List<LocalDate> getHolidaysForYear(int year) {
        return Stream.concat(getStaticHolidays(year).stream(), getDynamicHolidaysBasedOffEaster(year).stream()).sorted()
                .collect(Collectors.toList());
    }
}
