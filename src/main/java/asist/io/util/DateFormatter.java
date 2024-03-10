package asist.io.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class DateFormatter  {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate stringToLocalDate(String date){
        return LocalDate.parse(date, DATE_FORMAT);
    }

    public static String localDateToString(LocalDate date){
        return date.format(DATE_FORMAT);
    }
}