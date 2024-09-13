package asist.io.util;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class Constantes {
    public static final String CONTRASENA_PATTERN = "(?=.*[a-zA-Z])(?=.*[@#$%^&+=.])(?=\\S+$).{5,}$";
    public static final String CORREO_SETEADO = "poo2023correo@gmail.com";

    public static final Map<String, DayOfWeek> DIAS_DE_LA_SEMANA = new HashMap<>();
    static {
        DIAS_DE_LA_SEMANA.put("LUNES", DayOfWeek.MONDAY);
        DIAS_DE_LA_SEMANA.put("MARTES", DayOfWeek.TUESDAY);
        DIAS_DE_LA_SEMANA.put("MIERCOLES", DayOfWeek.WEDNESDAY);
        DIAS_DE_LA_SEMANA.put("JUEVES", DayOfWeek.THURSDAY);
        DIAS_DE_LA_SEMANA.put("VIERNES", DayOfWeek.FRIDAY);
        DIAS_DE_LA_SEMANA.put("SABADO", DayOfWeek.SATURDAY);
        DIAS_DE_LA_SEMANA.put("DOMINGO", DayOfWeek.SUNDAY);
    }

    public static final Map<DayOfWeek, String> DIAS_DE_LA_SEMANA_INVERSO = new HashMap<>();
    static {
        DIAS_DE_LA_SEMANA_INVERSO.put(DayOfWeek.MONDAY, "LUNES");
        DIAS_DE_LA_SEMANA_INVERSO.put(DayOfWeek.TUESDAY, "MARTES");
        DIAS_DE_LA_SEMANA_INVERSO.put(DayOfWeek.WEDNESDAY, "MIERCOLES");
        DIAS_DE_LA_SEMANA_INVERSO.put(DayOfWeek.THURSDAY, "JUEVES");
        DIAS_DE_LA_SEMANA_INVERSO.put(DayOfWeek.FRIDAY, "VIERNES");
        DIAS_DE_LA_SEMANA_INVERSO.put(DayOfWeek.SATURDAY, "SABADO");
        DIAS_DE_LA_SEMANA_INVERSO.put(DayOfWeek.SUNDAY, "DOMINGO");
    }
}
