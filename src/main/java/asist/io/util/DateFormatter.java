package asist.io.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import asist.io.exception.ModelException;

@Component
public class DateFormatter  {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

    public static LocalDate stringToLocalDate(String date){
        validarDate(date);
        try{
            return LocalDate.parse(date, DATE_FORMAT);
        }catch(Exception e){
            throw new ModelException("La fecha no tiene el formato correcto");
        }
    }

    public static String localDateToString(LocalDate date){
        validarDate(date.toString());
        return date.format(DATE_FORMAT);
    }

    public static String localDateTimeToString(LocalDateTime date){
        return date.format(LOCAL_DATE_TIME_FORMAT);
    }

    public static void validarDate(String date){
        if(date == null || date.isEmpty() || date.isBlank()){
            throw new ModelException("La fecha no puede ser nula o vacía");
        }
    }
}