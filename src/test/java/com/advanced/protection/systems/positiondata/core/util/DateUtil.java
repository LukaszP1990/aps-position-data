package com.advanced.protection.systems.positiondata.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtil {

	public static Date getRegularDate() {
		return new GregorianCalendar(2020, Calendar.JUNE, 2).getTime();
	}

	public static Date getRegularDateFrom() {
		return new GregorianCalendar(2020, Calendar.JUNE, 1).getTime();
	}

	public static Date getRegularDateTo() {
		return new GregorianCalendar(2020, Calendar.JUNE, 3).getTime();
	}
}
