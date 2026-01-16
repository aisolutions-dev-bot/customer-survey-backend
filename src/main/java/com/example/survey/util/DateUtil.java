package com.example.survey.util;

import java.time.*;

public class DateUtil {

  public static LocalDateTime startOfYearSGT() {
    ZoneId sgt = ZoneId.of("Asia/Singapore");
    return LocalDate.now(sgt).withDayOfYear(1).atStartOfDay();
  }

  public static LocalDateTime nowSGT() {
    return LocalDateTime.now(ZoneId.of("Asia/Singapore"));
  }
}
