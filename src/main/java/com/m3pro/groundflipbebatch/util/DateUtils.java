package com.m3pro.groundflipbebatch.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;

public class DateUtils {
	public static int getWeekOfDate(LocalDate date) {
		WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
		return date.get(weekFields.weekOfWeekBasedYear());
	}
}
