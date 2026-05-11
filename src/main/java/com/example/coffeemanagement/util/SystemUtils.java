package com.example.coffeemanagement.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SystemUtils {

    public static String dateToString(LocalDateTime date, String pattern){
        return date == null ? null :
                date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String bigDecimalToString(BigDecimal amount, Locale locale){
        if (amount == null) return "0";
        NumberFormat formatter = NumberFormat.getInstance(locale);
        return formatter.format(amount);
    }

    // "1,000,000" hoặc "1.000.000" -> BigDecimal
}
