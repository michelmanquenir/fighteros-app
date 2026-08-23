package com.killerdev.fighteros_app.util;

import com.killerdev.fighteros_app.model.evento.Evento;

import java.time.LocalDateTime;
import java.time.LocalTime;

// Cálculo compartido de cuándo cierran las inscripciones de un evento:
// automáticamente 8 horas antes de la fecha/hora del evento (si no se
// definió hora, se usa las 00:00 del día del evento como referencia).
public final class EventoUtils {

    public static final long HORAS_CIERRE_INSCRIPCIONES = 8;

    private EventoUtils() {
    }

    public static boolean inscripcionesCerradas(Evento evento) {
        LocalDateTime fechaHoraEvento = LocalDateTime.of(
                evento.getFecha(),
                evento.getHora() != null ? evento.getHora() : LocalTime.MIDNIGHT);
        LocalDateTime cierre = fechaHoraEvento.minusHours(HORAS_CIERRE_INSCRIPCIONES);
        return !LocalDateTime.now().isBefore(cierre);
    }
}
